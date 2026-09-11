package umleditor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import umleditor.model.links.Link;
import umleditor.model.objects.BaseObject;
import umleditor.model.objects.CompositeObject;

public class UMLModel {
    // Index 越大 (越靠近尾端) 的物件，代表深度值(depth)越小 (越上層)，會在繪圖時最後被畫出。
    private List<BaseObject> objects = new ArrayList<>();   
    private List<Link> links = new ArrayList<>();

    // --- 觀察者清單 (Observers) ---
    private List<ModelChangeListener> listeners = new ArrayList<>();

    // 註冊觀察者 (讓 Canvas 可以註冊進來)
    public void addListener(ModelChangeListener listener) {
        listeners.add(listener);
    }

    // 通知所有觀察者資料已更新 (觸發畫面重繪)
    public void notifyListeners() {
        for (ModelChangeListener listener : listeners) {
            listener.onModelChanged();
        }
    }

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
    public void moveObjectToFront(BaseObject targetObj) {
        if (objects.contains(targetObj)) {
            objects.remove(targetObj); // 先抽出來
            objects.add(targetObj); // 放回尾端 (變成最晚畫，也就是最上層)
            notifyListeners();
        }
    }

    public void groupSelected() {
        List<BaseObject> selectedItems = new ArrayList<>();
        for (BaseObject obj : objects) {
            if (obj.isSelected()) {
                selectedItems.add(obj);
            }
        }

        // 2. 規格要求：大於等於 2 個物件才能群組
        if (selectedItems.size() >= 2) {
            CompositeObject compositeGroup = new CompositeObject();
            
            objects.removeAll(selectedItems);

            // 把這些物件塞進剛剛建立的「群組物件」肚子裡
            for (BaseObject obj : selectedItems) {
                obj.setSelected(false); // 取消個別選取狀態
                compositeGroup.addComponent(obj);
            }

            compositeGroup.setSelected(true); 
            objects.add(compositeGroup);      
            
            notifyListeners(); 
        }
    }

    public void ungroupSelected() {
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

        // 規格書嚴格要求：必須「剛好只有 1 個」物件被選取，且該物件「必須是群組」，才能進行 Ungroup。
        if (totalSelectedCount != 1 || singleSelectedGroup == null) {
            return;
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
        allDrawables.addAll(objects);   // 先畫物件 (底層)
        allDrawables.addAll(links);     // 再畫線條 (上層)
        return Collections.unmodifiableList(allDrawables);
    }
}