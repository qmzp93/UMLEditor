package umleditor.model.objects;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class OvalObject extends BaseObject {
    
    public OvalObject(int x, int y) { 
        super(x, y, 100, 80); // 橢圓預設建立為 100x80 (稍扁)
    }
    
    @Override
    public List<Port> getPorts() {
        List<Port> ports = new ArrayList<>();
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();

        // 橢圓規格要求僅有 4 個連接點 (上下左右中點)
        ports.add(new Port(x + w / 2, y));             // 上
        ports.add(new Port(x + w / 2, y + h));         // 下
        ports.add(new Port(x, y + h / 2));             // 左
        ports.add(new Port(x + w, y + h / 2));         // 右
        return ports;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(getX(), getY(), getWidth(), getHeight());    // 填滿橢圓背景
        g.setColor(Color.BLACK);
        g.drawOval(getX(), getY(), getWidth(), getHeight());    // 繪製橢圓邊框
        drawLabel(g);
    }
}