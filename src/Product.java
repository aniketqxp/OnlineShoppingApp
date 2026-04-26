public class Product {

    public final String   name;
    public final double   price;
    public final String   imagePath;
    public final Category category;
    public final String   description;
    public final double   rating;
    public final int      stock;

    public Product(String name, double price, String imagePath, Category category,
                   String description, double rating, int stock) {
        this.name        = name;
        this.price       = price;
        this.imagePath   = imagePath;
        this.category    = category;
        this.description = description;
        this.rating      = rating;
        this.stock       = stock;
    }

    public String formattedPrice() {
        return String.format("$%.2f", price);
    }
}
