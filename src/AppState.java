import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Application state singleton.
 *
 * Cart state is persisted in Redis (key: "vectorstore:cart:<sessionId>", type: Hash)
 * with a 24-hour TTL so it survives app restarts within the same day.
 *
 * If Redis is unavailable the cart falls back to an in-memory LinkedHashMap
 * so the app always works regardless of infrastructure.
 *
 * Wishlist, ratings, and purchase history remain in-memory only (non-critical state).
 */
public class AppState {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final int    CART_TTL_SECONDS = 86_400; // 24 hours
    private static final String SESSION_FILE     = System.getProperty("user.home") + "/.vectorstore_session";
    private static final Gson   GSON             = new GsonBuilder().create();

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static final AppState INSTANCE = new AppState();
    public static AppState get() { return INSTANCE; }

    // ── Session ───────────────────────────────────────────────────────────────
    private final String sessionId;
    private final String cartKey;

    // ── In-memory fallback cart (used when Redis is down) ─────────────────────
    private final Map<String, CartItem> localCart = new LinkedHashMap<>();

    // ── Non-cart state (always in-memory) ─────────────────────────────────────
    private final Set<String>               wishlist  = new HashSet<>();
    private final Set<String>               purchased = new HashSet<>();
    private final Map<String, Double>       ratings   = new HashMap<>();
    private final List<StateChangeListener> listeners = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────
    private AppState() {
        this.sessionId = loadOrCreateSession();
        this.cartKey   = "vectorstore:cart:" + sessionId;
        System.out.println("[AppState] Session: " + sessionId
                + " | Redis: " + (RedisClient.isAvailable() ? "ON" : "OFF (in-memory fallback)"));
        // Ensure UI listeners sync with Redis data on startup
        javax.swing.SwingUtilities.invokeLater(this::fire);
    }

    // ── Session persistence ───────────────────────────────────────────────────

    private String loadOrCreateSession() {
        Path path = Paths.get(SESSION_FILE);
        try {
            if (Files.exists(path)) {
                String id = Files.readString(path).trim();
                if (!id.isBlank()) return id;
            }
        } catch (IOException ignored) {}

        String newId = UUID.randomUUID().toString();
        try { Files.writeString(path, newId); } catch (IOException ignored) {}
        return newId;
    }

    // ── Cart (Redis-backed, with in-memory fallback) ──────────────────────────

    public void addToCart(Product product, int qty) {
        if (RedisClient.isAvailable()) {
            try (Jedis j = RedisClient.get()) {
                if (j != null) {
                    String existing = j.hget(cartKey, product.name);
                    int currentQty = 0;
                    if (existing != null) {
                        CartItemDto dto = GSON.fromJson(existing, CartItemDto.class);
                        currentQty = dto.quantity;
                    }
                    CartItemDto dto = new CartItemDto(product, currentQty + qty);
                    j.hset(cartKey, product.name, GSON.toJson(dto));
                    j.expire(cartKey, CART_TTL_SECONDS);
                    fire();
                    return;
                }
            } catch (Exception e) {
                System.err.println("[Redis] addToCart failed: " + e.getMessage());
            }
        }
        // Fallback
        CartItem existing = localCart.get(product.name);
        if (existing != null) existing.setQuantity(existing.getQuantity() + qty);
        else localCart.put(product.name, new CartItem(product, qty));
        fire();
    }

    public void removeFromCart(String productName) {
        if (RedisClient.isAvailable()) {
            try (Jedis j = RedisClient.get()) {
                if (j != null) {
                    j.hdel(cartKey, productName);
                    j.expire(cartKey, CART_TTL_SECONDS);
                    fire();
                    return;
                }
            } catch (Exception e) {
                System.err.println("[Redis] removeFromCart failed: " + e.getMessage());
            }
        }
        localCart.remove(productName);
        fire();
    }

