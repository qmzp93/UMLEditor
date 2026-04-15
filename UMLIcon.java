import javax.swing.*;
import java.awt.*;

// 負責在工具列按鈕上繪製 UML 相關符號的類別
public class UMLIcon implements Icon {
    private String type;     // 圖示類型 (例如 "Select", "Rect" 等)
    private int width = 60;  // 圖示預設寬度
    private int height = 60; // 圖示預設高度

    public UMLIcon(String type) {
        this.type = type;
    }

    /**
     * 實際執行繪圖的方法
     * @param c 正在繪製此圖示的元件 (即 JButton)
     * @param g 繪圖工具箱
     * @param x 圖示起始 X 座標
     * @param y 圖示起始 Y 座標
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        // 將 Graphics 轉換為更強大的 Graphics2D
        // 使用 .create() 建立副本，避免修改到其他元件的繪圖設定
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    // 開啟反鋸齒，讓圓形和斜線更平滑
        
        // 檢查按鈕目前的背景色：如果是黑色(選中狀態)，圖示用白色；否則用深灰色
        if (c.getBackground().equals(Color.BLACK)) {
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.DARK_GRAY);
        }

        switch (type) {
            case "Select": // 繪製游標箭頭
                int[] px = {x+10, x+19, x+26, x+38, x+52, x+40, x+47};
                int[] py = {y+10, y+49, y+42, y+54, y+40, y+28, y+21};
                g2.fillPolygon(px, py, 7);
                break;
            case "Association": // 繪製實線箭頭
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x+50, y+30, x+10, y+30);
                g2.drawLine(x+10, y+30, x+24, y+17);
                g2.drawLine(x+10, y+30, x+24, y+43);
                break;
            case "Generalization": // 繪製空心三角形箭頭
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x+50, y+30, x+25, y+30);
                Polygon tri = new Polygon(new int[]{x+10, x+25, x+25}, new int[]{y+30, y+18, y+42}, 3);
                g2.drawPolygon(tri);    // 繪製空心多邊形
                break;
            case "Composition": // 繪製空心菱形箭頭
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x+50, y+30, x+34, y+30);
                Polygon diamond = new Polygon(new int[]{x+10, x+22, x+34, x+22}, new int[]{y+30, y+18, y+30, y+42}, 4);
                g2.drawPolygon(diamond);    // 繪製菱形
                break;
            case "Rect": // 繪製實心矩形
                g2.fillRect(x+15, y+15, 30, 30);
                break;
            case "Oval": // 繪製實心圓形
                g2.fillOval(x+15, y+15, 32, 32);
                break;
        }
        g2.dispose();   // 釋放繪圖資源
    }

    @Override public int getIconWidth() { return width; }
    @Override public int getIconHeight() { return height; }
}