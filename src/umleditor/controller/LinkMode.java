package umleditor.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import umleditor.model.UMLModel;
import umleditor.model.link.ArrowStrategy;
import umleditor.model.link.Link;
import umleditor.model.shape.BaseObject;
import umleditor.view.CanvasArea;

public class LinkMode extends Mode {
    private ArrowStrategy strategy; // 箭頭策略
    
    private BaseObject startObj = null;
    private int startPortIndex = -1;
    private Point currentMousePoint = null;

    // 建構子注入 MVC 組件與策略
    public LinkMode(CanvasArea view, UMLModel model, ArrowStrategy strategy) {
        super(view, model);
        this.view = view;
        this.model = model;
        this.strategy = strategy;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 從 Model 拿資料
        List<BaseObject> allObjects = model.getAllObjects();
        for (int i = allObjects.size() - 1; i >= 0; i--) {
            BaseObject obj = allObjects.get(i);
            int portIdx = obj.getHitPortIndex(e.getPoint());
            if (portIdx != -1) {
                startObj = obj;
                startPortIndex = portIdx;
                currentMousePoint = e.getPoint();
                return; 
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startObj != null) {
            currentMousePoint = e.getPoint();
            view.setTempLine(startObj.getPortLocation(startPortIndex), currentMousePoint);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (startObj != null) {
            BaseObject targetEndObj = null;
            int endPortIdx = -1;

            List<BaseObject> allObjects = model.getAllObjects();
            for (int i = allObjects.size() - 1; i >= 0; i--) {
                BaseObject obj = allObjects.get(i);
                if (obj == startObj) continue;

                int portIdx = obj.getHitPortIndex(e.getPoint());
                if (portIdx != -1) {
                    targetEndObj = obj;
                    endPortIdx = portIdx;
                    break;
                }
            }

            if (targetEndObj != null && endPortIdx != -1) {
                // 將資料存入 Model，並帶入策略
                model.addLink(new Link(startObj, startPortIndex, targetEndObj, endPortIdx, strategy));
            }
        }
        startObj = null;
        startPortIndex = -1;
        view.setTempLine(null, null); // 視覺清理
    }

}