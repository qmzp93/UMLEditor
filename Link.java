import java.awt.*;

public class Link {
    private BaseObject startObj, endObj;
    private int startPortIndex, endPortIndex;
    private String type; // "Association", "Generalization", "Composition"

    public Link(BaseObject startObj, int startPortIndex, BaseObject endObj, int endPortIndex, String type) {
        this.startObj = startObj;
        this.startPortIndex = startPortIndex;
        this.endObj = endObj;
        this.endPortIndex = endPortIndex;
        this.type = type;
    }

    public void draw(Graphics g) {
        // 從物件中取得最新的 Port 座標
        Point p1 = startObj.getPorts().get(startPortIndex);
        Point p2 = endObj.getPorts().get(endPortIndex);

        g.setColor(Color.BLACK);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);

        double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        int size = 15;

        switch (type) {
            case "Association": // 一般箭頭
                drawArrowHead(g, p2, angle, size);
                break;
            case "Generalization": // 空心三角形 [cite: 1, 41]
                drawTriangleHead(g, p2, angle, size);
                break;
            case "Composition": // 實心菱形 [cite: 8]
                drawDiamondHead(g, p2, angle, size);
                break;
        }
    }
    // 在 Link.java 類別中新增此方法
    private void drawArrowHead(Graphics g, Point p, double angle, int size) {
        // 計算箭頭兩翼的座標
        int x1 = (int) (p.x - size * Math.cos(angle - 0.5));
        int y1 = (int) (p.y - size * Math.sin(angle - 0.5));
        int x2 = (int) (p.x - size * Math.cos(angle + 0.5));
        int y2 = (int) (p.y - size * Math.sin(angle + 0.5));

        // 繪製兩條斜線形成箭頭
        g.drawLine(p.x, p.y, x1, y1);
        g.drawLine(p.x, p.y, x2, y2);
    }

    private void drawTriangleHead(Graphics g, Point p, double angle, int size) {
        Polygon poly = new Polygon();
        poly.addPoint(p.x, p.y);
        poly.addPoint((int)(p.x - size * Math.cos(angle - 0.5)), (int)(p.y - size * Math.sin(angle - 0.5)));
        poly.addPoint((int)(p.x - size * Math.cos(angle + 0.5)), (int)(p.y - size * Math.sin(angle + 0.5)));
        g.setColor(Color.WHITE);
        g.fillPolygon(poly);
        g.setColor(Color.BLACK);
        g.drawPolygon(poly);
    }

    private void drawDiamondHead(Graphics g, Point p, double angle, int size) {
        Polygon poly = new Polygon();
        poly.addPoint(p.x, p.y);
        poly.addPoint((int)(p.x - size * Math.cos(angle - 0.5)), (int)(p.y - size * Math.sin(angle - 0.5)));
        poly.addPoint((int)(p.x - 2 * size * Math.cos(angle)), (int)(p.y - 2 * size * Math.sin(angle)));
        poly.addPoint((int)(p.x - size * Math.cos(angle + 0.5)), (int)(p.y - size * Math.sin(angle + 0.5)));
        g.setColor(Color.WHITE);
        g.fillPolygon(poly); // 規格書圖示為白色填滿黑框 [cite: 1, 8]
        g.setColor(Color.BLACK);
        g.drawPolygon(poly);
    }
}