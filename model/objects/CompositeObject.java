package umleditor.model.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 群組物件 (Composite Pattern)：將多個圖形綁在一起，當成一個大圖形來操作
public class CompositeObject extends BaseObject {
    private List<BaseObject> children = new ArrayList<>();

    public CompositeObject() {
        super(0, 0, 0, 0);  // 一開始先給個假座標，等加入子物件後會重新計算
    }

    public void addComponent(BaseObject child) {
        children.add(child);
        updateBounds(); // 每次加入新成員，群組的外框就要重新計算
    }

    // 回傳所有子物件 (唯讀)，通常在 Ungroup 解散群組時會用到
    public List<BaseObject> getChildren() { 
        return Collections.unmodifiableList(children); 
    }

    // 計算剛好可以包住所有子物件的「最小外接矩形」
    public void updateBounds() {
        if (children.isEmpty()) return;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (BaseObject child : children) {
            minX = Math.min(minX, child.getX());
            minY = Math.min(minY, child.getY());
            maxX = Math.max(maxX, child.getX() + child.getWidth());
            maxY = Math.max(maxY, child.getY() + child.getHeight());
        }

        // 呼叫父類的 set 方法來更新群組自己的邊界大小
        super.setX(minX); 
        super.setY(minY); 
        super.setWidth(maxX - minX);
        super.setHeight(maxY - minY);
    }

    // 覆寫移動邏輯：當群組被拖曳移動時，要帶著底下所有小弟一起動
    @Override
    public void move(int dx, int dy) {
        super.move(dx, dy); // 1. 先移動群組外框
        
        for (BaseObject child : this.children) {
            child.move(dx, dy); // 2. 叫每個子物件跟著移動 (如果子物件也是群組，會自動產生遞迴)
        }
    }

    @Override
    public void draw(Graphics g) {
        // 1. 先畫出所有的子圖形
        for (BaseObject child : children) {
            child.draw(g);
        }
        
        // 2. 規格要求：群組被選取時，要顯示一個「包住全體的框」
        if (isSelected() || isHovered()) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke oldStroke = g2d.getStroke(); // 備份原來的畫筆粗細

            g2d.setColor(new Color(0, 56, 122)); // 深藍色
            float[] dashPattern = { 10, 3 };     // 定義虛線樣式 (畫 10 空 3)
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 100, dashPattern, 0));
            g2d.drawRect(getX(), getY(), getWidth(), getHeight());
            g2d.setStroke(oldStroke); // 還原畫筆，免得影響下一個物件
        }
    }
    
    // 群組不允許修改標籤 (規格限制)
    @Override
    public boolean isNameEditable() { return false; } 
    
    // 群組沒有連接點，不能被畫線連起來 (規格限制)
    @Override
    public List<Port> getPorts() { return new ArrayList<>(); }
    
    // 告訴系統「我是一個群組」(用於 Ungroup 判斷)
    @Override
    public boolean isGroup() { return true; }

    @Override
    public List<BaseObject> getMembers() {
        return Collections.unmodifiableList(this.children);
    }
}