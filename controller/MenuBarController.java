package umleditor.controller;

import umleditor.model.UMLModel;
import umleditor.model.objects.BaseObject;
import umleditor.view.LabelDialog;
import umleditor.view.MainFrame;
import umleditor.view.MenuBar;

public class MenuBarController {

    public MenuBarController(UMLModel model, MenuBar menuBar, MainFrame mainFrame) {
        // 群組與解群組事件直接委派給 Model
        menuBar.addGroupActionListener(e -> model.groupSelected());
        menuBar.addUngroupActionListener(e -> model.ungroupSelected());

        // 自定義標籤對話框邏輯
        menuBar.addLabelActionListener(e -> {
            BaseObject target = null;
            int selectedCount = 0;

            for (BaseObject obj : model.getAllObjects()) {
                if (obj.isSelected()) {
                    target = obj;
                    selectedCount++;
                }
            }

            if (selectedCount == 1 && target.isNameEditable()) {
                LabelDialog dialog = new LabelDialog(mainFrame, target.getLabel(), target.getColor());
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    target.setLabel(dialog.getEnteredName());
                    target.setColor(dialog.getSelectedColor());
                    model.notifyListeners();
                }
            }
        });
    }
}