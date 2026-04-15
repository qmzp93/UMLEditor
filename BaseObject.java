import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// 所有畫布物件的基類
abstract class BaseObject {
    private int x, y, width, height;
    private String label = "";
    private boolean isSelected = false;
    private boolean isHovered = false;
    private final int PORT_SIZE = 11;
    private Color color = Color.LIGHT_GRAY;


    public BaseObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // --- Getters & Setters ---
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { this.isSelected = selected; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    
    public boolean isHovered() { return isHovered; }
    public void setHovered(boolean hovered) { this.isHovered = hovered; }

    // --- 繪製與邏輯 ---

    // 由子類別實作，回傳所有 Port 的中心座標
    public abstract List<Point> getPorts();

    // 繪製所有的 Ports 
    public void drawPorts(Graphics g) {
        if (!isSelected() && !isHovered) return;

        g.setColor(Color.BLACK);
        for (Point p : getPorts()) {
            // 以座標為中心繪製黑色小方塊
            g.fillRect(p.x - PORT_SIZE / 2, p.y - PORT_SIZE / 2, PORT_SIZE, PORT_SIZE);
        }
    }

    public abstract void draw(Graphics g);

    // 輔助方法：在物件中心繪製標籤 [cite: 145]
    protected void drawLabel(Graphics g) {
        if (label != null && !label.isEmpty()) {
            g.setColor(Color.BLACK);
            FontMetrics fm = g.getFontMetrics();
            int textX = getX() + (getWidth() - fm.stringWidth(label)) / 2;
            int textY = getY() + (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(label, textX, textY);
        }
    }
    
    // 檢查座標是否在物件內，用於 Use Case C (選取)
    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}

class RectObject extends BaseObject {
    public RectObject(int x, int y) { super(x, y, 100, 100); }
    
    @Override
    public List<Point> getPorts() {
        List<Point> ports = new ArrayList<>();
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        
        // 四個頂點
        ports.add(new Point(x, y));                     // 左上
        ports.add(new Point(x + w, y));             // 右上
        ports.add(new Point(x, y + h));            // 左下
        ports.add(new Point(x + w, y + h));    // 右下
        // 四邊中點
        ports.add(new Point(x + w / 2, y));         // 上中
        ports.add(new Point(x + w / 2, y + h));// 下中
        ports.add(new Point(x, y + h / 2));        // 左中
        ports.add(new Point(x + w, y + h / 2));// 右中
        return ports;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(getX(), getY(), getWidth(), getHeight());
        drawPorts(g); // 繪製選取狀態下的 Ports
        drawLabel(g); // 繪製文字
    }
}

class OvalObject extends BaseObject {
    public OvalObject(int x, int y) { super(x, y, 100, 80); }
    @Override
    public List<Point> getPorts() {
        List<Point> ports = new ArrayList<>();
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        ports.add(new Point(x + w / 2, y));          // 上
        ports.add(new Point(x + w / 2, y + h)); // 下
        ports.add(new Point(x, y + h / 2));         // 左
        ports.add(new Point(x + w, y + h / 2)); // 右
        return ports;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(getX(), getY(), getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawOval(getX(), getY(), getWidth(), getHeight());
        drawPorts(g); // 繪製選取狀態下的 Ports
        drawLabel(g); // 繪製文字
    }
}