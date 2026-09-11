package umleditor.controller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import umleditor.model.ShapeType;
import umleditor.model.UMLModel;
import umleditor.model.shape.BaseObject;
import umleditor.model.shape.ShapeFactory;
import umleditor.view.CanvasArea;
import umleditor.view.UMLSideBar;

public class CreateObjectController extends MouseAdapter {
    private ShapeType shapeType;
    private JButton toolButton;
    private UMLSideBar sideBar;  // 改為持有 SideBar 的引用來控制視覺
    private CanvasArea canvas;
    private UMLModel model;

    public CreateObjectController(ShapeType shapeType, JButton toolButton, UMLSideBar sideBar, CanvasArea canvas, UMLModel model) {
        this.shapeType = shapeType;
        this.toolButton = toolButton;
        this.sideBar = sideBar;
        this.canvas = canvas;
        this.model = model;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 委派 View 改變視覺：目標按鈕變黑
        sideBar.setActiveVisual(toolButton);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point screenPoint = e.getLocationOnScreen();
        Point canvasPoint = new Point(screenPoint);
        SwingUtilities.convertPointFromScreen(canvasPoint, canvas);

        if (canvas.getBounds().contains(canvasPoint)) {
            BaseObject newShape = ShapeFactory.createShape(shapeType, canvasPoint.x, canvasPoint.y);
            model.addObject(newShape);
        }

        // 動作結束，委派 View 將視覺復原（把剛剛記住的原本模式按鈕重新變黑）
        // 這裡傳入 null 只是為了把拖曳按鈕變白，實際規格要求回到 Select，這在 MainFrame 裡統一處理
        sideBar.setActiveVisual(null); 
    }
}