package umleditor.model.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

// 實現組合模式的類別，將多個 BaseObject 組合為單一邏輯單元
public class CompositeObject extends BaseObject {
    private List<BaseObject> children = new ArrayList<>();

    public CompositeObject() {
        super(0, 0, 0, 0);  // 初始位置設為 0，待加入物件後透過 updateBounds 更新
    }

    public void addComponent(BaseObject obj) {
        children.add(obj);
        updateBounds();     // 每次加入物件都重新計算範圍 
    }

    //  回傳所有子物件，用於 Ungroup (解群組) 操作
    public List<BaseObject> getChildren() { return children; }

    // 計算並更新群組的邊界矩形 (Bounding Box)
    public void updateBounds() {
        if (children.isEmpty()) return;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (BaseObject obj : children) {
            minX = Math.min(minX, obj.getX());
            minY = Math.min(minY, obj.getY());
            maxX = Math.max(maxX, obj.getX() + obj.getWidth());
            maxY = Math.max(maxY, obj.getY() + obj.getHeight());
        }

        // 使用 super.setXXX 僅更新數據，避免觸發子物件的聯動位移邏輯
        super.setX(minX); 
        super.setY(minY); 
        super.setWidth(maxX - minX);
        super.setHeight(maxY - minY);
    }

    @Override
    public void move(int dx, int dy) {
        // 1. 先移動群組自己的外框邊界
        super.move(dx, dy);
        
        for (BaseObject child : this.children) {
            child.move(dx, dy); // 如果子物件又是另一個小群組，它會自己再遞迴傳下去！
        }
    }

    @Override
    public void draw(Graphics g) {
        for (BaseObject obj : children) {
            obj.draw(g);
        }
        // Use Case D: 被選取時顯示虛線藍色外框
        if (isSelected() || isHovered()) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke oldStroke = g2d.getStroke(); // 保存當前筆觸，以便之後恢復，避免影響子物件的繪製

            g2d.setColor(new Color(0, 56, 122)); 
            float[] dashPattern = { 10, 3 };
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 100, dashPattern, 0));
            g2d.drawRect(getX(), getY(), getWidth(), getHeight());
            g2d.setStroke(oldStroke);   // 恢復原筆觸，保持繪圖上下文的乾淨
        }
    }
    
    @Override
    public boolean isNameEditable() {
        return false; // 規格：群組物件不允許修改標籤
    }
    @Override
    public List<Port> getPorts() { 
        return new ArrayList<>(); 
    }
    @Override
    public boolean isGroup() {
        return true; // 只有我（群組物件）會回傳 true！
    }

    @Override
    public List<BaseObject> getMembers() {
        return this.children; // 回傳我肚子裡包裝的所有圖形
    }
}