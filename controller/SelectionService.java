package umleditor.controller;

import java.awt.Point;
import java.awt.Rectangle;
import umleditor.model.UMLModel;
import umleditor.model.objects.BaseObject;
import umleditor.view.Canvas;

public final class SelectionService {
    
    private SelectionService() {} // 工具類別防呆

    // 處理多選框的計算與視覺更新
    public static void performMultiSelection(Point startPoint, Point currentPoint, Canvas view, UMLModel model) {
        // 1. 計算多選框的幾何範圍
        int x = Math.min(startPoint.x, currentPoint.x);
        int y = Math.min(startPoint.y, currentPoint.y);
        int width = Math.abs(startPoint.x - currentPoint.x);
        int height = Math.abs(startPoint.y - currentPoint.y);
        Rectangle selectArea = new Rectangle(x, y, width, height);
        
        // 2. 委派 View 更新藍色半透明選取框
        view.setSelectionArea(selectArea);
        
        // 3. 判定哪些物件被包圍了，並更新 Model
        for (BaseObject obj : model.getAllObjects()) {
            obj.setSelected(obj.isContainedIn(selectArea));
        }
    }
}