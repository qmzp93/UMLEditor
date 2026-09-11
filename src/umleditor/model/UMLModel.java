package umleditor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import umleditor.model.shape.BaseObject;
import umleditor.model.shape.CompositeObject;
import umleditor.model.link.Link;

public class UMLModel {
    // --- 核心資料 (Data) ---
    private List<BaseObject> objects = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    // --- 觀察者清單 (Observers) ---
    private List<ModelChangeListener> listeners = new ArrayList<>();

    // 註冊觀察者 (讓 CanvasArea 可以註冊進來)
    public void addListener(ModelChangeListener listener) {
        listeners.add(listener);
    }

    // 通知所有觀察者資料已更新 (觸發畫面重繪)
    public void notifyListeners() {
        for (ModelChangeListener listener : listeners) {
            listener.onModelChanged();
        }
    }

    // --- 資料操作方法 (CRUD) ---

    public void addObject(BaseObject obj) {
        objects.add(obj);
        notifyListeners(); // 資料改變，發送通知
    }

    public void addLink(Link link) {
        links.add(link);
        notifyListeners();
    }

    public List<BaseObject> getAllObjects() {
        return Collections.unmodifiableList(objects); // Readonly
    }

    public List<Link> getAllLinks() {
        return Collections.unmodifiableList(links);
    }

    // 取消所有選取
    public void unselectAll() {
        for (BaseObject obj : objects) {
            obj.setSelected(false);
        }
        notifyListeners();
    }

    // 將特定物件移到清單末尾 (Z-order 置頂)
    public void moveObjectToFront(BaseObject obj) {
        if (objects.contains(obj)) {
            objects.remove(obj); // 先抽出來
            objects.add(obj); // 放回尾端 (變成最晚畫，也就是最上層)
            notifyListeners();
        }
    }

    // --- Use Case D: 群組邏輯 (屬於資料結構的變化，應放在 Model) ---
    public void groupSelected() {
        List<BaseObject> selected = new ArrayList<>();
        for (BaseObject obj : objects) {
            if (obj.isSelected())
                selected.add(obj);
        }

        if (selected.size() >= 2) {
            CompositeObject composite = new CompositeObject();
            objects.removeAll(selected);

            for (BaseObject obj : selected) {
                obj.setSelected(false);
                composite.addComponent(obj);
            }

            composite.setSelected(true);
            objects.add(composite);
            notifyListeners(); // 群組完成，通知重繪
        }
    }

    public void ungroupSelected() {
        // --- 先算算目前全畫布「總共」有幾個物件被選取了 ---
        int totalSelectedCount = 0;
        BaseObject singleSelectedGroup = null;

        for (BaseObject obj : objects) {
            if (obj.isSelected()) {
                totalSelectedCount++;
                if (obj.isGroup()) {
                    singleSelectedGroup = obj;
                }
            }
        }

        // 規格書嚴格要求：當大於 2 個物件被選取時，不執行任何動作。
        // 隱含條件：必須「剛好只有 1 個」物件被選取，且該物件「必須是群組」，才能進行 Ungroup。
        if (totalSelectedCount != 1 || singleSelectedGroup == null) {
            return; // 帥氣地直接結束，不給任何回應！
        }

        List<BaseObject> releasedMembers = singleSelectedGroup.getMembers();
        objects.remove(singleSelectedGroup);
        objects.addAll(releasedMembers);

        for (BaseObject member : releasedMembers) {
            member.setSelected(true);
        }

        notifyListeners();
    }

    public BaseObject findObjectAt(int x, int y) {
        // 從最上層(後面)開始往前找，確保點擊到重疊物件時，優先選取最上層的物件
        for (int i = objects.size() - 1; i >= 0; i--) {
            BaseObject obj = objects.get(i);
            if (obj.getBounds().contains(x, y)) {
                return obj;
            }
        }
        return null;
    }

    public List<Drawable> getAllDrawables() {
        List<Drawable> allDrawables = new ArrayList<>();
        allDrawables.addAll(objects); // 先畫物件 (底層)
        allDrawables.addAll(links); // 再畫線條 (上層)
        return Collections.unmodifiableList(allDrawables);
    }
}