package umleditor.model.shape;

import java.awt.Point;

// 將「連接點」抽象成一個獨立的領域物件 (Domain Object)
public class Port {
    private int x, y;
    private static final int PORT_SIZE = 13;
    // 預先算好感應範圍的偏移量，提升效能
    private static final int OFFSET = PORT_SIZE / 2 + 3;

    public Port(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // --- 資訊專家模式：讓 Port 自己決定滑鼠有沒有點到它 ---
    public boolean contains(Point p) {
        boolean inX = (p.x >= x - OFFSET && p.x <= x + OFFSET);
        boolean inY = (p.y >= y - OFFSET && p.y <= y + OFFSET);
        return inX && inY;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return PORT_SIZE; }
    
    public Point getLocation() { 
        return new Point(x, y); 
    }
}