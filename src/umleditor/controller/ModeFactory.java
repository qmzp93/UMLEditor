package umleditor.controller;

import umleditor.model.ModeType;
import umleditor.model.UMLModel;
import umleditor.model.link.AssociationStrategy;
import umleditor.model.link.CompositionStrategy;
import umleditor.model.link.GeneralizationStrategy;
import umleditor.view.CanvasArea;

// 工廠模式：利用 Enum 確保型別安全 (Type Safety)
public class ModeFactory {
    
    public static Mode createMode(ModeType type, CanvasArea view, UMLModel model) {
        switch (type) {
            case SELECT:
                return new SelectMode(view, model);
            case ASSOCIATION:
                return new LinkMode(view, model, new AssociationStrategy());
            case GENERALIZATION:
                return new LinkMode(view, model, new GeneralizationStrategy());
            case COMPOSITION:
                return new LinkMode(view, model, new CompositionStrategy());
            default:
                throw new IllegalArgumentException("未知的模式: " + type);
        }
    }
}