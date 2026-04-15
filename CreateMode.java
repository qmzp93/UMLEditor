import java.awt.event.MouseEvent;

public class CreateMode implements Mode {
    private String type; // "Rect" 或 "Oval"
    private CanvasArea canvas;

    public CreateMode(String type, CanvasArea canvas) {
        this.type = type;
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 規格書提到拖曳至編輯區任一範圍放開 [cite: 39]
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        BaseObject obj;
        if (type.equals("Rect")) {
            obj = new RectObject(e.getX(), e.getY());
        } else {
            obj = new OvalObject(e.getX(), e.getY());
        }
        canvas.addObject(obj); // 在座標處建立物件 
        
        // 規格書要求建立後回到原選取模式 [cite: 41]
        // 這裡可以透過回傳事件給 UMLEditor 來處理切換模式
    }

    @Override public void mouseDragged(MouseEvent e) {}
}