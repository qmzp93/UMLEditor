import javax.swing.*;
import java.awt.*;

public class LabelDialog extends JDialog {
    private JTextField nameField;
    private Color selectedColor;
    private boolean isConfirmed = false;

    public LabelDialog(JFrame parent, String currentName, Color currentColor) {
        super(parent, "Customize Label Style", true); // [cite: 129]
        setLayout(new GridLayout(3, 2, 10, 10));
        
        // 1. 標籤名稱輸入 [cite: 130]
        add(new JLabel("Name:"));
        nameField = new JTextField(currentName);
        add(nameField);

        // 2. 顏色選取按鈕 [cite: 131]
        add(new JLabel("Color:"));
        JButton colorBtn = new JButton("Select Color");
        selectedColor = currentColor;
        colorBtn.setBackground(selectedColor);
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Choose Color", selectedColor);
            if (c != null) {
                selectedColor = c;
                colorBtn.setBackground(c);
            }
        });
        add(colorBtn);

        // 3. 確認與取消按鈕 [cite: 141, 142]
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> { isConfirmed = true; setVisible(false); });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> { isConfirmed = false; setVisible(false); });

        add(okBtn);
        add(cancelBtn);

        pack();
        setLocationRelativeTo(parent);
    }

    public String getEnteredName() { return nameField.getText(); }
    public Color getSelectedColor() { return selectedColor; }
    public boolean isConfirmed() { return isConfirmed; }
}