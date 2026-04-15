import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompositeObject extends BaseObject {
    private List<BaseObject> children = new ArrayList<>();

    public CompositeObject() {
        super(0, 0, 0, 0); // 初始座標由子物件決定
    }

    public void addComponent(BaseObject obj) {
        children.add(obj);
        updateBounds(); // 每次加入物件都重新計算範圍 
    }

    public List<BaseObject> getChildren() { return children; }

    // 規格規定：群組範圍為完全包含所有組成物件的最小正方形區域
    // 重新計算群組的邊界矩形 
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
        super.setX(minX); 
        super.setY(minY); 
        super.setWidth(maxX - minX);
        super.setHeight(maxY - minY);
    }

    @Override
    public void setX(int x) {
        int dx = x - getX();
        super.setX(x);
        for (BaseObject obj : children) {
            obj.setX(obj.getX() + dx);
        }
    }

    @Override
    public void setY(int y) {
        int dy = y - getY();
        super.setY(y);
        for (BaseObject obj : children) {
            obj.setY(obj.getY() + dy);
        }
    }

    @Override
    public void draw(Graphics g) {
        for (BaseObject obj : children) {
            obj.draw(g);
        }
        if (isSelected()) {
            // 被選取時僅顯示外框 
            if (isSelected()) {
                g.setColor(Color.BLUE);
                g.drawRect(getX(), getY(), getWidth(), getHeight());
            }
        }
    }

    @Override
    public List<Point> getPorts() { return new ArrayList<>(); } // 群組本身不具備 Ports [cite: 57]
}