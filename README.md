![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-3776AB?style=for-the-badge&logo=OpenJFX&logoColor=white)
![GitHub](https://img.shields.io/badge/Repo-WordLy-blue?style=for-the-badge&logo=github)
# Ứng dụng hỗ trợ học tiếng Anh sử dụng ngôn ngữ Java

## 📑 Mục lục
- [👨‍💻 Nhóm tác giả](#-nhóm-tác-giả)
- [📝 Mô tả](#-mô-tả)
- [🛠️ Hướng dẫn cài đặt](#-hướng-dẫn-cài-)
- [📖 Hướng dẫn sử dụng](#-hướng-dẫn-sử-dụng)
- [🚀 Tính năng nâng cao](#-tính-năng-nâng-cao)
- [🎯 Dự định tương lai](#-dự-định-tương-lai)
- [🤝 Đóng góp](#-đóng-góp)
- [📅 Trạng thái dự án](#-trạng-thái-dự-án)
- [📝 Ghi chú](#-ghi-chú)

## 👨‍💻 Nhóm tác giả
**Nhóm Tam-Thái-Tử**  
- Trịnh Bình Minh – `24022413`  
- Phạm Văn Vương Thuận – `24022461`  
- Phạm Danh Thái – `24022449`  

## 📝 Mô tả
Ứng dụng được thiết kế để hỗ trợ học tiếng Anh, phát triển bằng **Java** với thư viện **JavaFX** và **CSS**, dựa trên mô hình **MVC**. Ứng dụng tích hợp các tính năng sau:
- **Tra từ điển**: Sử dụng API từ [dictionaryapi.dev](https://api.dictionaryapi.dev) để tra từ, cung cấp **phiên âm**, **loại từ**, **âm thanh**, **định nghĩa**, và **ví dụ sử dụng**.
- **Quiz ôn tập**: Các trò chơi giúp người dùng ôn tập từ vựng và kiến thức.
- **Chat-bot**: Tích hợp API từ **Gemini** để luyện giao tiếp tiếng Anh.
- **Tính năng nâng cao**: Hỗ trợ **Text-to-Speech**, tra từ đồng/trái nghĩa, và các công cụ học tập khác.

## 🔧 Thiết lập cấu hình API

1. Tạo file `config.properties` tại thư mục gốc dự án
2. Dán các API key và endpoint theo mẫu file `config.properties.example`
3. Không commit file `config.properties` lên GitHub

👉 Nếu bạn chưa có key, hãy đăng ký tại [Azure Portal](https://portal.azure.com)

## 🛠️ Hướng dẫn cài đặt
1. Cài đặt IntelliJ IDEA và Scene Builder.
1. Tải mã nguồn từ [GitHub WordLy](https://github.com/PhThuan-tech/WordLy/tree/main)
2. Mở project trong IDE.
3. Chạy file chính là WordlyApplication để khởi động ứng dụng.

## 📖 Hướng dẫn sử dụng

### 🔍 Tra từ (Tab Search)
- Nhập từ cần tra để nhận **phiên âm**, **loại từ**, **âm thanh**, **định nghĩa**, và **ví dụ**.
- Có thể tra cứu từ điển tiếng anh online và offline
- Từ đã tra được lưu vào file `history.txt` và hiển thị trong tab **History**.
- Người dùng có thể đánh dấu từ yêu thích, lưu vào file `favourite.txt` và xem trong tab **Favourite**.

### ➕ Thêm từ mới (Tab Edit Word)
- Người dùng có thể tự tạo và thêm từ mới vào cơ sở dữ liệu.

### 🎮 Tab Game (Trò chơi ôn tập)
Ứng dụng cung cấp các mini-game tương tác để giúp bạn ôn tập từ vựng một cách hiệu quả:
1.  **Definition Game**: Trò chơi trắc nghiệm, yêu cầu người chơi chọn định nghĩa đúng nhất cho một từ cho trước từ bốn lựa chọn.
2.  **Wordle**: Một phiên bản của trò chơi đoán từ Wordle nổi tiếng, trong đó bạn cần đoán từ tiếng Anh dựa trên định nghĩa gợi ý (thường là tiếng Việt).
3.  **Scramble Word**: Sắp xếp lại các chữ cái bị xáo trộn để tạo thành từ có nghĩa.

### 🚀 Tính năng nâng cao
- 🗣️ **Text-to-Speech**: Chuyển đổi **âm thanh**, **hình ảnh**, hoặc **văn bản** thành bản dịch. Hỗ trợ đọc văn bản.
- 🔍 **Word Finding**: Tra từ **đồng nghĩa** hoặc **trái nghĩa** trong tab Word Finding.
- 🤖 **Chat Bot**: Luyện giao tiếp tiếng Anh với *Quái vật tiếng Anh* qua tab Chat Bot.

## 🎯 Dự định tương lai
🚀 Tối ưu hiệu năng

✨ Thêm mini game và dạng bài tập mới

🎨 Nâng cấp giao diện thân thiện hơn với người dùng

## 🤝 Đóng góp
Mọi ý kiến đóng góp từ người dùng đều được đón nhận và sử dụng để cải thiện ứng dụng trong tương lai.

## 📅 Trạng thái dự án
- **Ngày thực hiện**: 13/03/2025
- **Ngày hoàn thành**: 07/05/2025

## 📝 Ghi chú
- Ứng dụng được xây dựng và đóng góp bởi 3 sinh viên nhằm hỗ trợ học tiếng Anh.
- Một số tính năng có thể còn hạn chế và sẽ được cải thiện trong tương lai.
- Mã nguồn tham khảo từ các tài liệu, hướng dẫn, và công cụ hỗ trợ (AI, Google, v.v.).
