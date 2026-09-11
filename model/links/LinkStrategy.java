package umleditor.model.links;

import java.awt.Graphics;
import java.awt.Point;

// 箭頭繪製的策略介面
public interface LinkStrategy {
    void drawArrowHead(Graphics g, Point endPoint, double angle, int arrowSize);
}