package umleditor.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import javax.swing.Icon;

// 向量符號繪製機 (ToolIcon)：負責在工具列按鈕上，用高畫質向量線條畫出 UML 相關符號
public class ToolIcon implements Icon {
    
    private static final int DEFAULT_ICON_WIDTH = 60;
    private static final int DEFAULT_ICON_HEIGHT = 60;
    private static final int LINE_THICKNESS = 2; // 線條粗細
    private static final Color DEFAULT_ICON_COLOR = Color.DARK_GRAY;
    private static final Color SELECTED_ICON_COLOR = Color.WHITE;

    private final String iconType; // 記錄這個圖示要畫什麼 (Select, Association, Rect...)

    public ToolIcon(String iconType) {
        this.iconType = iconType;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        

        // 如果按鈕變黑了 (代表被選中)，圖示就用白色畫；反之，用深灰色畫。
        if (component.getBackground().equals(Color.BLACK)) {
            graphics2D.setColor(SELECTED_ICON_COLOR);
        } else {
            graphics2D.setColor(DEFAULT_ICON_COLOR);
        }

        // 根據類型，利用絕對幾何座標畫出對應的向量圖形
        switch (iconType) {
            case "Select":
                int[] pointsX = {x+10, x+19, x+26, x+38, x+52, x+40, x+47};
                int[] pointsY = {y+10, y+49, y+42, y+54, y+40, y+28, y+21};
                graphics2D.fillPolygon(pointsX, pointsY, 7);
                break;
                
            case "Association":
                graphics2D.setStroke(new BasicStroke(LINE_THICKNESS));
                graphics2D.drawLine(x+50, y+30, x+10, y+30);
                graphics2D.drawLine(x+10, y+30, x+24, y+17);
                graphics2D.drawLine(x+10, y+30, x+24, y+43);
                break;
                
            case "Generalization":
                graphics2D.setStroke(new BasicStroke(LINE_THICKNESS));
                graphics2D.drawLine(x+50, y+30, x+25, y+30);
                Polygon triangle = new Polygon(new int[]{x+10, x+25, x+25}, new int[]{y+30, y+18, y+42}, 3);
                graphics2D.drawPolygon(triangle);
                break;
                
            case "Composition":
                graphics2D.setStroke(new BasicStroke(LINE_THICKNESS));
                graphics2D.drawLine(x+50, y+30, x+34, y+30);
                Polygon diamond = new Polygon(new int[]{x+10, x+22, x+34, x+22}, new int[]{y+30, y+18, y+30, y+42}, 4);
                graphics2D.drawPolygon(diamond);
                break;
                
            case "Rect":
                graphics2D.fillRect(x+15, y+15, 30, 30);
                break;
                
            case "Oval":
                graphics2D.fillOval(x+15, y+15, 32, 32);
                break;
        }
        
        graphics2D.dispose(); // 釋放記憶體資源
    }

    @Override public int getIconWidth() { return DEFAULT_ICON_WIDTH; }
    @Override public int getIconHeight() { return DEFAULT_ICON_HEIGHT; }
}