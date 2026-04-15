import javax.swing.*;
import java.awt.*;

public class UMLIcon implements Icon {
    private String type;
    private int width = 60;  // 放大寬度
    private int height = 60; // 放大高度

    public UMLIcon(String type) {
        this.type = type;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 如果背景是黑色，圖示就用白色，否則用深灰色
        if (c.getBackground().equals(Color.BLACK)) {
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.DARK_GRAY);
        }

        switch (type) {
            case "Select": // 繪製游標箭頭 [cite: 4]
                int[] px = {x+10, x+19, x+26, x+38, x+52, x+40, x+47};
                int[] py = {y+10, y+49, y+42, y+54, y+40, y+28, y+21};
                g2.fillPolygon(px, py, 7);
                break;
            case "Association": // 繪製實線箭頭 [cite: 6]
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x+50, y+30, x+10, y+30);
                g2.drawLine(x+10, y+30, x+24, y+17);
                g2.drawLine(x+10, y+30, x+24, y+43);
                break;
            case "Generalization": // 繪製空心三角形箭頭 [cite: 7]
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x+50, y+30, x+25, y+30);
                Polygon tri = new Polygon(new int[]{x+10, x+25, x+25}, new int[]{y+30, y+18, y+42}, 3);
                g2.drawPolygon(tri);
                break;
            case "Composition": // 繪製空心菱形箭頭 [cite: 8]
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x+50, y+30, x+34, y+30);
                Polygon diamond = new Polygon(new int[]{x+10, x+22, x+34, x+22}, new int[]{y+30, y+18, y+30, y+42}, 4);
                g2.drawPolygon(diamond);break;
            case "Rect": // 繪製實心矩形 [cite: 9, 16]
                g2.fillRect(x+15, y+15, 30, 30);
                break;
            case "Oval": // 繪製實心圓形 [cite: 10, 17]
                g2.fillOval(x+15, y+15, 32, 32);
                break;
        }
        g2.dispose();
    }

    @Override public int getIconWidth() { return width; }
    @Override public int getIconHeight() { return height; }
}