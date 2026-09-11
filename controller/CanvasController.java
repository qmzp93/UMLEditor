package umleditor.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import umleditor.model.UMLModel;
import umleditor.model.objects.BaseObject;

// 畫布的事件路由器
public class CanvasController extends MouseAdapter {
    private Mode currentMode = null;
    private UMLModel model;
    private BaseObject lastHoveredObject = null; // 紀錄上一次被 Hover 的物件

    public CanvasController(UMLModel model) {
        this.model = model;
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
    }

    // 往下轉發事件給 currentMode
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

    // 這個行為是全局共用的 (不論在哪個模式下，滑鼠游標掃過物件都會發亮)，所以寫在這裡
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        BaseObject currentHover = model.findObjectAt(mouseEvent.getX(), mouseEvent.getY());

        if (currentHover != lastHoveredObject) {
            if (lastHoveredObject != null) lastHoveredObject.setHovered(false);
            if (currentHover != null) currentHover.setHovered(true);
            
            lastHoveredObject = currentHover;
            model.notifyListeners();
        }
    }
}