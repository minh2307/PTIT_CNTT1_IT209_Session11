# Bài 4: Khai báo GitHub Secrets và cấu hình Đăng nhập Container Registry

- **Môn học:** IT209 - Tích hợp hệ thống
- **Bài thực hành:** Session 11 - Bài 4
- **Repository:** [`minh2307/PTIT_CNTT1_IT209_Session11`](https://github.com/minh2307/PTIT_CNTT1_IT209_Session11)

---

## 1. Mục tiêu
- Tạo Personal Access Token (PAT) an toàn trên GitHub có quyền `write:packages`.
- Lưu trữ bảo mật Token vào Repository Secrets với tên biến `REGISTRY_TOKEN`.
- Cấu hình bước xác thực đăng nhập tự động vào GitHub Container Registry (GHCR - `ghcr.io`) trong quy trình CI với `docker/login-action@v3`.

---

## 2. Các bước thực hiện

### Bước 1: Tạo Personal Access Token (classic)
1. Truy cập: [GitHub Personal Access Tokens](https://github.com/settings/tokens/new).
2. Thiết lập:
   - **Note:** `IT209 GHCR Token` (hoặc tên tùy chọn).
   - **Expiration:** Chọn thời gian phù hợp (ví dụ: 30 days hoặc 90 days).
   - **Select scopes:** Tích chọn quyền **`write:packages`** (hệ thống sẽ tự động tick kèm `read:packages`).
3. Nhấn **Generate token** và sao chép mã Token hiển thị (ví dụ dạng `ghp_xxxxxxxxxxxx`).

---

### Bước 2: Khai báo Repository Secret trên GitHub
1. Vào repository: [`minh2307/PTIT_CNTT1_IT209_Session11`](https://github.com/minh2307/PTIT_CNTT1_IT209_Session11).
2. Vào **Settings** -> menu bên trái chọn **Secrets and variables** -> chọn **Actions**.
3. Nhấn nút **New repository secret**.
4. Điền thông tin:
   - **Name:** `REGISTRY_TOKEN`
   - **Secret:** Dán mã Personal Access Token vừa tạo ở Bước 1.
5. Nhấn **Add secret**.
6. Chụp ảnh màn hình danh sách Repository Secrets hiển thị biến `REGISTRY_TOKEN` và lưu vào thư mục này: `screenshot.png`.

---

### Bước 3: Cấu hình kịch bản CI (`.github/workflows/ci.yml`)

Khai báo bước đăng nhập với `docker/login-action@v3`:

```yaml
      - name: Đăng nhập vào GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.REGISTRY_TOKEN }}
```

Tệp kịch bản hoàn chỉnh được lưu tại:
- [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml)
- [`homework/session_11/ex4/ci.yml`](./ci.yml)

---

### Bước 4: Kiểm tra kết quả (Kiểm tra Log)

Sau khi push mã nguồn lên nhánh `main`, mở tab **Actions** trên GitHub:
1. Chọn luồng chạy CI mới nhất.
2. Vào job **Build & Test**.
3. Mở bước **Đăng nhập vào GitHub Container Registry**.
4. Log chi tiết xác nhận đăng nhập thành công:
   ```text
   Run docker/login-action@v3
     with:
       registry: ghcr.io
       username: minh2307
       password: ***
       logout: true
   Logging into ghcr.io...
   Login Succeeded!
   ```

---

## 3. Sản phẩm nộp bài
1. Tệp kịch bản workflow: [`ci.yml`](./ci.yml) (có chứa bước `docker/login-action@v3`).
2. Ảnh chụp màn hình: [`screenshot.png`](./screenshot.png) (hiển thị `REGISTRY_TOKEN` trong Repository Secrets).
