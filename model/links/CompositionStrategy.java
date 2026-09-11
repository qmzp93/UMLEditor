package umleditor.model.links;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class CompositionStrategy implements LinkStrategy {
    @Override
    public void drawArrowHead(Graphics g, Point endPoint, double angle, int arrowSize) {
        Polygon diamond = new Polygon();
        diamond.addPoint(endPoint.x, endPoint.y);
        diamond.addPoint((int)(endPoint.x - arrowSize * Math.cos(angle - 0.5)), (int)(endPoint.y - arrowSize * Math.sin(angle - 0.5)));
        diamond.addPoint((int)(endPoint.x - 2 * arrowSize * Math.cos(angle)), (int)(endPoint.y - 2 * arrowSize * Math.sin(angle)));
        diamond.addPoint((int)(endPoint.x - arrowSize * Math.cos(angle + 0.5)), (int)(endPoint.y - arrowSize * Math.sin(angle + 0.5)));
        
        g.setColor(Color.WHITE);
        g.fillPolygon(diamond);
        g.setColor(Color.BLACK);
        g.drawPolygon(diamond);
    }
}