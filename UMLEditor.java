import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// 專案的主程式視窗，負責 UI 配置與模式切換管理
public class UMLEditor extends JFrame {
    private CanvasArea canvas;
    private List<JButton> toolButtons = new ArrayList<>(); /// 所有的左側工具按鈕
    private JButton activeToolButton;   // 紀錄目前「真正」選中的模式按鈕 (例如 Select 或 Link)
    private Mode activeMode;            // 紀錄目前「真正」選中的模式物件 (用於 State Pattern)

    public UMLEditor() {
        setTitle("Oops UML Editor");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 初始化中央畫布區域
        canvas = new CanvasArea();
        add(canvas, BorderLayout.CENTER);

        // 建立頂部選單列
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        
        JMenuItem groupItem = new JMenuItem("Group");
        JMenuItem ungroupItem = new JMenuItem("Ungroup");
        JMenuItem labelItem = new JMenuItem("Label");

        editMenu.add(groupItem);
        editMenu.add(ungroupItem);
        editMenu.add(labelItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        // --- 設定 Edit 選單項目監聽器 ---
        groupItem.addActionListener(e -> canvas.groupSelected());
        ungroupItem.addActionListener(e -> canvas.ungroupSelected());
        
        labelItem.addActionListener(e -> {
            BaseObject target = null;
            int selectedCount = 0;
            // 找出目前畫布上被選取的唯一物件
            for (BaseObject obj : canvas.getAllObjects()) {
                if (obj.isSelected()) {
                    target = obj;
                    selectedCount++;
                }
            }
            // Use Case G: 限制僅能修改單一基本物件 (不能修改群組)
            if (selectedCount == 1 && !(target instanceof CompositeObject)) {
                LabelDialog dialog = new LabelDialog(this, target.getLabel(), target.getColor());
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    target.setLabel(dialog.getEnteredName());
                    target.setColor(dialog.getSelectedColor());
                    canvas.repaint();
                }
            }
        });

        // 建立左側工具列 (側邊欄)
        JPanel sideBar = new JPanel(new GridLayout(6, 1, 5, 5));
        sideBar.setBackground(Color.LIGHT_GRAY);
        String[] btnNames = {"Select", "Association", "Generalization", "Composition", "Rect", "Oval"};
        
        for (String name : btnNames) {
            JButton btn = new JButton();
            btn.setIcon(new UMLIcon(name)); // 使用自定義 Icon 繪圖
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);

            btn.setContentAreaFilled(false);     // 禁用預設的按鈕區域填色邏輯
            btn.setOpaque(true);          // 確保按鈕會畫出我們設定的背景色

            toolButtons.add(btn);
            sideBar.add(btn);

            // 初始化：預設選取 Select 模式
            if (name.equals("Select")) {
                activeToolButton = btn;
                activeMode = new SelectMode(canvas);
                btn.setBackground(Color.BLACK);
                canvas.setMode(activeMode);
            }

            // --- 實作 Use Case A: 拖曳建立物件邏輯 ---
            if (name.equals("Rect") || name.equals("Oval")) {
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        // 當滑鼠在按鈕上按下，進入預備建立狀態，按鈕變黑
                        for (JButton b : toolButtons) b.setBackground(Color.WHITE);
                        btn.setBackground(Color.BLACK);
                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        // 取得放開滑鼠時的螢幕座標，並轉換為 Canvas 相對座標
                        Point screenPoint = e.getLocationOnScreen();
                        Point canvasPoint = new Point(screenPoint);
                        javax.swing.SwingUtilities.convertPointFromScreen(canvasPoint, canvas);

                        // 如果滑鼠是在畫布範圍內放開，則執行建立
                        if (canvas.getBounds().contains(canvasPoint)) {
                            BaseObject obj = name.equals("Rect") ? 
                                new RectObject(canvasPoint.x, canvasPoint.y) : 
                                new OvalObject(canvasPoint.x, canvasPoint.y);
                            canvas.addObject(obj);
                        }

                        // 建立動作完成後，自動恢復到之前的模式
                        restorePreviousMode();
                    }
                });
            } else {
                // Select 與三種 Link 模式則使用點擊切換邏輯
                btn.addActionListener(e -> {
                    activeToolButton = btn;
                    if (name.equals("Select")) {
                        activeMode = new SelectMode(canvas);
                    } else {
                        activeMode = new LinkMode(canvas, name);
                        canvas.unselectAll(); 
                    }
                    
                    restorePreviousMode();
                });
            }
        }
        add(sideBar, BorderLayout.WEST);
    }

    
    // 處理 UI 按鈕顏色與畫布模式的恢復
    private void restorePreviousMode() {
        for (JButton b : toolButtons) b.setBackground(Color.WHITE);
        if (activeToolButton != null) {
            activeToolButton.setBackground(Color.BLACK);
        }
        canvas.setMode(activeMode);
    }

    public static void main(String[] args) {
        // 使用 Event Dispatch Thread 啟動 Swing 程式以確保執行緒安全
        SwingUtilities.invokeLater(() -> new UMLEditor().setVisible(true));
    }
}