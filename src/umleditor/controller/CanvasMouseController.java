package umleditor.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import umleditor.model.UMLModel;
import umleditor.model.shape.BaseObject;

public class CanvasMouseController extends MouseAdapter {
    private Mode currentMode = null;
    private UMLModel model;
    private BaseObject lastHoveredObject = null; // 紀錄上一次被 Hover 的物件

    // 建構子傳入 model，以便查詢物件
    public CanvasMouseController(UMLModel model) {
        this.model = model;
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (currentMode != null) currentMode.mousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentMode != null) currentMode.mouseDragged(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentMode != null) currentMode.mouseReleased(e);
    }

    // --- 新增：監聽滑鼠單純移動（無拖曳）的事件，處理 Hover 規格 ---
    @Override
    public void mouseMoved(MouseEvent e) {
        // 向 Model 查詢滑鼠當前座標下的物件
        BaseObject currentHover = model.findObjectAt(e.getX(), e.getY());

        // 如果 Hover 的物件改變了
        if (currentHover != lastHoveredObject) {
            // 1. 把舊的物件 Hover 狀態洗掉
            if (lastHoveredObject != null) {
                lastHoveredObject.setHovered(false);
            }
            // 2. 把新物件的 Hover 狀態打開
            if (currentHover != null) {
                currentHover.setHovered(true);
            }
            // 3. 更新紀錄，並通知畫布重繪 Ports
            lastHoveredObject = currentHover;
            model.notifyListeners();
        }
    }
}