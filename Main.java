package umleditor;

import javax.swing.SwingUtilities;

import umleditor.controller.MainController;
import umleditor.model.UMLModel;
import umleditor.view.MainFrame;

// 系統進入點 (Composition Root)：只負責把 MVC 三層建立出來並接線
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UMLModel model = new UMLModel();
            MainFrame view = new MainFrame(model);
            MainController app = new MainController(model, view);
            
            app.start();
        });
    }
}