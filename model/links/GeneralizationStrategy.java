package umleditor.model.links;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class GeneralizationStrategy implements LinkStrategy {
    @Override
    public void drawArrowHead(Graphics g, Point endPoint, double angle, int arrowSize) {
        Polygon triangle = new Polygon();
        triangle.addPoint(endPoint.x, endPoint.y);
        triangle.addPoint((int)(endPoint.x - arrowSize * Math.cos(angle - 0.5)), (int)(endPoint.y - arrowSize * Math.sin(angle - 0.5)));
        triangle.addPoint((int)(endPoint.x - arrowSize * Math.cos(angle + 0.5)), (int)(endPoint.y - arrowSize * Math.sin(angle + 0.5)));
        
        g.setColor(Color.WHITE);
        g.fillPolygon(triangle);
        g.setColor(Color.BLACK);
        g.drawPolygon(triangle);
    }
}