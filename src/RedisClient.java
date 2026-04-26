import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Singleton Redis connection pool.
 * Connects to localhost:6379 with a 5-second timeout.
 * If Redis is unavailable the pool will be null and AppState
 * falls back to in-memory cart state gracefully.
 */
public class RedisClient {

    private static final String HOST    = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : "localhost";
    private static final int    PORT    = System.getenv("REDIS_PORT") != null ? Integer.parseInt(System.getenv("REDIS_PORT")) : 6379;
    private static final int    TIMEOUT = 5_000;

    private static final JedisPool pool;

    static {
        JedisPool tmp = null;
        try {
            tmp = new JedisPool(HOST, PORT);
            // Verify connectivity immediately
            try (Jedis j = tmp.getResource()) {
                j.ping();
            }
            System.out.println("[Redis] Connected to " + HOST + ":" + PORT);
        } catch (Exception e) {
            System.err.println("[Redis] WARNING: unavailable – using in-memory fallback. " + e.getMessage());
            tmp = null;
        }
        pool = tmp;
    }

    /** Returns a Jedis resource from the pool, or null if Redis is unavailable. */
    public static Jedis get() {
        if (pool == null) return null;
        try {
            return pool.getResource();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isAvailable() {
        return pool != null;
    }
}
