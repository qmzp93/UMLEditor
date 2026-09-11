package umleditor.model;

// 專屬於 ModeFactory 的型別
public enum ModeType implements Tool {
    SELECT("Select"),
    ASSOCIATION("Association"),
    GENERALIZATION("Generalization"),
    COMPOSITION("Composition");

    private final String displayName;

    ModeType(String displayName) { this.displayName = displayName; }
    
    @Override
    public String getDisplayName() { return displayName; }
}