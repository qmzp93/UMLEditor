import javax.swing.*;
import java.awt.*;

// 用於自定義 UML 物件名稱與顏色的彈出式對話框
public class LabelDialog extends JDialog {
    private JTextField nameField;
    private Color selectedColor;
    private boolean isConfirmed = false;    // 標記使用者是否按下 OK

    public LabelDialog(JFrame parent, String currentName, Color currentColor) {
        super(parent, "Customize Label Style", true);
        
        // 定義統一的尺寸規格
        Dimension inputSize = new Dimension(150, 30);  // Name 框與 Color 按鈕的大小
        Dimension buttonSize = new Dimension(100, 35); // OK 與 Cancel 按鈕的大小
        Color lightGray = new Color(220, 220, 220);    // 自定義淺灰色

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));  // 使用垂直佈局，讓各功能區域由上往下排
        contentPanel.setBackground(Color.GRAY); 
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));    // 設定邊距，避免元件貼齊邊框

        // Name 區域
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setOpaque(false); // 讓背景透明，透出 contentPanel 的灰色
        JLabel nameLabel = new JLabel("Name:     ");
        nameField = new JTextField(currentName);
        nameField.setPreferredSize(inputSize); // 設定框框大小
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Color 區域
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setOpaque(false);
        JLabel colorLabel = new JLabel("Color:    ");
        JButton colorBtn = new JButton("Select Color");
        selectedColor = currentColor;   // 預設為物件目前的顏色
        colorBtn.setBackground(selectedColor);
        colorBtn.setPreferredSize(inputSize);

        // 點擊後開啟 Swing 內建的調色盤對話框
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Choose Color", selectedColor);
            if (c != null) {
                selectedColor = c;
                colorBtn.setBackground(c);
            }
        });
        colorPanel.add(colorLabel);
        colorPanel.add(colorBtn);

        // 按鈕區域
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);
        
        JButton okBtn = new JButton("OK");
        okBtn.setPreferredSize(buttonSize); // 設定按鈕大小
        okBtn.setBackground(lightGray);     // 設定淺灰色
        okBtn.addActionListener(e -> { isConfirmed = true; setVisible(false); });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(buttonSize); // 設定與 OK 按鈕一樣大
        cancelBtn.setBackground(lightGray);     // 設定淺灰色
        cancelBtn.addActionListener(e -> { isConfirmed = false; setVisible(false); });

        btnPanel.add(cancelBtn);
        btnPanel.add(okBtn); 

        // --- 組裝面板，並加入垂直間距 (Strut) ---
        contentPanel.add(namePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(colorPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(btnPanel);

        add(contentPanel);
        pack(); // 根據內容自動調整視窗大小
        setLocationRelativeTo(parent);  // 置中於主視窗
    }

    // --- 提供給外部 (UMLEditor) 存取資料的方法 ---
    public String getEnteredName() { return nameField.getText(); } 
    public Color getSelectedColor() { return selectedColor; }
    public boolean isConfirmed() { return isConfirmed; }
}