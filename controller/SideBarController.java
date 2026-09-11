package umleditor.controller;

import javax.swing.JButton;
import umleditor.model.ModeType;
import umleditor.model.ObjectType;
import umleditor.model.UMLModel;
import umleditor.view.Canvas;
import umleditor.view.SideBar;

// 專門管理左側工具列上所有按鈕的點擊、切換、以及拖曳建立圖形的滑鼠事件。
public class SideBarController {
    
    public SideBarController(UMLModel model, SideBar sideBar, Canvas canvas, CanvasController canvasController) {
        // 因為 Java 的 Lambda 只能讀取外面「final (不可變)」的變數，用一個長度為 1 的 final 陣列當作外殼包裝它。
        final JButton[] currentActiveModeButton = { null };

        // 註冊「點擊切換模式」工具 (Select, Association, etc.)
        ModeType[] toolModes = { ModeType.SELECT, ModeType.ASSOCIATION, ModeType.GENERALIZATION, ModeType.COMPOSITION };
        JButton selectBtn = null; // 用來記錄 Select 按鈕，以便程式啟動時做預設點擊

        for (ModeType type : toolModes) {
            // 呼叫 View (SideBar) 產生按鈕，並傳入點擊事件的 Lambda 處理邏輯
            JButton btn = sideBar.addClickToolButton(type.getDisplayName(), mouseEvent -> {
                // 記錄當前被點擊的模式按鈕 (發黑的按鈕)
                currentActiveModeButton[0] = (JButton) mouseEvent.getSource();
                
                // 建立對應的 Mode 
                Mode newMode = ModeFactory.createMode(type, canvas, model);
                
                // 變更畫布的滑鼠總路由大腦，讓滑鼠接下來的操作行為切換成新模式
                canvasController.setMode(newMode);
                
                // 根據規格書要求：除了 Select 模式之外，切換到其他模式時要清空畫面的選取狀態
                if (type != ModeType.SELECT) {
                    model.unselectAll();
                }
            });
            
            // 抓出 Select 按鈕的實體
            if (type == ModeType.SELECT) {
                selectBtn = btn;
            }
        }

        //  註冊「拖曳建立圖形」工具 (Rect, Oval)
        ObjectType[] objectTools = { ObjectType.RECT, ObjectType.OVAL };

        for (ObjectType type : objectTools) {
            // 呼叫 View 產生圖形按鈕
            JButton btn = sideBar.addDragToolButton(type.getDisplayName(), null);
            
            // 實例化負責處理拖曳放開後「建立圖形」的控制器
            // 這裡使用「匿名內部類別」來覆寫 mouseReleased 方法，加上更精緻的 UI 連動體驗
            CreateObjectController dragController = new CreateObjectController(type, btn, sideBar, canvas, model) {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
                    // 1. 先執行原本父類別的邏輯：把圖形建立在畫布上
                    super.mouseReleased(mouseEvent);
                    
                    // 2. 【貼心 UI 特效】
                    // 圖形建立完放開滑鼠後，自動模擬點擊剛才被記錄的持久模式按鈕 (例如 Select)
                    // 這樣系統的滑鼠焦點就不會卡在 Rect/Oval 上，而是直覺地切換回原來的操作模式
                    if (currentActiveModeButton[0] != null) {
                        currentActiveModeButton[0].doClick();
                    }
                }
            };
            
            // 將這個拖曳傾聽器掛載到按鈕身上
            btn.addMouseListener(dragController);
        }

        // 初始化狀態(選取模式)
        if (selectBtn != null) {
            selectBtn.doClick();
        }
    }
}