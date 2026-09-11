package umleditor.model;

// 專屬於 ObjectFactory 的型別
public enum ObjectType implements ToolType {
    RECT("Rect"),
    OVAL("Oval");

    private final String displayName;

    ObjectType(String displayName) { this.displayName = displayName; }

    @Override
    public String getDisplayName() { return displayName; }
}