package umleditor.model.shape;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class RectObject extends BaseObject {
    
    public RectObject(int x, int y) { 
        super(x, y, 100, 100); 
    }
    
    @Override
    public List<Port> getPorts() {
        List<Port> ports = new ArrayList<>();
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        
        ports.add(new Port(x, y));                     // 左上
        ports.add(new Port(x + w, y));                 // 右上
        ports.add(new Port(x, y + h));                 // 左下
        ports.add(new Port(x + w, y + h));             // 右下
        ports.add(new Port(x + w / 2, y));             // 上中
        ports.add(new Port(x + w / 2, y + h));         // 下中
        ports.add(new Port(x, y + h / 2));             // 左中
        ports.add(new Port(x + w, y + h / 2));         // 右中
        return ports;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillRect(getX(), getY(), getWidth(), getHeight());    // 填滿背景色
        g.setColor(Color.BLACK);
        g.drawRect(getX(), getY(), getWidth(), getHeight());    // 繪製邊框
        drawPorts(g);
        drawLabel(g);
    }
}