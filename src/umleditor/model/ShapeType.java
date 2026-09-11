package umleditor.model;

// 專屬於 ShapeFactory 的型別
public enum ShapeType implements Tool {
    RECT("Rect"),
    OVAL("Oval");

    private final String displayName;

    ShapeType(String displayName) { this.displayName = displayName; }

    @Override
    public String getDisplayName() { return displayName; }
}