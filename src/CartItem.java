public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product  = product;
        this.quantity = quantity;
    }

    public Product getProduct()  { return product; }
    public String  getItemName() { return product.name; }
    public double  getPrice()    { return product.price; }
    public int     getQuantity() { return quantity; }
    public void    setQuantity(int quantity) { this.quantity = quantity; }
    public double  getTotal()    { return product.price * quantity; }
}
