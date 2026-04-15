import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

// 負責物件的繪製、管理，以及滑鼠事件的轉發
class CanvasArea extends JPanel {
    // 儲存所有 UML 物件，索引越大的繪製在越上層 (Z-order)
    private List<BaseObject> objects = new ArrayList<>();
    private List<Link> links = new ArrayList<>();
    private Mode currentMode;   // 目前正處於哪一種操作模式
    private Rectangle selectionArea = null;
    private Point tempStart, tempEnd;

    public CanvasArea() {
        setBackground(Color.WHITE);

        // 使用 MouseAdapter 同時處理點擊與拖曳動作
        MouseAdapter mouseAdapter = new MouseAdapter() {
            // --- 委派模式：將滑鼠事件轉交給目前的 Mode 處理 ---
            @Override public void mousePressed(MouseEvent e) { if(currentMode != null) currentMode.mousePressed(e); }
            @Override public void mouseReleased(MouseEvent e) { if(currentMode != null) currentMode.mouseReleased(e); }
            @Override public void mouseDragged(MouseEvent e) { if(currentMode != null) currentMode.mouseDragged(e); }
            
            // 處理滑鼠懸停效果 (Hovering)
            @Override
            public void mouseMoved(MouseEvent e) {
                // 只有在「選取」或「連線」模式下才觸發 Port 的顯示
                if (currentMode instanceof SelectMode || currentMode instanceof LinkMode) {
                    BaseObject hovered = findObjectAt(e.getX(), e.getY());
                    for (BaseObject obj : objects) {
                        obj.setHovered(obj == hovered);
                    }
                    repaint();
                } else {
                    // 其餘建立模式 (如 Rect/Oval) 強制清除 Hover
                    clearHover();
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        clearHover();
    }

    public void setSelectionArea(Rectangle area) {
        this.selectionArea = area;
        repaint();
    }

    private void clearHover() {
        boolean changed = false;
        for (BaseObject obj : objects) {
            if (obj.isHovered()) {
                obj.setHovered(false);
                changed = true;
            }
        }
        if (changed) repaint();
    }

    public void addObject(BaseObject obj) {
        objects.add(obj);
        repaint();
    }

    public void addLink(Link link) {
        links.add(link);
        repaint();
    }

    public void setTempLine(Point start, Point end) {
        this.tempStart = start;
        this.tempEnd = end;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (BaseObject obj : objects) {
            obj.draw(g);
        }

        for (Link link : links) {
            link.draw(g);
        }

        if (tempStart != null && tempEnd != null) {
            g.setColor(Color.GRAY);
            g.drawLine(tempStart.x, tempStart.y, tempEnd.x, tempEnd.y);
        }

        if (selectionArea != null) {
            // 使用半透明藍色填充
            g.setColor(new Color(0, 120, 215, 30)); 
            g.fillRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
            
            // 繪製藍色虛線或實線外框
            g.setColor(new Color(0, 120, 215));
            g.drawRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
        }

    }

    
    // 尋找被點擊的物件
    public BaseObject findObjectAt(int x, int y) {
        // 必須「由後往前」找
        for (int i = objects.size() - 1; i >= 0; i--) {
            BaseObject obj = objects.get(i);
            if (obj.contains(x, y)) {
                return obj;
            }
        }
        return null;
    }
    
    public List<BaseObject> getAllObjects() {
        return objects;
    }

    // 取消畫面上所有物件的選取狀態
    public void unselectAll() {
        for (BaseObject obj : objects) {
            obj.setSelected(false);
        }
        repaint();
    }

    // 將特定物件移到清單末尾，使其在畫面中顯示在最上層 
    public void moveObjectToFront(BaseObject obj) {
        if (objects.remove(obj)) {
            objects.add(obj); // 加到 List 末尾即為最上層
        }
        repaint();
    }

    // Use Case D: 群組功能
    public void groupSelected() {
        List<BaseObject> selected = new ArrayList<>();
        for (BaseObject obj : objects) {
            if (obj.isSelected()) selected.add(obj);
        }

        if (selected.size() >= 2) {
            CompositeObject composite = new CompositeObject();
            // 先從主清單中一次全部移除，避免在迴圈中修改清單
            objects.removeAll(selected); 
            
            for (BaseObject obj : selected) {
                obj.setSelected(false);     // 群組後，子物件本身不該處於 selected 狀態
                composite.addComponent(obj);
            }
            
            composite.setSelected(true);   // 群組後預設為選取狀態
            objects.add(composite);
            repaint();
        }
    }

    // Use Case D: 解群組功能
    public void ungroupSelected() {
        BaseObject target = null;
        int count = 0;
        for (BaseObject obj : objects) {
            if (obj.isSelected()) {
                target = obj;
                count++;
            }
        }

        // 只有在單獨選取一個 CompositeObject 時才能解群組
        if (count == 1 && target instanceof CompositeObject) {
            CompositeObject composite = (CompositeObject) target;
            objects.remove(composite);
            for (BaseObject child : composite.getChildren()) {
                child.setSelected(true);    // 解開後子物件自動設為選取狀態
                objects.add(child);     // 將子物件回歸主清單
            }
            repaint();
        }
    }
}