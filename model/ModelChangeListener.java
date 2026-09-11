package umleditor.model;

/**
 * 觀察者模式 (Observer Pattern) 的傾聽者介面。
 * 誰想要知道 Model 資料改變了 (例如 Canvas 想知道什麼時候該重繪)，
 * 誰就去實作這個介面，並註冊到 UMLModel 裡面。
 */
public interface ModelChangeListener {
    // 當 Model 內的資料發生改變時，會呼叫此方法
    void onModelChanged();
}