package umleditor.controller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import umleditor.model.ObjectType;
import umleditor.model.UMLModel;
import umleditor.model.objects.BaseObject;
import umleditor.model.objects.ObjectFactory;
import umleditor.view.Canvas;
import umleditor.view.SideBar;

public class CreateObjectController extends MouseAdapter {
    private ObjectType objectType;
    private JButton toolButton;
    private SideBar sideBar;  // 控制側邊欄按鈕的視覺變色
    private Canvas canvas;    // 用來確認滑鼠最後是不是放開在畫布範圍內
    private UMLModel model;

    public CreateObjectController(ObjectType objectType, JButton toolButton, SideBar sideBar, Canvas canvas, UMLModel model) {
        this.objectType = objectType;
        this.toolButton = toolButton;
        this.sideBar = sideBar;
        this.canvas = canvas;
        this.model = model;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        // 滑鼠在按鈕上按下去的瞬間，委派 View 改變視覺：讓這顆按鈕變黑
        sideBar.setActiveVisual(toolButton);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        // 取得滑鼠放開時在「整個螢幕」上的絕對座標
        Point screenPoint = mouseEvent.getLocationOnScreen();
        Point canvasPoint = new Point(screenPoint);
        
        // 將螢幕絕對座標，轉換成相對「畫布 (Canvas)」內部的座標
        SwingUtilities.convertPointFromScreen(canvasPoint, canvas);

        // 檢查滑鼠放開的位置是不是真的在畫布裡面
        if (canvas.contains(canvasPoint)) {
            // 呼叫工廠產生圖形，並加入 Model 中
            BaseObject newObject = ObjectFactory.createObject(objectType, canvasPoint.x, canvasPoint.y);
            model.addObject(newObject);
        }

        // 動作結束，把原本變黑的按鈕視覺還原
        sideBar.setActiveVisual(null); 
    }
}