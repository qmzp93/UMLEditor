package umleditor.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JPanel;

import umleditor.model.ModelChangeListener;
import umleditor.model.UMLModel;
import umleditor.model.objects.BaseObject;
import umleditor.model.Drawable;

// 負責繪製圖形與暫時性的視覺特效
public class Canvas extends JPanel implements ModelChangeListener {
    private final UMLModel model;

    private static final Color SELECTION_FILL_COLOR = new Color(0, 120, 215, 50);
    private static final Color SELECTION_BORDER_COLOR = new Color(0, 120, 215);
    private static final Color TEMP_LINE_COLOR = Color.BLACK;

    // 視覺特效暫存狀態 (這些狀態只跟「畫面顯示」有關，不屬於 Model 領域資料)
    private Rectangle selectionArea = null;
    private Point tempLineStart = null;
    private Point tempLineEnd = null;

    public Canvas(UMLModel model) {
        this.model = model;
        this.model.addListener(this); // 關鍵：把自己註冊給 Model 當觀察者
        setBackground(Color.WHITE);
        setLayout(null);
    }

    // 提供給 SelectMode 呼叫：更新並繪製多選藍框
    public void setSelectionArea(Rectangle area) {
        this.selectionArea = area;
        repaint();
    }

    // 提供給 LinkMode 呼叫：更新並繪製拖曳線
    public void setTempLine(Point start, Point end) {
        this.tempLineStart = start;
        this.tempLineEnd = end;
        repaint();
    }

    // 當畫面需要更新時，系統會自動呼叫這個方法
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics); // 清除舊畫面與粉刷背景

        // 多型繪圖：無腦遍歷所有的 Drawable 物件 (包含實體物件與連線)
        for (Drawable item : model.getAllDrawables()) {
            item.draw(graphics);
        }

        // 繪製圖形的 Ports
        for (BaseObject obj : model.getAllObjects()) {
            obj.drawPorts(graphics); // 內部有選取狀態判斷，只有被選中/Hover 的物件才會真的畫出來
        }

        // 繪製暫時性的拖曳線
        if (tempLineStart != null && tempLineEnd != null) {
            graphics.setColor(TEMP_LINE_COLOR);
            graphics.drawLine(tempLineStart.x, tempLineStart.y, tempLineEnd.x, tempLineEnd.y);
        }

        // 繪製暫時性的藍色多選框
        if (selectionArea != null) {
            graphics.setColor(SELECTION_FILL_COLOR);
            graphics.fillRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
            graphics.setColor(SELECTION_BORDER_COLOR);
            graphics.drawRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
        }
    }

    // 實作觀察者介面的方法：當 Model 發出資料變更通知時，立刻重繪整個畫布
    @Override
    public void onModelChanged() {
        repaint();
    }
}