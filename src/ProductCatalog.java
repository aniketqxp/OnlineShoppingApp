import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductCatalog {

    private static final List<Product> ALL = new ArrayList<>();

    static {
        ALL.add(new Product("Phone", 299.99, "images/phone.png", Category.ELECTRONICS,
                "Sleek smartphone with 6.5\" OLED display, 128 GB storage, and a 12 MP dual camera.", 4.5, 15));
        ALL.add(new Product("Tablet", 199.99, "images/tablet.png", Category.ELECTRONICS,
                "10\" HD tablet perfect for work and entertainment. 64 GB with expandable storage.", 4.2, 10));
        ALL.add(new Product("Laptop", 999.99, "images/laptop.png", Category.ELECTRONICS,
                "Lightweight 14\" laptop - Intel i5, 16 GB RAM, 512 GB SSD, all-day battery life.", 4.7, 8));
        ALL.add(new Product("Camera", 499.99, "images/camera.png", Category.ELECTRONICS,
                "24 MP mirrorless camera with interchangeable lenses and 4K video recording.", 4.4, 6));
        ALL.add(new Product("TV", 799.99, "images/tv.png", Category.ELECTRONICS,
                "55\" 4K Smart TV with HDR, built-in streaming apps, and hands-free voice control.", 4.6, 5));
        ALL.add(new Product("Console", 399.99, "images/game.png", Category.ELECTRONICS,
                "Next-gen gaming console - 1 TB storage, 4K gaming, and full online multiplayer.", 4.8, 12));

        ALL.add(new Product("Notebook", 4.99, "images/notebook.png", Category.STATIONERY,
                "A5 hardcover lined notebook, 200 pages. Ideal for notes, journaling, and planning.", 4.3, 50));
        ALL.add(new Product("Pen", 1.49, "images/pen.png", Category.STATIONERY,
                "Smooth ballpoint pen with blue ink and an ergonomic grip for comfortable writing.", 4.0, 100));
        ALL.add(new Product("Pencil", 0.99, "images/pencil.png", Category.STATIONERY,
                "Pre-sharpened HB pencil. Ideal for sketching, drawing, and everyday writing.", 4.1, 200));
        ALL.add(new Product("Eraser", 0.79, "images/eraser.png", Category.STATIONERY,
                "Clean-erasing white rubber eraser. No smudges. Compatible with all pencil types.", 4.2, 150));
        ALL.add(new Product("Marker", 2.49, "images/marker.png", Category.STATIONERY,
                "Chisel-tip permanent marker. Waterproof, fade-resistant vibrant black ink.", 4.3, 80));
        ALL.add(new Product("Sharpener", 1.99, "images/sharpener.png", Category.STATIONERY,
                "Dual-hole metal sharpener with a precision blade for standard and jumbo pencils.", 4.0, 120));

        ALL.add(new Product("Hat", 19.99, "images/hat.png", Category.ACCESSORIES,
                "Classic 6-panel cotton cap with an adjustable strap. One size fits most.", 4.2, 30));
        ALL.add(new Product("Sunglasses", 34.99, "images/sunglasses.png", Category.ACCESSORIES,
                "UV400 polarized sunglasses. Lightweight frame, unisex style. Full eye protection.", 4.5, 25));
        ALL.add(new Product("Watch", 89.99, "images/watch.png", Category.ACCESSORIES,
                "Analog quartz watch with a stainless steel case. Water-resistant up to 30 m.", 4.6, 18));
        ALL.add(new Product("Gloves", 14.99, "images/gloves.png", Category.ACCESSORIES,
                "Touchscreen-compatible knit gloves. Warm, stretchy, and snug fit for cold weather.", 4.1, 40));
        ALL.add(new Product("Backpack", 49.99, "images/backpack.png", Category.ACCESSORIES,
                "20 L backpack with a padded laptop sleeve, multiple pockets, and a built-in USB port.", 4.7, 22));
        ALL.add(new Product("Scarf", 12.99, "images/scarf.png", Category.ACCESSORIES,
                "Soft knit winter scarf, 180 cm long. Neutral tones that pair with any outfit.", 4.0, 35));

        ALL.add(new Product("Football", 24.99, "images/football.png", Category.SPORTS,
                "Official size 5 football. Durable PU leather, machine-stitched for consistency.", 4.4, 20));
        ALL.add(new Product("Basketball", 29.99, "images/basketball.png", Category.SPORTS,
                "Official size 7 basketball. Deep channel design for superior grip and ball control.", 4.5, 18));
        ALL.add(new Product("Baseball", 9.99, "images/ball.png", Category.SPORTS,
                "Official weight and size baseball - cork centre, wool winding, cowhide cover.", 4.3, 30));
        ALL.add(new Product("Cricket Bat", 59.99, "images/cricket-bat.png", Category.SPORTS,
                "Full-size Grade 1 English willow cricket bat. Built for professional performance.", 4.6, 12));
        ALL.add(new Product("Tennis Racket", 44.99, "images/tennis-racket.png", Category.SPORTS,
                "Lightweight graphite racket, 270 g. Pre-strung with synthetic gut at 55 lbs.", 4.4, 15));
        ALL.add(new Product("Badminton Racket", 39.99, "images/badminton.png", Category.SPORTS,
                "Aluminum alloy badminton racket, balanced for control. Pre-strung nylon strings.", 4.3, 16));
    }

    public static List<Product> getAll() {
        return new ArrayList<>(ALL);
    }

    public static Product getByName(String name) {
        return ALL.stream().filter(p -> p.name.equals(name)).findFirst().orElse(null);
    }

    public static List<Product> getByCategory(Category category) {
        return ALL.stream()
                .filter(p -> p.category == category)
                .collect(Collectors.toList());
    }

    public static List<Product> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase().trim();
        return ALL.stream()
                .filter(p -> p.name.toLowerCase().contains(q)
                          || p.category.label.toLowerCase().contains(q)
                          || p.description.toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public static List<Product> sort(List<Product> products, SortOrder order) {
        List<Product> list = new ArrayList<>(products);
        if (order == SortOrder.PRICE_ASC) {
            list.sort(Comparator.comparingDouble(p -> p.price));
        } else if (order == SortOrder.PRICE_DESC) {
            list.sort((a, b) -> Double.compare(b.price, a.price));
        } else if (order == SortOrder.NAME_ASC) {
            list.sort(Comparator.comparing(p -> p.name));
        }
        return list;
    }
}
