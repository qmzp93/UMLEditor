package umleditor;

import javax.swing.SwingUtilities;

import umleditor.controller.AppController;
import umleditor.model.UMLModel;
import umleditor.view.MainFrame;

// 系統進入點 (Composition Root)：只負責把 MVC 三層建立出來並接線
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 建立 Model (資料)
            UMLModel model = new UMLModel();
            
            // 2. 建立 View (畫面，這時畫面還是死的，沒有任何功能)
            MainFrame view = new MainFrame(model);
            
            // 3. 建立 Controller (大腦)，把資料和畫面交給大腦
            AppController app = new AppController(model, view);
            
            // 4. 大腦開始接線並啟動系統
            app.start();
        });
    }
}