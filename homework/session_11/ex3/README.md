# Bài 3: Tối ưu tốc độ CI bằng Gradle Dependency Caching

## 1. Mục tiêu
Bật tính năng Caching cho Gradle Dependencies trong GitHub Actions để giảm thời gian tải các thư viện từ Internet mỗi lần luồng CI chạy.

## 2. Cấu hình Workflow CI (`.github/workflows/ci.yml`)
Trong bước `actions/setup-java@v5`, bổ sung tham số `cache: 'gradle'`:
```yaml
      - name: Thiết lập môi trường Java JDK 17
        uses: actions/setup-java@v5
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'gradle'
```

## 3. Các bước thực hiện
1. **Push lần 1 (Cold Run - Chưa có cache):**
   - GitHub Actions chạy lần đầu, chưa tìm thấy cache dependencies.
   - Gradle tải các dependencies từ internet.
   - Sau khi hoàn thành job, bước hậu xử lý (Post setup-java) lưu toàn bộ cache Gradle lên GitHub Actions Cache storage.
2. **Push lần 2 (Warm Run - Đã có cache):**
   - Bước setup-java khôi phục cache thành công: `Cache restored successfully from key: gradle-...`.
   - Gradle tái sử dụng các thư viện đã tải, không cần download lại từ internet.
   - Thời gian thực thi `./gradlew test` và tổng thời gian Job giảm rõ rệt.

Chi tiết đối soát và bằng chứng thực thi được trình bày trong file [report.md](report.md).
