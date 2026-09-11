package umleditor.controller;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

import umleditor.model.ModeType;
import umleditor.model.UMLModel;
import umleditor.model.links.AssociationStrategy;
import umleditor.model.links.CompositionStrategy;
import umleditor.model.links.GeneralizationStrategy;
import umleditor.view.Canvas;

// 註冊式工廠模式 (Factory Pattern)
public class ModeFactory {
    private static final Map<ModeType, BiFunction<Canvas, UMLModel, Mode>> registry = new EnumMap<>(ModeType.class);

    // 靜態區塊：系統啟動時，把所有支援的滑鼠模式註冊進來
    static {
        registry.put(ModeType.SELECT, (view, model) -> new SelectMode(view, model));
        
        // 畫線模式都是共用 LinkMode，只是裡面塞進去的「箭頭策略 (Strategy)」不一樣
        registry.put(ModeType.ASSOCIATION, (view, model) -> new LinkMode(view, model, new AssociationStrategy()));
        registry.put(ModeType.GENERALIZATION, (view, model) -> new LinkMode(view, model, new GeneralizationStrategy()));
        registry.put(ModeType.COMPOSITION, (view, model) -> new LinkMode(view, model, new CompositionStrategy()));
    }

    // 預留給未來擴充使用
    public static void register(ModeType type, BiFunction<Canvas, UMLModel, Mode> creator) {
        registry.put(type, creator);
    }

    // MainController 呼叫的建立方法：給我模式種類，我生一個全新的 Mode 物件給你
    public static Mode createMode(ModeType type, Canvas view, UMLModel model) {
        BiFunction<Canvas, UMLModel, Mode> creator = registry.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("未知的模式: " + type);
        }
        return creator.apply(view, model);
    }
}