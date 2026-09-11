package umleditor.controller;

import javax.swing.JButton;

import umleditor.model.ModeType;
import umleditor.model.ShapeType;
import umleditor.model.UMLModel;
import umleditor.model.shape.BaseObject;
import umleditor.view.CanvasArea;
import umleditor.view.LabelDialog;
import umleditor.view.MainFrame;
import umleditor.view.UMLMenuBar;
import umleditor.view.UMLSideBar;

// 系統主控制器：這才是真正負責 MVC「路由與組裝」的大腦
public class AppController {
    private UMLModel model;
    private MainFrame view;

    public AppController(UMLModel model, MainFrame view) {
        this.model = model;
        this.view = view;
    }

    // 啟動系統，將 View 的按鈕與 Controller 的邏輯綁定
    public void start() {
        UMLSideBar sideBar = view.getSideBar();
        CanvasArea canvas = view.getCanvas();

        // --- 新增：將畫布專屬的滑鼠控制器掛載到 View 上 ---
        CanvasMouseController canvasController = new CanvasMouseController(model);
        canvas.addMouseListener(canvasController);
        canvas.addMouseMotionListener(canvasController);

        final JButton[] currentActiveModeButton = { null };
        // 1. 註冊「點擊切換模式」的邏輯
        ModeType[] toolModes = { ModeType.SELECT, ModeType.ASSOCIATION, ModeType.GENERALIZATION, ModeType.COMPOSITION };
        JButton selectBtn = null;

        for (ModeType type : toolModes) {
            JButton btn = sideBar.addClickToolButton(type.getDisplayName(), e -> {
                currentActiveModeButton[0] = (JButton) e.getSource();
                Mode newMode = ModeFactory.createMode(type, canvas, model); // 傳入 Enum
                canvasController.setMode(newMode);
                if (type != ModeType.SELECT)
                    model.unselectAll();
            });
            if (type.getDisplayName().equals("Select"))
                selectBtn = btn;
        }

        // 2. 註冊「拖曳建立圖形」的邏輯
        ShapeType[] shapeTools = { ShapeType.RECT, ShapeType.OVAL };

        for (ShapeType type : shapeTools) {
            // 呼叫 View 時，使用 getDisplayName() 取出 "Rect" / "Oval" 供按鈕讀取圖片
            JButton btn = sideBar.addDragToolButton(type.getDisplayName(), null);

            // 建立 Controller 時，直接將安全的 Enum (type) 傳進去
            CreateObjectController dragController = new CreateObjectController(type, btn, sideBar, canvas, model) {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    super.mouseReleased(e);
                    if (currentActiveModeButton[0] != null) {
                        currentActiveModeButton[0].doClick();
                    }
                }
            };
            btn.addMouseListener(dragController);
        }

        // 3. 預設進入 Select 模式，並顯示視窗
        if (selectBtn != null)
            selectBtn.doClick();
        view.setVisible(true);

        // --- 3. 註冊頂部選單的 Controller 邏輯 (完全從 View 層抽離) ---
        UMLMenuBar menuBar = view.getUMLMenuBar();

        // 3.1 群組事件
        menuBar.addGroupActionListener(e -> model.groupSelected());

        // 3.2 解除群組事件
        menuBar.addUngroupActionListener(e -> model.ungroupSelected());

        // 3.3 修改標籤事件 (原 MenuBar 內最肥大的核心商務邏輯)
        menuBar.addLabelActionListener(e -> {
            BaseObject target = null;
            int selectedCount = 0;

            // 從 Model 遍歷資料
            for (BaseObject obj : model.getAllObjects()) {
                if (obj.isSelected()) {
                    target = obj;
                    selectedCount++;
                }
            }

            // 多型判定與彈窗控制
            if (selectedCount == 1 && target.isNameEditable()) {
                // 彈出對話框 (傳入 view 作為 Parent Component)
                LabelDialog dialog = new LabelDialog(view, target.getLabel(), target.getColor());
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    // 資料層修改
                    target.setLabel(dialog.getEnteredName());
                    target.setColor(dialog.getSelectedColor());
                    // 資料變更，通知重繪
                    model.notifyListeners();
                }
            }
        });
    }
}