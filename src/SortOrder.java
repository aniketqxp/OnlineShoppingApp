public enum SortOrder {

    DEFAULT   ("Default"),
    PRICE_ASC ("Price: Low -> High"),
    PRICE_DESC("Price: High -> Low"),
    NAME_ASC  ("Name: A -> Z");

    public final String label;

    SortOrder(String label) { this.label = label; }

    @Override public String toString() { return label; }
}
