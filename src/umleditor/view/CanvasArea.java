package umleditor.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JPanel;

import umleditor.model.ModelChangeListener;
import umleditor.model.UMLModel;
import umleditor.model.Drawable;

// 100% 純粹的渲染引擎：沒有 Controller、沒有具體圖形類別，只負責「畫」！
public class CanvasArea extends JPanel implements ModelChangeListener {
    private UMLModel model;

    private static final Color SELECTION_FILL_COLOR = new Color(0, 120, 215, 50);
    private static final Color SELECTION_BORDER_COLOR = new Color(0, 120, 215);
// 然後在 paintComponent 裡面使用它們
    // 視覺特效暫存 (View State)：這不屬於 Model，這是畫布的視覺暫留特效
    private Rectangle selectionArea = null;
    private Point tempLineStart = null;
    private Point tempLineEnd = null;


    public CanvasArea(UMLModel model) {
        this.model = model;
        this.model.addListener(this); // 訂閱 Model 的變更
        setBackground(Color.WHITE);
        setLayout(null);
    }

    // --- 給 Controller 控制視覺特效的接口 ---
    public void setSelectionArea(Rectangle area) {
        this.selectionArea = area;
        repaint();
    }

    public void setTempLine(Point start, Point end) {
        this.tempLineStart = start;
        this.tempLineEnd = end;
        repaint();
    }

    // --- 繪圖引擎核心 ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. 介面隔離原則 (ISP)：畫布完全不知道自己畫的是 Rect 還是 Link，它只知道它們是 Drawable
        for (Drawable item : model.getAllDrawables()) {
            item.draw(g);
        }

        // 2. 繪製暫時的拖曳連線 (Use Case D 特效)
        if (tempLineStart != null && tempLineEnd != null) {
            g.setColor(Color.BLACK);
            g.drawLine(tempLineStart.x, tempLineStart.y, tempLineEnd.x, tempLineEnd.y);
        }

        // 3. 繪製多選的藍色半透明框 (Use Case C 特效)
        if (selectionArea != null) {
            g.setColor(SELECTION_FILL_COLOR); // 半透明藍色
            g.fillRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
            g.setColor(SELECTION_BORDER_COLOR);     // 藍色邊框
            g.drawRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
        }
    }

    // --- 實作 Observer 的通知介面 ---
    @Override
    public void onModelChanged() {
        repaint(); // 資料更新了，通知 Swing 重繪
    }
}