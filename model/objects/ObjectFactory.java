package umleditor.model.objects;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

import umleditor.model.ObjectType;

// 物件工廠：負責把「按鈕的列舉型態」轉換成「真正的圖形物件」
public class ObjectFactory {
    private static final Map<ObjectType, BiFunction<Integer, Integer, BaseObject>> registry = new EnumMap<>(ObjectType.class);

    // 靜態區塊：程式一啟動時，就把已知支援的圖形註冊進去
    static {
        registry.put(ObjectType.RECT, (x, y) -> new RectObject(x, y));
        registry.put(ObjectType.OVAL, (x, y) -> new OvalObject(x, y));
    }

    public static void register(ObjectType type, BiFunction<Integer, Integer, BaseObject> creator) {
        registry.put(type, creator);
    }

    // 給 Controller 呼叫的主要方法
    public static BaseObject createObject(ObjectType type, int x, int y) {
        BiFunction<Integer, Integer, BaseObject> creator = registry.get(type);
        if (creator == null) throw new IllegalArgumentException("未知的圖形類型: " + type);
        return creator.apply(x, y);
    }
}