import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UMLEditor extends JFrame {
    private CanvasArea canvas;
    private List<JButton> toolButtons = new ArrayList<>(); // 儲存按鈕以切換顏色

    public UMLEditor() {
        setTitle("Oops UML Editor");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 建立畫布 [cite: 5, 11]
        canvas = new CanvasArea();
        add(canvas, BorderLayout.CENTER);

        // 2. 頂部選單 [cite: 3]
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        
        // 建立選單項目變數，以便後續設定監聽器 [cite: 86, 128]
        JMenuItem groupItem = new JMenuItem("Group");
        JMenuItem ungroupItem = new JMenuItem("Ungroup");
        JMenuItem labelItem = new JMenuItem("Label");

        editMenu.add(groupItem);
        editMenu.add(ungroupItem);
        editMenu.add(labelItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        // --- 設定選單監聽器 ---
        groupItem.addActionListener(e -> canvas.groupSelected()); // [cite: 81, 86]
        ungroupItem.addActionListener(e -> canvas.ungroupSelected()); // [cite: 81, 90]
        
        labelItem.addActionListener(e -> {
            BaseObject target = null;
            int selectedCount = 0;
            for (BaseObject obj : canvas.getAllObjects()) {
                if (obj.isSelected()) {
                    target = obj;
                    selectedCount++;
                }
            }
            // 規格：僅在選取一個基本物件時有效 [cite: 89, 126]
            if (selectedCount == 1 && !(target instanceof CompositeObject)) {
                LabelDialog dialog = new LabelDialog(this, target.getLabel(), target.getColor());
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    target.setLabel(dialog.getEnteredName()); // [cite: 130, 132]
                    target.setColor(dialog.getSelectedColor()); // [cite: 131, 132]
                    canvas.repaint();
                }
            }
        });

        // 3. 左側工具列 
        JPanel sideBar = new JPanel(new GridLayout(6, 1, 5, 5));
        sideBar.setBackground(Color.LIGHT_GRAY);
        String[] btnNames = {"Select", "Association", "Generalization", "Composition", "Rect", "Oval"};
        
        for (String name : btnNames) {
            JButton btn = new JButton();
            btn.setIcon(new UMLIcon(name)); // 設定自定義圖示
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 加上細邊框
            btn.setBackground(Color.WHITE);
            // btn.setPreferredSize(new Dimension(80, 80)); // 強制按鈕大小
            
            // 預設將 "Select" 按鈕變黑 [cite: 12, 37]
            if (name.equals("Select")) btn.setBackground(Color.BLACK);

            btn.addActionListener(e -> {
                // 重置所有按鈕顏色 
                for (JButton b : toolButtons) b.setBackground(Color.WHITE);
                // 當前選取的按鈕變黑 
                btn.setBackground(Color.BLACK);

                // 切換模式邏輯
                if (name.equals("Rect") || name.equals("Oval")) {
                    canvas.setMode(new CreateMode(name, canvas));
                } else if (name.equals("Select")) {
                    canvas.setMode(new SelectMode(canvas));
                } else {
                    canvas.setMode(new LinkMode(canvas, name));
                }
            });
            
            toolButtons.add(btn);
            sideBar.add(btn);
        }
        add(sideBar, BorderLayout.WEST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UMLEditor().setVisible(true));
    }
}