# Báo cáo Bài 3: Tối ưu tốc độ CI bằng Gradle Dependency Caching

- **Môn học:** IT209 - Tích hợp hệ thống
- **Bài thực hành:** Session 11 - Bài 3
- **Repository:** [`minh2307/PTIT_CNTT1_IT209_Session11`](https://github.com/minh2307/PTIT_CNTT1_IT209_Session11)

---

## 1. Mục tiêu bài thực hành
Kích hoạt tính năng **Dependency Caching** cho Gradle trong quy trình CI (Continuous Integration) với GitHub Actions thông qua action `actions/setup-java@v5`.
- Tránh việc luồng CI phải tải lại toàn bộ các thư viện (dependencies) và wrapper từ Internet trong mỗi lần chạy.
- Rút ngắn thời gian thực thi của bước `./gradlew test` và toàn bộ quy trình CI.

---

## 2. Cấu hình kịch bản CI (`.github/workflows/ci.yml`)

Trong bước thiết lập môi trường Java JDK 17, tham số `cache: 'gradle'` được bổ sung:

```yaml
name: CI Pipeline

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build-and-test:
    name: Build & Test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout mã nguồn
        uses: actions/checkout@v4

      - name: Thiết lập môi trường Java JDK 17
        uses: actions/setup-java@v5
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'gradle'

      - name: Cấp quyền thực thi cho gradlew
        run: chmod +x gradlew

      - name: Chạy kiểm thử với Gradle
        run: ./gradlew test
```

---

## 3. Bảng so sánh kết quả thực thi giữa 2 lần Push

Hai lần push liên tiếp được thực hiện lên nhánh `main` của repository GitHub:
- **Lần push 1 (Commit `0284dde`):** Lần chạy khởi tạo (Cold Run) — chưa có cache trên hệ thống GitHub Actions.
- **Lần push 2 (Commit `4a9eb0c`):** Lần chạy tiếp theo (Warm Run) — khôi phục thành công cache từ lần 1.

| Bước (Step) | Lần push 1 (Chưa có cache) | Lần push 2 (Đã có cache) | Độ chênh lệch (Cải thiện) |
| :--- | :---: | :---: | :---: |
| **Set up job** | 1.0s | 2.0s | +1.0s |
| **Checkout mã nguồn** | 1.0s | 0.0s | -1.0s |
| **Thiết lập môi trường Java JDK 17** | 1.0s *(Cache miss)* | 3.0s *(Restoring cache)* | +2.0s |
| **Cấp quyền thực thi cho gradlew** | 0.0s | 0.0s | 0.0s |
| **Chạy kiểm thử với Gradle (`./gradlew test`)** | **30.0s** *(Phải tải deps)* | **10.0s** *(Đã có deps trong cache)* | **-20.0s (Giảm 66.7%)** |
| **Post Thiết lập môi trường Java JDK 17** | **5.0s** *(Lưu cache mới)* | **0.0s** *(Cache đã tồn tại)* | **-5.0s** |
| **Post Checkout & Complete job** | 0.0s | 1.0s | +1.0s |
| **TỔNG THỜI GIAN TOÀN BỘ JOB** | **41.0s** | **17.0s** | **-24.0s (Giảm 58.5%)** |

> **Liên kết kiểm chứng trên GitHub Actions:**
> - Lần push 1: [Run #38145098017 (Job #114486675110)](https://github.com/minh2307/PTIT_CNTT1_IT209_Session11/actions/runs/38145098017/job/114486675110) - Thời gian: **41s**
> - Lần push 2: [Run #38145174767 (Job #114486900130)](https://github.com/minh2307/PTIT_CNTT1_IT209_Session11/actions/runs/38145174767/job/114486900130) - Thời gian: **17s**

---

## 4. Kiểm tra Log khôi phục Cache (Lần push 2)

Tại bước **Thiết lập môi trường Java JDK 17** trong lần chạy thứ 2, log ghi nhận hành vi khôi phục cache thành công:

```text
Run actions/setup-java@v5
  with:
    java-version: 17
    distribution: temurin
    cache: gradle
...
Resolved gradle-Linux-xxxx to: Linux-gradle-xxxx
Searching for a cache with key: gradle-Linux-xxxx
Cache restored successfully from key: gradle-Linux-xxxx
Restored from cache:
  - ~/.gradle/caches
  - ~/.gradle/wrapper
```

Đồng thời, tại bước **Chạy kiểm thử với Gradle (`./gradlew test`)**:
- Gradle không cần thực hiện tải lại hàng chục MB thư viện (Spring Boot, Tomcat, Jackson, JUnit,...) từ Maven Central.
- Tốc độ thực thi giảm từ **30.0s** xuống còn **10.0s**, chạy thẳng vào giai đoạn biên dịch và chạy test `DemoApplicationTests`.

---

## 5. Nguyên lý hoạt động của Gradle Caching trong `actions/setup-java`

1. **Sinh Cache Key:**
   Action tự động quét và băm (hash) toàn bộ các tệp cấu hình Gradle trong dự án theo mẫu:
   ```text
   gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
   ```
2. **Cơ chế Lưu trữ (Save Cache):**
   - Khi luồng CI chạy lần đầu tiên, không có cache key nào khớp.
   - Khi job chạy xong, bước hậu xử lý `Post actions/setup-java@v5` tự động nén thư mục `~/.gradle/caches` và `~/.gradle/wrapper` rồi tải lên bộ lưu trữ cache của GitHub Actions.
3. **Cơ chế Khôi phục (Restore Cache):**
   - Ở các lần push tiếp theo, nếu các tệp cấu hình Gradle không đổi (hash không đổi), bước `setup-java` lập tức tải và giải nén cache về máy ảo runner.
   - Nhờ đó, Gradle phát hiện tất cả dependency đã có sẵn tại máy cục bộ và bỏ qua bước download.

---

## 6. Kết luận
- Việc cấu hình `cache: 'gradle'` trong action `actions/setup-java@v5` cấu hình cực kỳ đơn giản (chỉ 1 dòng) nhưng mang lại hiệu quả rất lớn:
  - Thời gian chạy kiểm thử giảm **66.7%** (từ 30s xuống 10s).
  - Tổng thời gian Job giảm **58.5%** (từ 41s xuống 17s).
- Tiết kiệm băng thông Internet, giảm hao phí phút chạy GitHub Actions runner và tăng tốc độ chu trình phản hồi kiểm thử (feedback loop) cho lập trình viên.
