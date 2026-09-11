package umleditor.model.objects;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import umleditor.model.Drawable;

// 所有畫布物件的抽象基類
public abstract class BaseObject implements Drawable {
    protected Rectangle bounds;         // x, y, width, height
    private String label = "";
    private boolean isSelected = false;
    private boolean isHovered = false;
    private Color color = Color.LIGHT_GRAY;

    public BaseObject(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void move(int dx, int dy) {
        this.bounds.translate(dx, dy);
    }
    
    // 檢查物件是否完全「選取框」包圍住
    public boolean isContainedIn(Rectangle selectionArea) {
        return selectionArea.contains(this.bounds);
    }

    // 判斷滑鼠是否點擊到這個物件的 Port
    public int getHitPortIndex(Point mousePoint) {
        List<Port> ports = getPorts();
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).contains(mousePoint)) {
                return i;
            }
        }
        return -1;
    }

    // 取得特定 Port 的絕對座標 (供 Link 連線使用)
    public Point getPortLocation(int portIndex) {
        List<Port> currentPorts = getPorts();
        if (portIndex >= 0 && portIndex < currentPorts.size()) {
            return currentPorts.get(portIndex).getLocation();
        }
        return new Point(bounds.x, bounds.y);   // 防呆機制，避免發生索引越界
    }

    // 多型預設行為
    public boolean isNameEditable() { return true; }        // 預設允許修改名稱
    public boolean isGroup() { return false; }              // 預設不是群組物件
    public List<BaseObject> getMembers() { return new ArrayList<>(); }  // 預設沒有子成員

    public void drawPorts(Graphics g) {
        if (!isSelected() && !isHovered()) return;

        g.setColor(Color.BLACK);
        for (Port port : getPorts()) {
            port.draw(g);
        }
    }

    protected void drawLabel(Graphics g) {
        if (label != null && !label.isEmpty()) {
            g.setColor(Color.BLACK);
            FontMetrics fontMetrics = g.getFontMetrics();
            // 計算置中座標：物件起始座標 + (剩餘空間的一半)
            int textX = bounds.x + (bounds.width - fontMetrics.stringWidth(label)) / 2;
            int textY = bounds.y + (bounds.height - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
            g.drawString(label, textX, textY);
        }
    }

    //  抽象方法
    public abstract List<Port> getPorts();
    public abstract void draw(Graphics g);

    // Getters & Setters
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

    // 防禦性複製：避免外部直接修改內部的 Rectangle
    public Rectangle getBounds() { return new Rectangle(bounds); }
    public void setBounds(Rectangle bounds) { this.bounds = new Rectangle(bounds); }
}