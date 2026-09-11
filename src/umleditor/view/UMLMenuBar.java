package umleditor.view;

import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

// 100% 純粹的 View：移除了對 Model 的依賴，只負責選單外觀與事件暴露
public class UMLMenuBar extends JMenuBar {
    private JMenuItem groupItem;
    private JMenuItem ungroupItem;
    private JMenuItem labelItem;

    public UMLMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        
        groupItem = new JMenuItem("Group");
        ungroupItem = new JMenuItem("Ungroup");
        labelItem = new JMenuItem("Label");

        editMenu.add(groupItem);
        editMenu.add(ungroupItem);
        editMenu.add(labelItem);
        
        add(fileMenu);
        add(editMenu);
    }

    // 提供接口供 Controller 註冊事件
    public void addGroupActionListener(ActionListener l) { groupItem.addActionListener(l); }
    public void addUngroupActionListener(ActionListener l) { ungroupItem.addActionListener(l); }
    public void addLabelActionListener(ActionListener l) { labelItem.addActionListener(l); }
}