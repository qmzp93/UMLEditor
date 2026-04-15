import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

class CanvasArea extends JPanel {
    // 儲存所有物件，索引越大的繪製在越上層
    private List<BaseObject> objects = new ArrayList<>();
    private List<Link> links = new ArrayList<>(); // 永久連線清單
    private Mode currentMode;
    private Rectangle selectionArea = null;
    private Point tempStart, tempEnd;

    public CanvasArea() {
        setBackground(Color.WHITE);
        // setBorder(BorderFactory.createLineBorder(Color.BLACK));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { if(currentMode != null) currentMode.mousePressed(e); }
            @Override public void mouseReleased(MouseEvent e) { if(currentMode != null) currentMode.mouseReleased(e); }
            @Override public void mouseDragged(MouseEvent e) { if(currentMode != null) currentMode.mouseDragged(e); }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // 只有在 SelectMode (選取) 或 LinkMode (連線) 時才顯示 Port
                // 這樣在 CreateMode (Rect/Oval) 下就不會出現 Port
                if (currentMode instanceof SelectMode || currentMode instanceof LinkMode) {
                    BaseObject hovered = findObjectAt(e.getX(), e.getY());
                    for (BaseObject obj : objects) {
                        obj.setHovered(obj == hovered);
                    }
                    repaint();
                } else {
                    // 在其他模式（如建立物件模式）下，強制清除懸停狀態
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

        // 1. 先畫連線 (讓線在物件下方)
        for (Link link : links) {
            link.draw(g);
        }

        // 2. 畫拖曳中的暫時線 [cite: 49]
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

        // 3. 畫物件
        for (BaseObject obj : objects) {
            obj.draw(g);
        }
    }
    // 尋找被點擊的物件（從最上層開始找）
    public BaseObject findObjectAt(int x, int y) {
        // 從列表末尾遍歷到開頭 
        for (int i = objects.size() - 1; i >= 0; i--) {
            BaseObject obj = objects.get(i);
            if (obj.contains(x, y)) {
                return obj;
            }
        }
        return null;
    }
    
    // 新增這個方法，讓 Mode 可以取得目前畫布上的所有物件
    public List<BaseObject> getAllObjects() {
        return objects;
    }

    // 取消所有物件的選取狀態 [cite: 72, 75]
    public void unselectAll() {
        for (BaseObject obj : objects) {
            obj.setSelected(false);
        }
        repaint();
    }

    // 將物件移到最上層 
    public void moveObjectToFront(BaseObject obj) {
        if (objects.remove(obj)) {
            objects.add(obj); // 加到 List 末尾即為最上層
        }
        repaint();
    }

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
                obj.setSelected(false); // 群組後，子物件本身不該處於 selected 狀態
                composite.addComponent(obj);
            }
            
            composite.setSelected(true);
            objects.add(composite);
            repaint();
        }
    }

    public void ungroupSelected() {
        BaseObject target = null;
        int count = 0;
        for (BaseObject obj : objects) {
            if (obj.isSelected()) {
                target = obj;
                count++;
            }
        }

        // 只有當選取唯一一個 Composite 時才執行 [cite: 89, 95]
        if (count == 1 && target instanceof CompositeObject) {
            CompositeObject composite = (CompositeObject) target;
            objects.remove(composite);
            for (BaseObject child : composite.getChildren()) {
                child.setSelected(true);
                objects.add(child);
            }
            repaint();
        }
    }
}