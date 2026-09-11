package umleditor.model.shape;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import umleditor.model.Drawable;

// 所有畫布物件的抽象基類，負責封裝幾何邊界、共用外觀與多型預設行為
public abstract class BaseObject implements Drawable {
    
    // ==========================================
    //  1. 核心屬性區 (Property)
    // ==========================================
    
    // 核心幾何屬性：以單一 Rectangle 完美取代 x, y, width, height
    protected Rectangle bounds;

    // 外觀與狀態屬性
    private String label = "";
    private boolean isSelected = false;
    private boolean isHovered = false;
    private Color color = Color.LIGHT_GRAY;

    // 互動暫存狀態 (保留給 Controller 進行縮放計算使用)
    protected Point fixedPoint = null;
    protected int currentResizePort = -1;

    public BaseObject(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    // ==========================================
    //  2. 領域核心行為 (Domain Logic)
    // ==========================================

    public void move(int dx, int dy) {
        this.bounds.translate(dx, dy);
    }
    
    // 讓物件自己判斷「我是否被這個選取框完全包圍」
    public boolean isContainedIn(Rectangle area) {
        return area.contains(this.bounds);
    }

    // 讓物件自己判斷「滑鼠點到了我的哪一個連接點」
    public int getHitPortIndex(Point p) {
        List<Port> ports = getPorts();
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).contains(p)) {
                return i;
            }
        }
        return -1;
    }

    // 取得特定連接點的絕對座標 (供 Link 連線使用)
    public Point getPortLocation(int portIndex) {
        List<Port> currentPorts = getPorts();
        if (portIndex >= 0 && portIndex < currentPorts.size()) {
            return currentPorts.get(portIndex).getLocation();
        }
        return new Point(bounds.x, bounds.y); // 防呆機制
    }

    // ==========================================
    //  3. 多型預設行為 (Tell, Don't Ask 原則)
    // ==========================================

    public boolean isNameEditable() { return true; }
    public boolean isGroup() { return false; }
    public List<BaseObject> getMembers() { return new ArrayList<>(); }

    // ==========================================
    //  4. 繪圖共用邏輯 (Drawing)
    // ==========================================

    public void drawPorts(Graphics g) {
        if (!isSelected() && !isHovered()) return;
        g.setColor(Color.BLACK);
        for (Port port : getPorts()) {
            int s = port.getSize();
            g.fillRect(port.getX() - s / 2, port.getY() - s / 2, s, s);
        }
    }

    protected void drawLabel(Graphics g) {
        if (label != null && !label.isEmpty()) {
            g.setColor(Color.BLACK);
            FontMetrics fm = g.getFontMetrics();
            // 計算置中座標：物件起始座標 + (剩餘空間的一半)
            int textX = bounds.x + (bounds.width - fm.stringWidth(label)) / 2;
            int textY = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(label, textX, textY);
        }
    }

    // ==========================================
    //  5. 抽象方法 (強迫子類別實作專屬邏輯)
    // ==========================================

    public abstract List<Port> getPorts();
    public abstract void draw(Graphics g);

    // ==========================================
    //  6. Getters & Setters (使用委派模式隱藏細節)
    // ==========================================

    public Rectangle getBounds() { return bounds; }
    public void setBounds(Rectangle bounds) { this.bounds = bounds; }

    // 完美的委派 (Delegation)：外部依然呼叫 getX()，但底層統一由 bounds 管理
    public int getX() { return bounds.x; }
    public void setX(int x) { bounds.x = x; }
    public int getY() { return bounds.y; }
    public void setY(int y) { bounds.y = y; }
    public int getWidth() { return bounds.width; }
    public void setWidth(int width) { bounds.width = width; }
    public int getHeight() { return bounds.height; }
    public void setHeight(int height) { bounds.height = height; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { this.isSelected = selected; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public boolean isHovered() { return isHovered; }
    public void setHovered(boolean hovered) { this.isHovered = hovered; }
}