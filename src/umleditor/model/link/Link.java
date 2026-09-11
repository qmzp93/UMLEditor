package umleditor.model.link;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import umleditor.model.shape.BaseObject;
import umleditor.model.Drawable;

public class Link implements Drawable {
    private BaseObject startObj, endObj;
    private int startPortIndex, endPortIndex;
    
    // --- 關鍵：持有策略介面的引用，而不是字串 ---
    private ArrowStrategy arrowStrategy; 

    public Link(BaseObject startObj, int startPortIndex, BaseObject endObj, int endPortIndex, ArrowStrategy strategy) {
        this.startObj = startObj;
        this.startPortIndex = startPortIndex;
        this.endObj = endObj;
        this.endPortIndex = endPortIndex;
        this.arrowStrategy = strategy;
    }

    public void draw(Graphics g) {
        Point p1 = startObj.getPortLocation(startPortIndex);
        Point p2 = endObj.getPortLocation(endPortIndex);

        g.setColor(Color.BLACK);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);

        double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        
        // --- 關鍵：多型呼叫，不管裡面是什麼策略，畫就對了 ---
        arrowStrategy.drawArrowHead(g, p2, angle, 15);
    }
}