    public void setCartQty(String productName, int qty) {
        if (qty <= 0) { removeFromCart(productName); return; }

        if (RedisClient.isAvailable()) {
            try (Jedis j = RedisClient.get()) {
                if (j != null) {
                    String existing = j.hget(cartKey, productName);
                    if (existing != null) {
                        CartItemDto dto = GSON.fromJson(existing, CartItemDto.class);
                        dto.quantity = qty;
                        j.hset(cartKey, productName, GSON.toJson(dto));
                        j.expire(cartKey, CART_TTL_SECONDS);
                    }
                    fire();
                    return;
                }
            } catch (Exception e) {
                System.err.println("[Redis] setCartQty failed: " + e.getMessage());
            }
        }
        CartItem item = localCart.get(productName);
        if (item != null) item.setQuantity(qty);
        fire();
    }

    public void clearCart() {
        if (RedisClient.isAvailable()) {
            try (Jedis j = RedisClient.get()) {
                if (j != null) {
                    j.del(cartKey);
                    fire();
                    return;
                }
            } catch (Exception e) {
                System.err.println("[Redis] clearCart failed: " + e.getMessage());
            }
        }
        localCart.clear();
        fire();
    }

    public Map<String, CartItem> getCart() {
        if (RedisClient.isAvailable()) {
            try (Jedis j = RedisClient.get()) {
                if (j != null) {
                    Map<String, String> raw = j.hgetAll(cartKey);
                    Map<String, CartItem> result = new LinkedHashMap<>();
                    for (Map.Entry<String, String> entry : raw.entrySet()) {
                        CartItemDto dto = GSON.fromJson(entry.getValue(), CartItemDto.class);
                        Product p = ProductCatalog.getByName(dto.productName);
                        if (p != null) result.put(dto.productName, new CartItem(p, dto.quantity));
                    }
                    return Collections.unmodifiableMap(result);
                }
            } catch (Exception e) {
                System.err.println("[Redis] getCart failed: " + e.getMessage());
            }
        }
        return Collections.unmodifiableMap(localCart);
    }

    public int getCartItemCount() {
        return getCart().values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getCartTotal() {
        return getCart().values().stream().mapToDouble(CartItem::getTotal).sum();
    }

    // ── Wishlist ──────────────────────────────────────────────────────────────

    public void toggleWishlist(String productName) {
        if (!wishlist.remove(productName)) wishlist.add(productName);
        fire();
    }
    public boolean isWishlisted(String productName) { return wishlist.contains(productName); }
    public Set<String> getWishlist() { return Collections.unmodifiableSet(wishlist); }

    // ── Purchases & Ratings ───────────────────────────────────────────────────

    public void markAsPurchased(String productName)  { purchased.add(productName); fire(); }
    public void markMultiplePurchased(java.util.Collection<String> names) { purchased.addAll(names); fire(); }
    public boolean isPurchased(String productName)   { return purchased.contains(productName); }
    public Set<String> getPurchased()                { return Collections.unmodifiableSet(purchased); }

    public void rateProduct(String productName, double rating) {
        ratings.put(productName, Math.max(0, Math.min(5, rating)));
        fire();
    }
    public double  getProductRating(String name) { return ratings.getOrDefault(name, 0.0); }
    public boolean hasRated(String name)         { return ratings.containsKey(name); }

    // ── Observer ──────────────────────────────────────────────────────────────

    public void addListener(StateChangeListener l)    { listeners.add(l); }
    public void removeListener(StateChangeListener l) { listeners.remove(l); }
    private void fire() { new ArrayList<>(listeners).forEach(StateChangeListener::onStateChanged); }

    // ── Inner DTO (serialized to/from Redis) ──────────────────────────────────

    /** Lightweight DTO stored as JSON in Redis. Only contains serializable primitives. */
    private static class CartItemDto {
        String productName;
        double price;
        int    quantity;

        CartItemDto(Product p, int qty) {
            this.productName = p.name;
            this.price       = p.price;
            this.quantity    = qty;
        }
    }
}
