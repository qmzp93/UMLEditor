package umleditor.model;

// 新增檔案：ModelChangeListener.java
public interface ModelChangeListener {
    /** 當 Model 內的資料發生改變時，會呼叫此方法 */
    void onModelChanged();
}