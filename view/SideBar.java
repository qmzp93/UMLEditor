package umleditor.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

// 負責工具列的排版與按鈕變色視覺效果
public class SideBar extends JPanel {
    // 記錄目前畫面上「哪一個按鈕被選中並變黑」了
    private JButton activeToolButton = null;

    public SideBar() {
        // 使用網格排版：無限列 (0) 代表自動往下堆疊，1 欄，按鈕間距 5 像素
        setLayout(new GridLayout(0, 1, 0, 5));
        setBackground(Color.LIGHT_GRAY);
    }

    // 提供給外部 (SideBarController) 註冊「點擊型按鈕 (Select, Links)」的方法
    public JButton addClickToolButton(String buttonName, ActionListener action) {
        JButton button = createBaseButton(buttonName);
        
        button.addActionListener(actionEvent -> {
            setActiveVisual(button);           // 1. 點擊時，自己先把按鈕變黑
            action.actionPerformed(actionEvent); // 2. 執行 Controller 傳進來的商業邏輯
        });
        
        add(button);
        return button;
    }

    // 提供給外部 (SideBarController) 註冊「拖曳型按鈕 (Rect, Oval)」的方法
    public JButton addDragToolButton(String buttonName, MouseListener mouseAction) {
        JButton button = createBaseButton(buttonName);
        button.addMouseListener(mouseAction); // 直接掛上外部傳進來的 Controller 監聽器
        add(button);
        return button;
    }

    // 給 Controller 呼叫：主動變更某個按鈕為「被選中 (黑底白圖)」的視覺狀態
    public void setActiveVisual(JButton targetButton) {
        // 先把上一個變黑的按鈕還原成標準的白底
        if (activeToolButton != null) {
            activeToolButton.setBackground(Color.WHITE);
        }
        
        activeToolButton = targetButton;
        
        // 把當前按鈕設定成黑底 (ToolIcon 內部會自動偵測黑底並把圖案塗白)
        if (targetButton != null) {
            targetButton.setBackground(Color.BLACK);
        }
    }

    // 建立一個符合系統標準化外觀的按鈕
    private JButton createBaseButton(String buttonName) {
        JButton button = new JButton();
        button.setIcon(new ToolIcon(buttonName));   // 利用 ToolIcon 繪製 UML 符號
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);            // 移除點擊時的藍色焦點外框
        button.setContentAreaFilled(false);
        button.setOpaque(true);            // 允許背景顏色正常顯示
        return button;
    }
}