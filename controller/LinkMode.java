package umleditor.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import umleditor.model.UMLModel;
import umleditor.model.links.LinkStrategy;
import umleditor.model.objects.BaseObject;
import umleditor.model.links.Link;
import umleditor.view.Canvas;

public class LinkMode extends Mode {
    // 這個 Mode 產生線條時，要塞給線條什麼樣的「箭頭策略」
    private LinkStrategy arrowDrawingStrategy;
    
    private BaseObject startShape = null;
    private int startPortIndex = -1;
    private Point currentMousePoint = null;

    public LinkMode(Canvas view, UMLModel model, LinkStrategy strategy) {
        super(view, model);
        this.arrowDrawingStrategy = strategy;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        List<BaseObject> allShapes = model.getAllObjects();
        // 反向走訪 (從最上層開始找)
        for (int i = allShapes.size() - 1; i >= 0; i--) {
            BaseObject shape = allShapes.get(i);
            int portIdx = shape.getHitPortIndex(mouseEvent.getPoint());
            
            // 找到了使用者點擊的起點連接點！
            if (portIdx != -1) {
                startShape = shape;
                startPortIndex = portIdx;
                currentMousePoint = mouseEvent.getPoint();
                return; 
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        // 如果起點存在，請 Canvas 幫忙畫一條跟著滑鼠游標動的「暫存黑線」
        if (startShape != null) {
            currentMousePoint = mouseEvent.getPoint();
            view.setTempLine(startShape.getPortLocation(startPortIndex), currentMousePoint);
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (startShape != null) {
            BaseObject endShape = null;
            int endPortIdx = -1;

            // 尋找滑鼠放開時，有沒有剛好對準別人的連接點
            List<BaseObject> allShapes = model.getAllObjects();
            for (int i = allShapes.size() - 1; i >= 0; i--) {
                BaseObject shape = allShapes.get(i);
                if (shape == startShape) continue; // 不能自己連自己

                int portIdx = shape.getHitPortIndex(mouseEvent.getPoint());
                if (portIdx != -1) {
                    endShape = shape;
                    endPortIdx = portIdx;
                    break;
                }
            }

            // 如果起點與終點都合法，建立實體線段存入 Model
            if (endShape != null && endPortIdx != -1) {
                model.addLink(new Link(startShape, startPortIndex, endShape, endPortIdx, arrowDrawingStrategy));
            }
        }
        
        // 狀態清理
        startShape = null;
        startPortIndex = -1;
        view.setTempLine(null, null); // 清掉暫時黑線
    }
}