package umleditor.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;

import umleditor.model.UMLModel;
import umleditor.model.objects.BaseObject;
import umleditor.view.Canvas;

public class SelectMode extends Mode {
    private Point startPoint;
    private BaseObject targetObject = null;
    private boolean isDragging = false; // 正在移動圖形
    private boolean isResizing = false; // 正在縮放圖形
    private boolean isSelecting = false; // 正在拉藍色虛線框多選
    private boolean hasDragged = false; // 區分是「單純點擊」還是「拖曳移動」

    // --- 縮放專用的計算暫存變數 ---
    private ResizeHandle resizeHandle = null;
    private Point fixedAnchorPoint = null; // 縮放時，對角線那個「固定不動」的點

    public SelectMode(Canvas view, UMLModel model) {
        super(view, model);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        startPoint = mouseEvent.getPoint();
        hasDragged = false;

        // --- 情境 1：使用者點到了圖形邊緣的黑色小方塊 (Port) -> 準備縮放 ---
        for (BaseObject shape : model.getAllObjects()) {
            if (shape.isSelected()) {
                int portIndex = shape.getHitPortIndex(startPoint);
                if (portIndex != -1) {
                    isResizing = true;
                    targetObject = shape;
                    resizeHandle = ResizeHandle.fromIndex(portIndex);
                    // 計算縮放時的對角線錨點 (例如拉右下角，左上角就要釘死不動)
                    fixedAnchorPoint = ResizeService.calculateFixedPoint(targetObject.getBounds(), resizeHandle);
                    return; // 確定是縮放，直接結束這個 Method
                }
            }
        }

        targetObject = model.findObjectAt(startPoint.x, startPoint.y);

        if (targetObject == null) {
            // 情境 2：點到空白處 -> 取消選取，準備拉藍色多選框
            model.unselectAll();
            isSelecting = true;
        } else {
            // 情境 3：點到圖形本體 -> 準備移動
            if (!targetObject.isSelected()) {
                model.unselectAll();
                targetObject.setSelected(true);
                model.moveObjectToFront(targetObject); // Z-Order 置頂
            }
            isDragging = true;
        }

        model.notifyListeners();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        Point currentMousePoint = mouseEvent.getPoint();

        if (isSelecting) {
            // --- 將多選框委派出去 ---
            SelectionService.performMultiSelection(startPoint, currentMousePoint, view, model);
            model.notifyListeners();

        } else if (isResizing && targetObject != null && fixedAnchorPoint != null && resizeHandle != null) {
            // --- 處理縮放 (把複雜的數學交給 ResizeService 處理) ---
            ResizeService.performResize(targetObject, resizeHandle, fixedAnchorPoint, currentMousePoint);
            model.notifyListeners();

        } else if (isDragging && targetObject != null) {
            // --- 處理移動 ---
            hasDragged = true;
            int deltaX = currentMousePoint.x - startPoint.x;
            int deltaY = currentMousePoint.y - startPoint.y;

            // 若目前選取了多個物件，要一起跟著移動
            for (BaseObject shape : model.getAllObjects()) {
                if (shape.isSelected()) {
                    shape.move(deltaX, deltaY);
                }
            }
            startPoint = currentMousePoint; // 更新起點供下一幀畫面使用
            model.notifyListeners();
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        // 處理「單純點擊 (沒拖曳)」覆蓋物件的情況
        if (isDragging && !hasDragged && targetObject != null) {
            model.unselectAll();
            targetObject.setSelected(true);
            model.moveObjectToFront(targetObject);
            model.notifyListeners();
        }

        // --- 狀態還原 ---
        isDragging = false;
        isResizing = false;
        if (isSelecting) {
            isSelecting = false;
            view.setSelectionArea(null);
        }
        targetObject = null;
        resizeHandle = null;
        fixedAnchorPoint = null;
    }
}