package umleditor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// 彈出式屬性對話框 (LabelDialog)：當使用者點擊 Label 功能時，跳出負責修改物件文字與背景顏色的視窗
public class LabelDialog extends JDialog {
    private static final Dimension INPUT_COMPONENT_SIZE = new Dimension(150, 30);
    private static final Dimension CONFIRM_BUTTON_SIZE = new Dimension(100, 35);
    
    private static final Color DIALOG_BACKGROUND_COLOR = Color.GRAY;
    private static final Color LIGHT_GRAY_BUTTON_COLOR = new Color(220, 220, 220);
    
    private static final int BORDER_PADDING = 20;
    private static final int BORDER_SIDE_PADDING = 30;
    private static final int SMALL_ELEMENT_GAP = 10;
    private static final int LARGE_ELEMENT_GAP = 20;
    private static final int BUTTON_PANEL_GAP = 20;

    private JTextField nameField;
    private Color selectedColor;
    private boolean isConfirmed = false;

    public LabelDialog(JFrame parentFrame, String currentName, Color currentColor) {
        // 設定為 Modal 視窗 (第三個參數為 true)，代表這個彈窗不關掉的話，使用者無法點擊後方的主視窗
        super(parentFrame, "Customize Label Style", true);
        
        // 主內容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(DIALOG_BACKGROUND_COLOR); 
        contentPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_PADDING, BORDER_SIDE_PADDING, BORDER_PADDING, BORDER_SIDE_PADDING));

        // Name 文字輸入區域
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Name:     ");
        nameField = new JTextField(currentName);
        nameField.setPreferredSize(INPUT_COMPONENT_SIZE);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Color 顏色選取區域
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setOpaque(false);
        JLabel colorLabel = new JLabel("Color:    ");
        JButton colorBtn = new JButton("Select Color");
        selectedColor = currentColor;
        colorBtn.setBackground(selectedColor);
        colorBtn.setPreferredSize(INPUT_COMPONENT_SIZE);

        // 點擊顏色按鈕時，調用 Java 內建的調色盤 JColorChooser
        colorBtn.addActionListener(e -> {
            Color choosedColor = JColorChooser.showDialog(this, "Choose Color", selectedColor);
            if (choosedColor != null) {
                selectedColor = choosedColor;
                colorBtn.setBackground(choosedColor); // 即時更新按鈕顏色
            }
        });
        colorPanel.add(colorLabel);
        colorPanel.add(colorBtn);

        // 下方確定/取消按鈕區域
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, BUTTON_PANEL_GAP, SMALL_ELEMENT_GAP));
        btnPanel.setOpaque(false);
        
        JButton okBtn = new JButton("OK");
        okBtn.setPreferredSize(CONFIRM_BUTTON_SIZE);
        okBtn.setBackground(LIGHT_GRAY_BUTTON_COLOR);
        okBtn.addActionListener(e -> { isConfirmed = true; setVisible(false); }); // 標記為確認，並關閉視窗
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(CONFIRM_BUTTON_SIZE);
        cancelBtn.setBackground(LIGHT_GRAY_BUTTON_COLOR);
        cancelBtn.addActionListener(e -> { isConfirmed = false; setVisible(false); }); // 標記為取消，並關閉視窗

        btnPanel.add(cancelBtn);
        btnPanel.add(okBtn); 

        // --- 將三大面板組裝起來，中間塞入空白間距 (Strut) ---
        contentPanel.add(namePanel);
        contentPanel.add(Box.createVerticalStrut(SMALL_ELEMENT_GAP));
        contentPanel.add(colorPanel);
        contentPanel.add(Box.createVerticalStrut(LARGE_ELEMENT_GAP));
        contentPanel.add(btnPanel);

        add(contentPanel);
        pack();
        setLocationRelativeTo(parentFrame); // 讓彈窗居中顯示在主視窗的正中央
    }

    //  公開接口：給 MenuBarController 撈取使用者最後修改的結果
    public String getEnteredName() { return nameField.getText(); } 
    public Color getSelectedColor() { return selectedColor; }
    public boolean isConfirmed() { return isConfirmed; }
}