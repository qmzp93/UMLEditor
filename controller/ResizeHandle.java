package umleditor.controller;

// ResizeHandle: 用語意化型別取代裸 int port index
public enum ResizeHandle {
    TOP_MIDDLE(0),
    BOTTOM_MIDDLE(1),
    LEFT_MIDDLE(2),
    RIGHT_MIDDLE(3),
    TOP_LEFT(4),
    TOP_RIGHT(5),
    BOTTOM_LEFT(6),
    BOTTOM_RIGHT(7);

    private final int index;

    ResizeHandle(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static ResizeHandle fromIndex(int index) {
        for (ResizeHandle handle : values()) {
            if (handle.index == index) return handle;
        }
        return null;
    }

    public boolean isVerticalEdge() {
        return this == TOP_MIDDLE || this == BOTTOM_MIDDLE;
    }

    public boolean isHorizontalEdge() {
        return this == LEFT_MIDDLE || this == RIGHT_MIDDLE;
    }
}
