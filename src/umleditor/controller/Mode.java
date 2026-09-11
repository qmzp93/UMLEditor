package umleditor.controller;

import java.awt.event.MouseAdapter;

import umleditor.model.UMLModel;
import umleditor.view.CanvasArea;

// 將 interface 改為 abstract class，並繼承 MouseAdapter
public abstract class Mode extends MouseAdapter {
    
    // 將 view 和 model 設為 protected，讓所有的子類別 (SelectMode 等) 都可以直接使用
    protected CanvasArea view;
    protected UMLModel model;

    // 父類別建構子：強迫所有繼承的 Mode 都必須傳入 view 和 model
    public Mode(CanvasArea view, UMLModel model) {
        this.view = view;
        this.model = model;
    }
}