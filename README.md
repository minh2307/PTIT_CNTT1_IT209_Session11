# PTIT CNTT1 - IT209: Session 11

[![CI Pipeline](https://github.com/minh2307/PTIT_CNTT1_IT209_Session11/actions/workflows/ci.yml/badge.svg)](https://github.com/minh2307/PTIT_CNTT1_IT209_Session11/actions/workflows/ci.yml)

Repository bài tập môn **Tích hợp hệ thống (IT209)** - Học viện Công nghệ Bưu chính Viễn thông (PTIT).

## Danh sách bài tập
- **[Bài 3: Tối ưu tốc độ CI bằng Gradle Dependency Caching](homework/session_11/ex3/)**
  - Cấu hình tham số `cache: 'gradle'` trong action `actions/setup-java@v5`.
  - [Báo cáo so sánh thời gian thực thi (report.md)](homework/session_11/ex3/report.md)
- **[Bài 4: Khai báo GitHub Secrets và cấu hình Đăng nhập Container Registry](homework/session_11/ex4/)**
  - Khai báo Repository Secret `REGISTRY_TOKEN` với quyền `write:packages`.
  - Cấu hình xác thực tự động vào GitHub Container Registry (`ghcr.io`) với `docker/login-action@v3`.
