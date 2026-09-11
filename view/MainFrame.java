package umleditor.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import umleditor.model.UMLModel;

// 負責將 Canvas, SideBar, MenuBar 三大組件組裝在一起
public class MainFrame extends JFrame {
    private Canvas canvas;
    private SideBar sideBar;
    private MenuBar menuBar; 

    public MainFrame(UMLModel model) {
        setTitle("Oops UML Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 設定關閉視窗時，程式要完全結束
        setLayout(new BorderLayout());

        // 初始化三大元件
        canvas = new Canvas(model);
        sideBar = new SideBar();
        menuBar = new MenuBar(); 

        // 進行拼裝
        add(canvas, BorderLayout.CENTER);
        add(sideBar, BorderLayout.WEST);
        setJMenuBar(menuBar);
    }

    //  公開接口：給 MainController 撈取子元件並分發給各個子 Controller
    public Canvas getCanvas() { return canvas; }
    public SideBar getSideBar() { return sideBar; }
    public MenuBar getUMLMenuBar() { return menuBar; }
}