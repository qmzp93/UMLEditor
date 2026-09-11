package umleditor.controller;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import umleditor.model.UMLModel;
import umleditor.model.shape.BaseObject;
import umleditor.view.CanvasArea;

// SelectMode: 負責處理「選取」、「拖曳移動」、「拖曳多選」與「縮放」的控制器
public class SelectMode extends Mode {
    private Point startPoint;
    private BaseObject targetObject = null;
    
    // --- 控制器的內部狀態機 (State Machine) ---
    private boolean isDragging = false;
    private boolean isResizing = false;
    private boolean isSelecting = false;
    
    // --- 縮放專用的計算暫存變數 ---
    private int resizePortIndex = -1;
    private Point fixedPoint = null;

    public SelectMode(CanvasArea view, UMLModel model) {
        super(view, model);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        
        // --- 1. 優先檢查：目前已經被選取的物件，其 Ports 有沒有被點到 ---
        for (BaseObject obj : model.getAllObjects()) {
            if (obj.isSelected()) {
                int portIndex = obj.getHitPortIndex(startPoint);
                if (portIndex != -1) {
                    // 找到了！點擊落在 Port 的完整範圍內（包含物件外的部分）
                    isResizing = true;
                    targetObject = obj; // 鎖定縮放目標
                    resizePortIndex = portIndex;
                    calculateFixedPoint(targetObject, portIndex); // 計算定錨點
                    model.notifyListeners();
                    return; // 成功進入縮放模式，直接結束方法！
                }
            }
        }

        // --- 2. 若沒點到任何 Port，才去檢查是否點擊到物件本體 (原本的單選/移動邏輯) ---
        targetObject = model.findObjectAt(startPoint.x, startPoint.y);

        if (targetObject == null) {
            // 點擊到純空白處 -> 進入「多選框」模式
            model.unselectAll();
            isSelecting = true;
        } else {
            // 點擊到物件本體
            if (!targetObject.isSelected()) {
                model.unselectAll();
                targetObject.setSelected(true);
                model.moveObjectToFront(targetObject);
            }
            // 點擊到物件本體，代表接下來要做「拖曳移動」
            isDragging = true;
        }
        
        model.notifyListeners();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point currentPoint = e.getPoint();

        if (isSelecting) {
            // --- 處理多選框 ---
            int x = Math.min(startPoint.x, currentPoint.x);
            int y = Math.min(startPoint.y, currentPoint.y);
            int width = Math.abs(startPoint.x - currentPoint.x);
            int height = Math.abs(startPoint.y - currentPoint.y);
            Rectangle selectArea = new Rectangle(x, y, width, height);
            
            // 委派 View 畫出藍色半透明選取框
            view.setSelectionArea(selectArea);
            
            // 即時判定哪些物件被包圍了
            for (BaseObject obj : model.getAllObjects()) {
                obj.setSelected(obj.isContainedIn(selectArea));
            }
            model.notifyListeners();
            
        } else if (isResizing && targetObject != null && fixedPoint != null) {
            // --- 處理縮放 ---
            performResize(currentPoint);
            model.notifyListeners();
            
        } else if (isDragging && targetObject != null) {
            // --- 處理移動 ---
            int dx = currentPoint.x - startPoint.x;
            int dy = currentPoint.y - startPoint.y;
            
            // 若選取多個物件，一起移動
            for (BaseObject obj : model.getAllObjects()) {
                if (obj.isSelected()) {
                    obj.move(dx, dy);
                }
            }
            startPoint = currentPoint; // 更新起點供下一次觸發使用
            model.notifyListeners();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // 重置所有狀態
        isDragging = false;
        isResizing = false;
        if (isSelecting) {
            isSelecting = false;
            view.setSelectionArea(null); // 隱藏畫布上的選取框
        }
        targetObject = null;
        fixedPoint = null;
    }

    // ==========================================
    //  移植自 BaseObject 的縮放數學邏輯
    // ==========================================

    private void calculateFixedPoint(BaseObject obj, int portIdx) {
        Rectangle b = obj.getBounds();
        int px = b.x, py = b.y, w = b.width, h = b.height;
        switch (portIdx) {
            case 0: fixedPoint = new Point(px + w, py + h); break; // 左上 -> 固定右下
            case 1: fixedPoint = new Point(px, py + h); break;     // 右上 -> 固定左下
            case 2: fixedPoint = new Point(px + w, py); break;     // 左下 -> 固定右上
            case 3: fixedPoint = new Point(px, py); break;         // 右下 -> 固定左上
            case 4: fixedPoint = new Point(px, py + h); break;     // 上中 -> 固定下中
            case 5: fixedPoint = new Point(px, py); break;         // 下中 -> 固定上中
            case 6: fixedPoint = new Point(px + w, py); break;     // 左中 -> 固定右中
            case 7: fixedPoint = new Point(px, py); break;         // 右中 -> 固定左中
        }
    }

    private void performResize(Point mousePos) {
        int mx = mousePos.x, my = mousePos.y;
        int fx = fixedPoint.x, fy = fixedPoint.y;
        
        // 限制圖形的最小寬高為 50
        int newW = Math.max(50, Math.abs(mx - fx));
        int newH = Math.max(50, Math.abs(my - fy));
        
        Rectangle b = targetObject.getBounds();
        int newX = b.x, newY = b.y;

        if (resizePortIndex < 4) { 
            // 0~3: 四個角點，雙向縮放
            newX = Math.min(mx, fx);
            newY = Math.min(my, fy);
            b.setBounds(newX, newY, newW, newH);
        } else if (resizePortIndex == 4 || resizePortIndex == 5) { 
            // 4, 5: 上下中點，單向改變高度 (X 與 Width 不變)
            newY = Math.min(my, fy);
            b.setBounds(b.x, newY, b.width, newH);
        } else if (resizePortIndex == 6 || resizePortIndex == 7) { 
            // 6, 7: 左右中點，單向改變寬度 (Y 與 Height 不變)
            newX = Math.min(mx, fx);
            b.setBounds(newX, b.y, newW, b.height);
        }
        
        // 將計算好的新外框更新回 Model 中
        targetObject.setBounds(b);
    }
}