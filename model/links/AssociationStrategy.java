package umleditor.model.links;

import java.awt.Graphics;
import java.awt.Point;

public class AssociationStrategy implements LinkStrategy {
    @Override
    public void drawArrowHead(Graphics g, Point endPoint, double angle, int arrowSize) {
        int x1 = (int) (endPoint.x - arrowSize * Math.cos(angle - 0.5));
        int y1 = (int) (endPoint.y - arrowSize * Math.sin(angle - 0.5));
        int x2 = (int) (endPoint.x - arrowSize * Math.cos(angle + 0.5));
        int y2 = (int) (endPoint.y - arrowSize * Math.sin(angle + 0.5));

        g.drawLine(endPoint.x, endPoint.y, x1, y1);
        g.drawLine(endPoint.x, endPoint.y, x2, y2);
    }
}