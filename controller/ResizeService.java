package umleditor.controller;

import java.awt.Point;
import java.awt.Rectangle;

import umleditor.model.objects.BaseObject;

// ResizeService: 封裝縮放錨點與變形計算，供 SelectMode 使用
public final class ResizeService {
    private static final int DEFAULT_MIN_SIZE = 50;

    private ResizeService() {}

    public static Point calculateFixedPoint(Rectangle b, ResizeHandle handle) {
        int px = b.x, py = b.y, w = b.width, h = b.height;
        switch (handle) {
            case TOP_MIDDLE: return new Point(px + w / 2, py + h); // 上中 -> 固定下中
            case BOTTOM_MIDDLE: return new Point(px + w / 2, py);  // 下中 -> 固定上中
            case LEFT_MIDDLE: return new Point(px + w, py + h / 2);// 左中 -> 固定右中
            case RIGHT_MIDDLE: return new Point(px, py + h / 2);   // 右中 -> 固定左中
            case TOP_LEFT: return new Point(px + w, py + h);       // 左上 -> 固定右下
            case TOP_RIGHT: return new Point(px, py + h);          // 右上 -> 固定左下
            case BOTTOM_LEFT: return new Point(px + w, py);        // 左下 -> 固定右上
            case BOTTOM_RIGHT: return new Point(px, py);           // 右下 -> 固定左上
            default: return new Point(px, py);
        }
    }

    public static void performResize(BaseObject targetObject, ResizeHandle handle, Point fixedPoint, Point mousePos) {
        int mx = mousePos.x, my = mousePos.y;
        int fx = fixedPoint.x, fy = fixedPoint.y;

        int newW = Math.max(DEFAULT_MIN_SIZE, Math.abs(mx - fx));
        int newH = Math.max(DEFAULT_MIN_SIZE, Math.abs(my - fy));

        Rectangle b = targetObject.getBounds();
        int newX = b.x, newY = b.y;

        if (handle.isVerticalEdge()) {
            // 垂直縮放：保持 fixedPoint.y 作為固定邊，允許交叉反向拖曳
            newY = (my <= fy) ? (fy - newH) : fy;
            targetObject.setBounds(new Rectangle(b.x, newY, b.width, newH));
        } else if (handle.isHorizontalEdge()) {
            // 水平縮放：保持 fixedPoint.x 作為固定邊，允許交叉反向拖曳
            newX = (mx <= fx) ? (fx - newW) : fx;
            targetObject.setBounds(new Rectangle(newX, b.y, newW, b.height));
        } else {
            // 角點縮放：X/Y 皆以固定錨點重新計算，避免最小尺寸時發生滑動
            newX = (mx <= fx) ? (fx - newW) : fx;
            newY = (my <= fy) ? (fy - newH) : fy;
            targetObject.setBounds(new Rectangle(newX, newY, newW, newH));
        }
    }
}
