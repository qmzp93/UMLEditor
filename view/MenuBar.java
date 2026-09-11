package umleditor.view;

import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

// 負責暴露出事件接口供 Controller 註冊
public class MenuBar extends JMenuBar {
    private JMenuItem groupItem;
    private JMenuItem ungroupItem;
    private JMenuItem labelItem;

    public MenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        
        // 建立選單裡面的細項按鈕
        groupItem = new JMenuItem("Group");
        ungroupItem = new JMenuItem("Ungroup");
        labelItem = new JMenuItem("Label");

        // 將按鈕塞進 Edit 選單中
        editMenu.add(groupItem);
        editMenu.add(ungroupItem);
        editMenu.add(labelItem);
        
        // 將兩大選單掛上選單列
        add(fileMenu);
        add(editMenu);
    }

    //  事件接口 (供 MenuBarController 綁定)
    public void addGroupActionListener(ActionListener actionListener) { groupItem.addActionListener(actionListener); }
    public void addUngroupActionListener(ActionListener actionListener) { ungroupItem.addActionListener(actionListener); }
    public void addLabelActionListener(ActionListener actionListener) { labelItem.addActionListener(actionListener); }
}