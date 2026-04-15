import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// 所有畫布物件的抽象基類，定義了 UML 物件的核心數據與共用行為
abstract class BaseObject {
    private int x, y, width, height;
    private String label = "";
    private boolean isSelected = false;
    private boolean isHovered = false;
    private final int PORT_SIZE = 13;
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

    public int getPortSize() { return PORT_SIZE; }

    // --- 抽象方法：強制子類別實作特定的行為 ---

    // 回傳該形狀所有的連接點 (Ports) 座標 
    public abstract List<Point> getPorts();

    // 定義形狀的繪製邏輯 
    public abstract void draw(Graphics g);

    // --- 共用邏輯實作 ---

    // 只有在物件被選取 (Selected) 或滑鼠移入 (Hovered) 時才會顯示
    public void drawPorts(Graphics g) {
        if (!isSelected() && !isHovered) return;

        g.setColor(Color.BLACK);
        for (Point p : getPorts()) {
            // 以座標為中心繪製黑色小方塊
            g.fillRect(p.x - PORT_SIZE / 2, p.y - PORT_SIZE / 2, PORT_SIZE, PORT_SIZE);
        }
    }

    // 在物件正中心繪製文字標籤
    protected void drawLabel(Graphics g) {
        if (label != null && !label.isEmpty()) {
            g.setColor(Color.BLACK);
            FontMetrics fm = g.getFontMetrics(); //獲取字體寬高以精確計算座標
            // 計算置中座標：物件起始座標 + (剩餘空間的一半)
            int textX = getX() + (getWidth() - fm.stringWidth(label)) / 2;
            int textY = getY() + (getHeight() - fm.getHeight()) / 2 + fm.getAscent(); // getAscent() 上半部高度
            g.drawString(label, textX, textY);
        }
    }
    
    // 用於判斷滑鼠點擊座標 (px, py) 是否落在物件的矩形範圍內
    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}

// 矩形物件類別
class RectObject extends BaseObject {
    public RectObject(int x, int y) { super(x, y, 100, 100); }
    
    @Override
    public List<Point> getPorts() {
        List<Point> ports = new ArrayList<>();
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        
        // 矩形定義 8 個連接點：包含四個頂點與四邊中點
        ports.add(new Point(x, y));                     // 左上
        ports.add(new Point(x + w, y));                 // 右上
        ports.add(new Point(x, y + h));                 // 左下
        ports.add(new Point(x + w, y + h));             // 右下
        ports.add(new Point(x + w / 2, y));             // 上中
        ports.add(new Point(x + w / 2, y + h));         // 下中
        ports.add(new Point(x, y + h / 2));             // 左中
        ports.add(new Point(x + w, y + h / 2));         // 右中
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

// 橢圓物件類別
class OvalObject extends BaseObject {
    public OvalObject(int x, int y) { super(x, y, 100, 80); }
    @Override
    public List<Point> getPorts() {
        List<Point> ports = new ArrayList<>();
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // 橢圓僅定義 4 個連接點：上下左右四個中點
        ports.add(new Point(x + w / 2, y));             // 上
        ports.add(new Point(x + w / 2, y + h));         // 下
        ports.add(new Point(x, y + h / 2));             // 左
        ports.add(new Point(x + w, y + h / 2));         // 右
        return ports;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(getX(), getY(), getWidth(), getHeight());    // 填滿橢圓背景
        g.setColor(Color.BLACK);
        g.drawOval(getX(), getY(), getWidth(), getHeight());    // 繪製橢圓邊框
        drawPorts(g);
        drawLabel(g);
    }
}