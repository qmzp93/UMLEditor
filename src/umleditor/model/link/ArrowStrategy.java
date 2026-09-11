package umleditor.model.link;

import java.awt.Graphics;
import java.awt.Point;

// 箭頭繪製的策略介面
public interface ArrowStrategy {
    void drawArrowHead(Graphics g, Point p, double angle, int size);
}