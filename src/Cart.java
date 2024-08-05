import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<String> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public void addItem(String item) {
        items.add(item);
    }

    public List<String> getItems() {
        return new ArrayList<>(items); // Return a copy to avoid external modifications
    }

    public void clear() {
        items.clear();
    }
}