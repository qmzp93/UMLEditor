package umleditor.model.shape;

import umleditor.model.ShapeType;

public class ShapeFactory {
    public static BaseObject createShape(ShapeType type, int x, int y) {
        switch (type) {
            case RECT:
                return new RectObject(x, y);
            case OVAL:
                return new OvalObject(x, y);
            default:
                throw new IllegalArgumentException("未知的圖形類型: " + type);
        }
    }
}