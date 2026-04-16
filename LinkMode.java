import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

// 處理建立連線的狀態類別
public class LinkMode implements Mode {
    private CanvasArea canvas;
    private String type;    
    private BaseObject startObj = null;
    private int startPortIndex = -1;
    private Point currentMousePoint = null;

    public LinkMode(CanvasArea canvas, String type) {
        this.canvas = canvas;
        this.type = type;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 從最後加入的物件（最上層）開始往前找，以符合視覺上的深度優先
        List<BaseObject> allObjects = canvas.getAllObjects();
        for (int i = allObjects.size() - 1; i >= 0; i--) {
            BaseObject obj = allObjects.get(i);
            
            // 直接檢查這個物件的 Ports 有沒有被點到
            int portIdx = findPortAt(obj, e.getPoint());
            
            if (portIdx != -1) {
                // 只要點到任何物件的 Port，就立刻鎖定起點並結束搜尋
                startObj = obj;
                startPortIndex = portIdx;
                currentMousePoint = e.getPoint();
                return; // 找到了就跳出方法，不再繼續往下找
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startObj != null) {
            currentMousePoint = e.getPoint();
            canvas.setTempLine(startObj.getPorts().get(startPortIndex), currentMousePoint);
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (startObj != null) {
            BaseObject targetEndObj = null;
            int endPortIdx = -1;

            List<BaseObject> allObjects = canvas.getAllObjects();
            // 從最上層物件開始往前找
            for (int i = allObjects.size() - 1; i >= 0; i--) {
                BaseObject obj = allObjects.get(i);
                
                // 規則：終點物件不能跟起點物件一樣 (禁止自連)
                if (obj == startObj) continue;

                int portIdx = findPortAt(obj, e.getPoint());
                if (portIdx != -1) {
                    targetEndObj = obj;
                    endPortIdx = portIdx;
                    break; // 鎖定第一個找到的 Port
                }
            }

            // 如果成功找到符合條件的終點 Port，則建立永久連線
            if (targetEndObj != null && endPortIdx != -1) {
                canvas.addLink(new Link(startObj, startPortIndex, targetEndObj, endPortIdx, type));
            }
        }
        // --- 結束動作後的清理 ---
        startObj = null;
        startPortIndex = -1;
        canvas.setTempLine(null, null);
        canvas.repaint();
    }

    private int findPortAt(BaseObject obj, Point p) {
        List<Point> ports = obj.getPorts();
        int offset = obj.getPortSize() / 2 + 3;

        for (int i = 0; i < ports.size(); i++) {
            Point port = ports.get(i);
            // 檢查滑鼠點 p 是否落在正方形的 [x - offset, x + offset] 與 [y - offset, y + offset] 之間
            boolean inX = (p.x >= port.x - offset && p.x <= port.x + offset);
            boolean inY = (p.y >= port.y - offset && p.y <= port.y + offset);
            if (inX && inY) return i;
        }
        return -1;
    }
}