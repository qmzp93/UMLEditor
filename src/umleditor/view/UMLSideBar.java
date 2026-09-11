package umleditor.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

// 極致解耦：UMLSideBar 現在是一個「純 View」，完全不知道 Model 與 Controller 的存在
public class UMLSideBar extends JPanel {
    private JButton activeToolButton = null;

    public UMLSideBar() {
        setLayout(new GridLayout(0, 1, 0, 5));
        setBackground(Color.LIGHT_GRAY);
    }

    // 提供給外部註冊「點擊型按鈕 (Select, Links)」的方法
    public JButton addClickToolButton(String name, ActionListener action) {
        JButton btn = createBaseButton(name);
        
        btn.addActionListener(e -> {
            setActiveVisual(btn); // 自己處理 UI 視覺變色
            action.actionPerformed(e); // 執行外部傳進來的商業邏輯 (Controller)
        });
        
        add(btn);
        return btn;
    }

    // 提供給外部註冊「拖曳型按鈕 (Rect, Oval)」的方法
    public JButton addDragToolButton(String name, MouseListener mouseAction) {
        JButton btn = createBaseButton(name);
        btn.addMouseListener(mouseAction); // 直接掛上外部傳進來的 Controller
        add(btn);
        return btn;
    }

    // 開放給 Controller 呼叫：變更按鈕為「選中(黑)」的視覺狀態
    public void setActiveVisual(JButton btn) {
        if (activeToolButton != null) {
            activeToolButton.setBackground(Color.WHITE);
        }
        activeToolButton = btn;
        if (btn != null) {
            btn.setBackground(Color.BLACK);
        }
    }

    // 建立標準化外觀的按鈕
    private JButton createBaseButton(String name) {
        JButton btn = new JButton();
        btn.setIcon(new UMLIcon(name));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        return btn;
    }
}