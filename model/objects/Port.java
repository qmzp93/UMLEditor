package umleditor.model.objects;

import java.awt.Point;
import java.awt.Graphics;

// 代表 UML 圖形四周用來連接線條的「黑色小方塊」
public class Port {
    private int x, y;   // 中心點座標
    private static final int PORT_SIZE = 12;
    private static final int OFFSET = PORT_SIZE / 2;    // 中心點到邊緣的距離

    public Port(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 判斷滑鼠是否點擊到這個小方塊的範圍內 (正負 OFFSET 的正方形區域)
    public boolean contains(Point mousePoint) {
        boolean inX = (mousePoint.x >= x - OFFSET && mousePoint.x <= x + OFFSET);
        boolean inY = (mousePoint.y >= y - OFFSET && mousePoint.y <= y + OFFSET);
        return inX && inY;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return PORT_SIZE; }
    
    public Point getLocation() { 
        return new Point(x, y); 
    }

    public void draw(Graphics g) {
        g.fillRect(x - OFFSET, y - OFFSET, PORT_SIZE, PORT_SIZE);
    }
}