public enum Category {

    ELECTRONICS("Electronics", "images/gadgets.png"),
    STATIONERY ("Stationery",  "images/stationery.png"),
    ACCESSORIES("Accessories", "images/accessories.png"),
    SPORTS     ("Sports",      "images/sports.png");

    public final String label;
    public final String iconPath;

    Category(String label, String iconPath) {
        this.label    = label;
        this.iconPath = iconPath;
    }

    @Override public String toString() { return label; }
}
