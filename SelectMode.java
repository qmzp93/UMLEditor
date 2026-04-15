import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

public class SelectMode implements Mode {
    private CanvasArea canvas;
    private BaseObject selectedObject = null;
    private Point startPoint = null;
    private Point lastPoint = null;
    private int resizePortIndex = -1;
    private Point fixedPoint = null; // 縮放時固定不動的點
    private Rectangle selectArea = null;

    public SelectMode(CanvasArea canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastPoint = e.getPoint();
        startPoint = e.getPoint();
        
        // 1. 優先檢查是否點擊到「已選取物件」的 Port (進入 Resize 模式) [cite: 113]
        if (selectedObject != null && selectedObject.isSelected()) {
            // 規格：Composite 物件無法縮放 
            if (!(selectedObject instanceof CompositeObject)) {
                resizePortIndex = findPortAt(selectedObject, e.getPoint());
                if (resizePortIndex != -1) {
                    calculateFixedPoint(selectedObject, resizePortIndex);
                    return; // 進入縮放邏輯，跳過後續判斷
                }
            }
        }

        // 2. 檢查是否有物件被點擊 (進入 Select/Move 模式) [cite: 67, 102]
        BaseObject clickedObj = canvas.findObjectAt(e.getX(), e.getY());

        if (clickedObj != null) {
            canvas.unselectAll();
            selectedObject = clickedObj;
            selectedObject.setSelected(true); // [cite: 66]
            canvas.moveObjectToFront(selectedObject); // 最後選取的繪製於最上層 
            resizePortIndex = -1;
        } else {
            // 3. 點擊空白處，準備區域選取或取消所有選取 [cite: 74, 75]
            canvas.unselectAll();
            selectedObject = null;
            resizePortIndex = -1;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedObject == null) { // 區域選取
            int x = Math.min(startPoint.x, e.getX());
            int y = Math.min(startPoint.y, e.getY());
            int w = Math.abs(startPoint.x - e.getX());
            int h = Math.abs(startPoint.y - e.getY());
            selectArea = new Rectangle(x, y, w, h);
            
            // 更新畫布上的選取框視覺效果 
            canvas.setSelectionArea(selectArea);
            
            // 判定哪些物件完全落在區域內
            for (BaseObject obj : canvas.getAllObjects()) {
                obj.setSelected(selectArea.contains(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()));
            }
        } else if (resizePortIndex != -1) {
            // --- Use Case F: Resize 邏輯 --- [cite: 114]
            int mx = e.getX();
            int my = e.getY();
            int fx = fixedPoint.x;
            int fy = fixedPoint.y;

            // 計算新寬高，並限制最小尺寸為 50 pixels 
            int newW = Math.max(50, Math.abs(mx - fx));
            int newH = Math.max(50, Math.abs(my - fy));
            
            // 處理座標轉換 (自動重新計算基準座標以支援交叉反向拖曳) 
            selectedObject.setX(Math.min(mx, fx));
            selectedObject.setY(Math.min(my, fy));
            
            // 根據拖曳的 Port 類型決定縮放維度 (角點雙向，邊中點單向)
            if (resizePortIndex < 4) { // 0:NW, 1:NE, 2:SW, 3:SE (四個角點)
                selectedObject.setWidth(newW);
                selectedObject.setHeight(newH);
            } else if (resizePortIndex == 4 || resizePortIndex == 5) { // 4:N, 5:S (上下中點)
                selectedObject.setHeight(newH);
            } else { // 6:W, 7:E (左右中點)
                selectedObject.setWidth(newW);
            }
        } else {
            // --- Use Case E: Move 邏輯 --- [cite: 103]
            int dx = e.getX() - lastPoint.x;
            int dy = e.getY() - lastPoint.y;

            selectedObject.setX(selectedObject.getX() + dx);
            selectedObject.setY(selectedObject.getY() + dy);
            lastPoint = e.getPoint();
        }
        canvas.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastPoint = null;
        startPoint = null;
        resizePortIndex = -1; // 結束縮放狀態 [cite: 115]

        selectArea = null;
        canvas.setSelectionArea(null);
    }

    // 尋找點擊位置是否在物件的 Port 範圍內
    private int findPortAt(BaseObject obj, Point p) {
        List<Point> ports = obj.getPorts();
        for (int i = 0; i < ports.size(); i++) {
            if (p.distance(ports.get(i)) < 13) return i;
        }
        return -1;
    }

    // 計算縮放時對角的固定點
    private void calculateFixedPoint(BaseObject obj, int portIdx) {
        int x = obj.getX(), y = obj.getY(), w = obj.getWidth(), h = obj.getHeight();
        switch (portIdx) {
            case 0: fixedPoint = new Point(x + w, y + h); break; // 拖左上，固定右下
            case 3: fixedPoint = new Point(x, y); break;         // 拖右下，固定左上
            case 1: fixedPoint = new Point(x, y + h); break;     // 拖右上，固定左下
            case 2: fixedPoint = new Point(x + w, y); break;     // 拖左下，固定右上
            case 4: fixedPoint = new Point(x, y + h); break;     // 拖上中，固定底部
            case 5: fixedPoint = new Point(x, y); break;         // 拖下中，固定頂部
            case 6: fixedPoint = new Point(x + w, y); break;     // 拖左中，固定右側
            case 7: fixedPoint = new Point(x, y); break;         // 拖右中，固定左側
        }
    }
}