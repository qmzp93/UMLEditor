import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

// 處理選取、移動與縮放物件的邏輯
public class SelectMode implements Mode {
    private CanvasArea canvas;
    private BaseObject selectedObject = null;
    private Point startPoint = null;
    private Point lastPoint = null;
    private int resizePortIndex = -1;   // 目前正在拖曳的 Port 索引 (-1 代表非縮放模式)
    private Point fixedPoint = null;    // 縮放時保持不動的對角點
    private Rectangle selectArea = null;

    public SelectMode(CanvasArea canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        lastPoint = e.getPoint();

        // --- 處理 Resize (縮放) 判定 ---
        // 只有物件已被選取且非群組物件 (Composite) 時才允許縮放
        if (selectedObject != null && selectedObject.isSelected()) {
            // Composite 物件無法縮放 
            if (!(selectedObject instanceof CompositeObject)) {
                resizePortIndex = findPortAt(selectedObject, e.getPoint());
                if (resizePortIndex != -1) {
                    // Oval 只有四邊中點，將其索引 0-3 映射到基類的 4-7 (中點縮放邏輯)
                    if (selectedObject instanceof OvalObject) {
                        resizePortIndex += 4; 
                    }
                    // 鎖定縮放時的固定端
                    calculateFixedPoint(selectedObject, resizePortIndex);
                    return; // 進入縮放模式，不再執行後續選取邏輯
                }
            }
        }

        selectedObject = canvas.findObjectAt(e.getX(), e.getY());
        // --- 處理 Select/Move (選取與移動) 判定 ---
        if (selectedObject != null) {
            if (!selectedObject.isSelected()) {
                canvas.unselectAll();
                selectedObject.setSelected(true);
            }
            canvas.moveObjectToFront(selectedObject); // 最後選取的繪製於最上層 
            resizePortIndex = -1;
        } else {
            // 點擊空白處，準備區域選取或取消所有選取
            canvas.unselectAll();
            selectedObject = null;
            resizePortIndex = -1;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedObject == null) {
            // --- Use Case C: 區域多選 ---
            int x = Math.min(startPoint.x, e.getX());
            int y = Math.min(startPoint.y, e.getY());
            int w = Math.abs(startPoint.x - e.getX());
            int h = Math.abs(startPoint.y - e.getY());
            selectArea = new Rectangle(x, y, w, h);
            
            // 將範圍傳給畫布以顯示藍色半透明框 
            canvas.setSelectionArea(selectArea);
            
            // 判定物件是否完全包含在選取框內
            for (BaseObject obj : canvas.getAllObjects()) {
                obj.setSelected(selectArea.contains(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()));
            }
        } else if (resizePortIndex != -1) {
            // --- Use Case F: Resize 縮放邏輯 ---
            int mx = e.getX(), my = e.getY();
            int fx = fixedPoint.x, fy = fixedPoint.y;

            // 限制物件最小尺寸為 50x50
            int newW = Math.max(50, Math.abs(mx - fx));
            int newH = Math.max(50, Math.abs(my - fy));
            
            // 根據點擊的 Port 類型決定縮放維度
            if (resizePortIndex < 4) { 
                // 角點縮放：同時更動 X, Y, Width, Height
                selectedObject.setX(Math.min(mx, fx));
                selectedObject.setY(Math.min(my, fy));
                selectedObject.setWidth(newW);
                selectedObject.setHeight(newH);
            } else if (resizePortIndex == 4 || resizePortIndex == 5) { 
                // 上下中點縮放：僅更動 Y 與 Height
                selectedObject.setY(Math.min(my, fy));
                selectedObject.setHeight(newH);
            } else { 
                // 左右中點縮放：僅更動 X 與 Width
                selectedObject.setX(Math.min(mx, fx));
                selectedObject.setWidth(newW);
            }
            canvas.repaint();
        } else {
            // --- Use Case E: Move 邏輯 ---
            int dx = e.getX() - lastPoint.x;
            int dy = e.getY() - lastPoint.y;

            for (BaseObject obj : canvas.getAllObjects()) {
                // 只要物件處於「選取狀態」，就同步套用位移量
                if (obj.isSelected()) {
                    obj.setX(obj.getX() + dx);
                    obj.setY(obj.getY() + dy);
                }
            }
            lastPoint = e.getPoint();
        }
        canvas.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // 重置所有暫時狀態
        lastPoint = null;
        startPoint = null;
        resizePortIndex = -1; 
        selectArea = null;
        canvas.setSelectionArea(null);
        canvas.repaint();
    }

    // 檢查滑鼠是否精準點擊在物件的 Port 上
    private int findPortAt(BaseObject obj, Point p) {
        List<Point> ports = obj.getPorts();
        int offset = obj.getPortSize() / 2 + 3;

        for (int i = 0; i < ports.size(); i++) {
            Point port = ports.get(i);
            // 檢查滑鼠點 p 是否落在正方形的邊界內
            boolean inX = (p.x >= port.x - offset && p.x <= port.x + offset);
            boolean inY = (p.y >= port.y - offset && p.y <= port.y + offset);
            if (inX && inY) return i;
        }
        return -1;
    }

    // 計算縮放時對角的固定點
    private void calculateFixedPoint(BaseObject obj, int portIdx) {
        int x = obj.getX(), y = obj.getY(), w = obj.getWidth(), h = obj.getHeight();
        switch (portIdx) {
            case 0: fixedPoint = new Point(x + w, y + h); break; // 拖左上，固定右下
            case 1: fixedPoint = new Point(x, y + h); break;     // 拖右上，固定左下
            case 2: fixedPoint = new Point(x + w, y); break;     // 拖左下，固定右上
            case 3: fixedPoint = new Point(x, y); break;         // 拖右下，固定左上
            case 4: fixedPoint = new Point(x, y + h); break;     // 拖上中，固定底部
            case 5: fixedPoint = new Point(x, y); break;         // 拖下中，固定頂部
            case 6: fixedPoint = new Point(x + w, y); break;     // 拖左中，固定右側
            case 7: fixedPoint = new Point(x, y); break;         // 拖右中，固定左側
        }
    }
}