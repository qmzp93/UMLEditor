package umleditor.model;

// 給所有工具列按鈕的共通介面
public interface ToolType {
    String getDisplayName();     //取得按鈕的顯示名稱 (用來抓取對應的 Icon 圖片)
}