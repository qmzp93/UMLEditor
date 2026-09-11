package umleditor.controller;

import java.awt.event.MouseAdapter;

import umleditor.model.UMLModel;
import umleditor.view.Canvas;

// 狀態模式 (State Pattern) 的抽象基底類別。
public abstract class Mode extends MouseAdapter {
    
    // 將 view 和 model 設為 protected，讓所有的子類別 (SelectMode 等) 都可以直接使用
    protected Canvas view;
    protected UMLModel model;

    // 父類別建構子：強迫所有繼承的 Mode 都必須傳入 view 和 model
    public Mode(Canvas view, UMLModel model) {
        this.view = view;
        this.model = model;
    }
}