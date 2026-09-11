package umleditor.model.objects;

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
        
        // 矩形有 8 個連接點 (四個角 + 四邊中點)
        ports.add(new Port(x + w / 2, y));             // 0: 上中
        ports.add(new Port(x + w / 2, y + h));         // 1: 下中
        ports.add(new Port(x, y + h / 2));             // 2: 左中
        ports.add(new Port(x + w, y + h / 2));         // 3: 右中
        ports.add(new Port(x, y));                     // 4: 左上
        ports.add(new Port(x + w, y));                 // 5: 右上
        ports.add(new Port(x, y + h));                 // 6: 左下
        ports.add(new Port(x + w, y + h));             // 7: 右下
        return ports;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillRect(getX(), getY(), getWidth(), getHeight());    // 填滿背景色
        g.setColor(Color.BLACK);
        g.drawRect(getX(), getY(), getWidth(), getHeight());    // 繪製邊框
        drawLabel(g);
    }
}