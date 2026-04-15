import java.awt.Point;
import java.awt.event.MouseEvent;

public class LinkMode implements Mode {
    private CanvasArea canvas;
    private String type;
    private BaseObject startObj = null;
    private int startPortIndex = -1;
    private Point currentMousePoint = null; // 用於繪製暫時的線

    public LinkMode(CanvasArea canvas, String type) {
        this.canvas = canvas;
        this.type = type;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 1. 尋找滑鼠點擊處的物件
        BaseObject obj = canvas.findObjectAt(e.getX(), e.getY());
        
        if (obj != null) {
            // 2. 尋找最近的 Port (需判斷座標是否在 Port 範圍內) [cite: 57]
            int portIdx = findPortAt(obj, e.getPoint());
            if (portIdx != -1) {
                startObj = obj;
                startPortIndex = portIdx;
                currentMousePoint = e.getPoint();
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
            BaseObject endObj = canvas.findObjectAt(e.getX(), e.getY());
            
            // 規則：起點終點不能是同一個物件，且終點必須在一個 Port 上 
            if (endObj != null && endObj != startObj) {
                int endPortIdx = findPortAt(endObj, e.getPoint());
                if (endPortIdx != -1) {
                    // 建立永久連線 [cite: 52]
                    canvas.addLink(new Link(startObj, startPortIndex, endObj, endPortIdx, type));
                }
            }
        }
        // 重置狀態
        startObj = null;
        canvas.setTempLine(null, null);
        canvas.repaint();
    }

    private int findPortAt(BaseObject obj, Point p) {
        java.util.List<Point> ports = obj.getPorts();
        for (int i = 0; i < ports.size(); i++) {
            Point port = ports.get(i);
            // 判斷滑鼠是否在 Port 的感應範圍內
            if (p.distance(port) < 17) return i;
        }
        return -1;
    }
}