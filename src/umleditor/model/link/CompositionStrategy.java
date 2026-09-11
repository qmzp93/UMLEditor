package umleditor.model.link;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class CompositionStrategy implements ArrowStrategy {
    @Override
    public void drawArrowHead(Graphics g, Point p, double angle, int size) {
        Polygon poly = new Polygon();
        poly.addPoint(p.x, p.y);
        poly.addPoint((int)(p.x - size * Math.cos(angle - 0.5)), (int)(p.y - size * Math.sin(angle - 0.5)));
        poly.addPoint((int)(p.x - 2 * size * Math.cos(angle)), (int)(p.y - 2 * size * Math.sin(angle)));
        poly.addPoint((int)(p.x - size * Math.cos(angle + 0.5)), (int)(p.y - size * Math.sin(angle + 0.5)));
        
        g.setColor(Color.WHITE);
        g.fillPolygon(poly);
        g.setColor(Color.BLACK);
        g.drawPolygon(poly);
    }
}