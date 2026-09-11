package umleditor.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import umleditor.model.UMLModel;

public class MainFrame extends JFrame {
    private CanvasArea canvas;
    private UMLSideBar sideBar;
    private UMLMenuBar menuBar; // 抽成屬性

    public MainFrame(UMLModel model) {
        setTitle("Oops UML Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        canvas = new CanvasArea(model);
        sideBar = new UMLSideBar();
        menuBar = new UMLMenuBar(); // 初始化純 View 選單

        add(canvas, BorderLayout.CENTER);
        add(sideBar, BorderLayout.WEST);
        setJMenuBar(menuBar); 
    }

    public CanvasArea getCanvas() { return canvas; }
    public UMLSideBar getSideBar() { return sideBar; }
    public UMLMenuBar getUMLMenuBar() { return menuBar; }
}