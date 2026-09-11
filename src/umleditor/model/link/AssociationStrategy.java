package umleditor.model.link;

import java.awt.Graphics;
import java.awt.Point;

public class AssociationStrategy implements ArrowStrategy {
    @Override
    public void drawArrowHead(Graphics g, Point p, double angle, int size) {
        int x1 = (int) (p.x - size * Math.cos(angle - 0.5));
        int y1 = (int) (p.y - size * Math.sin(angle - 0.5));
        int x2 = (int) (p.x - size * Math.cos(angle + 0.5));
        int y2 = (int) (p.y - size * Math.sin(angle + 0.5));

        g.drawLine(p.x, p.y, x1, y1);
        g.drawLine(p.x, p.y, x2, y2);
    }
}