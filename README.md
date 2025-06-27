# 🏡 Homestay Booking Backend

Dự án xây dựng hệ thống backend cho nền tảng đặt phòng homestay trực tuyến, được phát triển bằng **Spring Boot** và sử dụng **PostgreSQL** làm hệ quản trị cơ sở dữ liệu. Hệ thống hỗ trợ phân quyền động (RBAC + permission), đặt phòng, quản lý homestay, lịch trống, tiện nghi, thanh toán VNPay, v.v.

### Yêu cầu
- Java 17
- PostgreSQL 16+ với PostGIS extension
- Spring boot 3.3.12
- Tài khoản VNPay sandbox (cho thanh toán)
  
## 📌 Tính năng chính

### 🔐 Xác thực & Phân quyền
- Xác thực bằng JWT (JSON Web Token)
- Hỗ trợ phân quyền động: Role – Permission
- Tự động kiểm tra quyền truy cập thông qua `Interceptor`

### 🏠 Quản lý Homestay
- CRUD homestay, ảnh, tiện nghi (amenities)
- Gắn tiện nghi và hình ảnh cho từng homestay
- Quản lý lịch trống và đơn giá theo ngày (`Availability`)

### 📍 Tìm kiếm theo vị trí
- Tìm homestay gần vị trí người dùng bằng bán kính cụ thể (metter)
- Dựa trên PostGIS với kiểu dữ liệu geometry(Point)
- Tại frontend, người dùng cung cấp vị trí dưới dạng kinh độ và vĩ độ theo hệ quy chiếu EPSG:4326 (WGS 84)
- Dữ liệu homestay được lưu với:
    latitude, longitude: dùng để hiển thị đơn giản
    geom: cột kiểu geometry(Point, 3857) — được chuyển từ 4326 sang 3857 khi lưu
- Tìm kiếm gần được thực hiện trong cơ sở dữ liệu bằng PostGIS với hệ tọa độ EPSG:3857 để tính toán khoảng cách chính xác theo đơn vị mét

### 📅 Đặt phòng
- Người dùng có thể đặt homestay theo khoảng ngày
- Tính tổng tiền, số lượng khách, ghi chú
- Cập nhật trạng thái đơn (DRAFT, BOOKED, COMPLETED, CANCELLED, PAYMENT_PROCESSING, PAYMENT_FAILED)

### 💬 Đánh giá Homestay
- Người dùng chỉ có thể đánh giá homestay sau khi đặt phòng thành công và đánh giá 1 lần duy nhất
- Mỗi đánh giá gồm điểm số (rating) và nội dung nhận xét

### 💳 Thanh toán VNPay
- Tích hợp VNPay theo chuẩn IPN
- Kiểm tra giao dịch, lưu thông tin `payment_transaction`

### 📂 Quản lý dữ liệu
- Quản lý người dùng, vai trò (role), quyền (permission)
- Tạo, phân quyền, gán vai trò động cho user
- Truy vấn lịch sử đặt phòng theo người dùng

### 📘 Tài liệu API (Swagger)
- Hệ thống sử dụng Swagger UI để cung cấp giao diện tài liệu API REST.
- Tự động hiển thị toàn bộ endpoint, hỗ trợ nhập liệu và test trực tiếp trên giao diện web.
- Hỗ trợ xác thực bằng Bearer Token để thử các API bảo mật.
- Truy cập: http://localhost:8080/swagger-ui/index.html

## 🧱 Kiến trúc hệ thống

- Kiến trúc phân tầng: `Controller → Service → Repository → Database`
- Áp dụng `Interceptor` để kiểm tra quyền người dùng động
- Dữ liệu phản hồi tuân theo chuẩn **RFC 7807 (Problem Detail)** và **Custom Unified Response Format**

## ⚙️ Công nghệ sử dụng

| Loại | Công nghệ |
|------|-----------|
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA |
| Auth | JWT Authentication |
| Database | PostgreSQL + PostGIS |
| Connection Pooling | HikariCP |
| Dev Tools | Postman, DBeaver, Git, GitHub, VSCode |
| Payment | VNPay IPN |
| Build Tool | Gradle - Groovy |
| Tài liệu API | Swagger UI, Springdoc OpenAPI |

## 📁 Cấu trúc thư mục
```
src/main/java/vn/quangkhongbiet/homestay_booking
├── config
├── domain
│   ├── audit
│   ├── booking
│   ├── homestay
│   ├── payment
│   └── user
├── repository
├── service
│   ├── booking
│   ├── homestay
│   ├── payment
│   └── user
├── utils
├── web
│   ├── dto
│   └── rest
|      ├── api
|      └── errors
└── HomestayBookingApplication.java
