# UML Editor

簡單的 UML 圖形編輯器，使用 Java Swing 與 MVC 架構開發。

## 功能

- 建立矩形與橢圓形物件
- 選取、移動及刪除 UML 物件
- 建立 Association、Generalization、Composition 關係
- 編輯物件標籤與顏色
- 將多個物件群組或解除群組
- 滑鼠移至物件時顯示連接埠

## 基本操作

1. 啟動程式後，預設會進入 Select 模式。
2. 選擇 Rect 或 Oval 工具，拖曳滑鼠即可建立物件。
3. 使用 Select 工具選取或移動物件。
4. 選擇 Association、Generalization 或 Composition，依序連接兩個物件。
5. 使用上方選單進行群組、解除群組或修改標籤。

## 專案結構

```text
src/umleditor/
├── Main.java                 # 程式進入點
├── controller/               # 使用者操作與模式控制
├── model/                    # UML 資料與商業邏輯
└── view/                     # Swing 使用者介面
```
