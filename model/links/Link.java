package umleditor.model.links;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import umleditor.model.Drawable;
import umleditor.model.objects.BaseObject;

public class Link implements Drawable {
    private BaseObject startObj, endObj;
    private int startPortIndex, endPortIndex;
    private int arrowSize = 15;
    
    // 持有策略介面的引用
    private LinkStrategy arrowStrategy; 

    public Link(BaseObject startObj, int startPortIndex, BaseObject endObj, int endPortIndex, LinkStrategy strategy) {
        this.startObj = startObj;
        this.startPortIndex = startPortIndex;
        this.endObj = endObj;
        this.endPortIndex = endPortIndex;
        this.arrowStrategy = strategy;
    }

    public void draw(Graphics g) {
        Point startPoint = startObj.getPortLocation(startPortIndex);
        Point endPoint = endObj.getPortLocation(endPortIndex);

        g.setColor(Color.BLACK);
        g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);

        double angle = Math.atan2(endPoint.y - startPoint.y, endPoint.x - startPoint.x);
        
        // 多型呼叫，不管裡面是什麼策略---
        arrowStrategy.drawArrowHead(g, endPoint, angle, arrowSize);
    }
}