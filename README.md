# Hệ Thống Tra Cứu & Theo Dõi Vận Đơn (Shipment Tracking System)

Dự án được xây dựng dựa trên tài liệu khảo sát và thiết kế nghiệp vụ của Hệ thống Tra cứu & Theo dõi Vận đơn (mô phỏng mô hình thực tế J&T Express).

---

## 🛠 Tech Stack

- **Ngôn ngữ**: Java 17 / Java 21
- **Framework**: Spring Boot 3.2.4
- **Build Tool**: Maven
- **Cơ sở dữ liệu**:
  - **Dev/Testing**: H2 Database (In-Memory, sẵn sàng chạy ngay không cần cài đặt CSDL)
  - **Production/Staging**: PostgreSQL
- **Tài liệu API**: Springdoc OpenAPI / Swagger UI
- **Thư viện phụ trợ**: Lombok, Spring Data JPA, Spring Validation

---

## 📁 Cấu trúc Thư mục Dự án

```text
TrackingSystemIntern/
├── src/
│   ├── main/
│   │   ├── java/com/tracking/system/
│   │   │   ├── config/             # Cấu hình OpenAPI, DataInitializer (Seed 12 mốc trạng thái)
│   │   │   ├── common/             # ApiResponse wrapper, GlobalExceptionHandler, Exceptions
│   │   │   ├── controller/         # REST Controllers (Shipment, Tracking, StatusCode)
│   │   │   ├── dto/                # Request & Response Data Transfer Objects
│   │   │   ├── entity/             # JPA Entities (User, Shipment, ShipmentStatusHistory, StatusCode)
│   │   │   ├── enums/              # UserRole, UserStatus, UpdateSource, ShipmentSource, CodPayer
│   │   │   ├── repository/         # Spring Data JPA Repositories
│   │   │   ├── service/            # Interfaces nghiệp vụ (ShipmentService, TrackingService, ...)
│   │   │   │   └── impl/           # Implementations của Service
│   │   │   └── TrackingSystemApplication.java
│   │   └── resources/
│   │       └── application.yml     # Cấu hình môi trường Dev (H2) & PG (PostgreSQL)
├── pom.xml                         # File cấu hình phụ thuộc Maven
└── README.md                       # Hướng dẫn dự án
```

---

## 🚀 Hướng dẫn Chạy Dự án trên IntelliJ IDEA

1. **Mở Dự án**:
   - Mở **IntelliJ IDEA** -> chọn `File` -> `Open...` -> chọn thư mục `TrackingSystemIntern`.
   - IntelliJ sẽ tự động phát hiện `pom.xml` và tải các dependency Maven.

2. **Cấu hình JDK**:
   - Vào `File` -> `Project Structure...` -> `Project`.
   - Chọn **SDK**: Java 17 hoặc Java 21 (ví dụ `ms-21` hoặc JDK 17 đã cài trên máy).

3. **Chạy Ứng dụng**:
   - Mở file `src/main/java/com/tracking/system/TrackingSystemApplication.java`.
   - Nhấn chuột phải hoặc bấm nút **Run** (biểu tượng ▶️ tam giác màu xanh).

---

## 🌐 Các Đường Dẫn Truy Cập Khi Ứng Dụng Chạy (Port 8081)

- **Swagger UI (Tài liệu REST API tương tác)**:
  👉 `http://localhost:8081/swagger-ui.html`
- **H2 Database Console**:
  👉 `http://localhost:8081/h2-console`
  - **JDBC URL**: `jdbc:h2:mem:trackingdb`
  - **User Name**: `sa`
  - **Password**: *(để trống)*

---

## 📌 Các Mốc Trạng Thái Vận Đơn Lõi (Status Codes Master Data)

Hệ thống được khởi tạo sẵn 12 mốc trạng thái chuẩn theo quy trình vận chuyển:

| Status Code | Tên Tiếng Việt | Tên Tiếng Anh | Final Status |
| :--- | :--- | :--- | :---: |
| `CREATED` | Đã tạo đơn | Order created | ❌ |
| `PICKUP_PENDING` | Chờ lấy hàng | Pending pickup | ❌ |
| `PICKED_UP` | Đã lấy hàng | Picked up | ❌ |
| `AT_SORT_HUB` | Đã nhập kho phân loại | At sorting hub | ❌ |
| `IN_TRANSIT` | Đang trung chuyển | In transit | ❌ |
| `ARRIVED_DEST_HUB` | Đã đến bưu cục khu vực | Arrived at destination | ❌ |
| `OUT_FOR_DELIVERY` | Đang giao hàng | Out for delivery | ❌ |
| `DELIVERED` | Giao hàng thành công | Delivered | ✅ |
| `DELIVERY_FAILED` | Giao hàng thất bại | Delivery failed | ❌ |
| `RETURNING` | Đang hoàn hàng | Returning to sender | ❌ |
| `RETURNED` | Đã hoàn hàng | Returned to sender | ✅ |
| `CANCELLED` | Đã hủy đơn | Cancelled | ✅ |

---

## 🔑 Nổi Bật Về Kỹ Thuật & Nghiệp Vụ

1. **Lịch sử trạng thái Append-Only (`shipment_status_history`)**:
   - Mọi lần cập nhật trạng thái chỉ thực hiện `INSERT`, bảo toàn lịch sử theo mốc thời gian để phục vụ đối soát, khiếu nại.
2. **Sinh mã Vận đơn duy nhất tự động (`tracking_code`)**:
   - Định dạng `VN + YYMMDDHHMMSS + Random4Digits`, đảm bảo tính duy nhất và không bị trùng lặp.
3. **Bảo mật Tra cứu Công khai (Mô phỏng J&T Express)**:
   - Tra cứu công khai chỉ xem được tỉnh/thành phố và trạng thái.
   - Nếu cung cấp đúng **4 số cuối SĐT** người gửi hoặc người nhận, hệ thống mới giải mã và hiển thị chi tiết tên, SĐT đầy đủ, địa chỉ nhà.
