package umleditor.controller;

import umleditor.model.UMLModel;
import umleditor.view.MainFrame;

public class MainController {
    private UMLModel model;
    private MainFrame view;

    public MainController(UMLModel model, MainFrame view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        // 初始化畫布的滑鼠總路由
        CanvasController canvasController = new CanvasController(model);
        view.getCanvas().addMouseListener(canvasController);
        view.getCanvas().addMouseMotionListener(canvasController);

        // 將 Sidebar 的事件分配給專屬的 Sidebar 控制器
        new SideBarController(model, view.getSideBar(), view.getCanvas(), canvasController);

        // 將 MenuBar 的事件分配給專屬的 MenuBar 控制器
        new MenuBarController(model, view.getUMLMenuBar(), view);

        view.setVisible(true);  // 顯示視窗
    }
}