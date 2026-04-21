# VinRECIPE — Kế hoạch Triển khai Giao diện (UI Implementation Plan)

> **Mục đích**: Kế hoạch kỹ thuật chi tiết để team (đặc biệt là bạn Kiet phụ trách Frontend) bắt tay vào code giao diện JavaFX một cách có hệ thống, không bị rối.

## 1. Yêu cầu Công nghệ & Thư viện (Tech Stack)

Để UI đẹp mà không quá phức tạp cho project môn học:
- **Core**: JavaFX (chuẩn theo yêu cầu môn).
- **Font/Icon**: Sử dụng thư viện `fontawesomefx` (để dùng icon cho Menu, nút bấm).
- **Styling**: `style.css` (Code chay bằng JavaFX CSS để dễ custom, không phụ thuộc nặng vào thư viện thứ 3).

**Cập nhật `pom.xml` (Maven) cho UI:**
```xml
<dependency>
    <groupId>de.jensd</groupId>
    <artifactId>fontawesomefx-fontawesome</artifactId>
    <version>4.7.0-9.1.2</version> 
</dependency>
```

## 2. Kiến trúc File Giao Diện (UI Folder Structure)

Tách biệt các màn hình thành từng file `.fxml` riêng để dễ quản lý và tránh bị conflict code.

```text
src/main/resources/
├── css/
│   ├── style.css             (File CSS chung: màu sắc, nút, font...)
│   └── components.css        (CSS cho Card món ăn, Sidebar...)
│
├── fxml/
│   ├── layout/
│   │   ├── MainLayout.fxml   (Chứa Sidebar và khung sườn chính)
│   │
│   ├── components/
│   │   ├── RecipeCard.fxml   (Component tái sử dụng cho từng món ăn)
│   │
│   ├── views/
│   │   ├── LoginView.fxml
│   │   ├── DashboardView.fxml
│   │   ├── SearchView.fxml
│   │   ├── RecipeDetailView.fxml
│   │   ├── RecipeFormView.fxml
│   │   └── ShoppingListView.fxml
│
└── images/
    ├── logo.png
    └── placeholder_food.png
```

## 3. Lộ trình triển khai (Implementation Phases)

### Phase 1: Móng giao diện (Base Foundation) & Login
- [ ] **Tạo `style.css` gốc:** Định nghĩa các biến màu sắc.
  ```css
  * {
      -fx-primary-color: #2ECC71;      /* Xanh lá */
      -fx-background-color: #F8F9FA;   /* Xám trắng */
      -fx-text-color: #1F2937;
  }
  .button-primary {
      -fx-background-color: -fx-primary-color;
      -fx-text-fill: white;
      -fx-background-radius: 5px;
  }
  ```
- [ ] Thiết kế `LoginView.fxml` (Đơn giản: 2 Textfield, 1 Button ở giữa màn hình) và `LoginController`.

### Phase 2: Khung sườn chính (Main Layout & Sidebar)
*(Kỹ thuật: Sử dụng `BorderPane` làm khung chính)*
- [ ] Tạo `MainLayout.fxml` với:
  - **Left**: `.vbox-sidebar` (Chứa Logo + Các nút Menu `Dashboard`, `Search`, `Shopping List`, `Add`).
  - **Center**: `.stackpane-content` (Vùng rỗng để load/swap các màn hình con vào).
- [ ] Code logic để nhấn nút Sidebar thì vùng Center thay đổi file `.fxml` tương ứng (Ví dụ: Nhấn "Dashboard" thì nhét `DashboardView.fxml` vào tâm).

### Phase 3: Phát triển Core View (Các màn hình chính)
- [ ] **DashboardView:** Dạng lưới (`GridPane` hoặc `TilePane`), hiển thị các món ăn mới nhất.
- [ ] **RecipeCard Component:** Thiết kế riêng cục `RecipeCard.fxml` (Có hình vuông, tên, sao đánh giá). Dashboard sẽ load nhiều file Card này.
- [ ] **SearchView:** Layout chia dọc.
  - Box trên: `HBox` chứa `TextField` (Tìm tên) và Component tag nguyên liệu.
  - Box dưới: Bảng hiển thị kết quả.

### Phase 4: Màn hình Chi tiết và Shopping List
- [ ] **RecipeDetailView:** Chia tỷ lệ 3/7. Cột trái liệt kê nguyên liệu (`ListView` + Checkbox), cột phải text chi tiết cách làm.
- [ ] **ShoppingListView:** Giống giao diện To-Do. Dùng `ListView` tùy chỉnh cell. Mỗi dòng hiển thị: `[ ] 500g Gà`. Cần xử lý CSS `.strike-through` khi click vào.

## 4. Rủi ro & Lưu ý khi triển khai

> [!WARNING]
> Mẹo tránh lỗi trong JavaFX:
> 1. **fx:id và Controller**: Rất hay bị `NullPointerException` do đặt sai tên hoặc quên gán `fx:id` từ SceneBuilder qua code Java.
> 2. **Chuyển Scene (Switching Scenes)**: Chỉ nạp nội dung (node) vào cái khung `Center` của `MainLayout`, ĐỪNG tạo một cái Stage/Window mới hoàn toàn mỗi khi chuyển trang (sẽ rất giật và tốn RAM).
> 3. **Non-blocking UI**: Nếu Load ảnh từ Database hay tìm kiếm mất công, dùng `Task` hoặc `Thread` để không làm đơ giao diện.

## 5. User Review Required (Cần Chốt)
1. Team có dự định dùng **JavaFX SceneBuilder** để kéo thả không? (Rất khuyến khích vì làm XML tay cho JavaFX dễ sai sót).
2. Khi chuyển trang, bạn muốn dùng **Single Window** (1 phần mềm duy nhất đổi ruột) đúng như mình vạch ra chứ?
