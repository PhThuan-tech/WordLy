Ngày 05/04/2025 

1 Cải thiện giao diện sử dụng CSS (Tông màu, cỡ chữ, nút bấm, ...)

2 Tạo các Controller của mỗi giao diện khác nhau (Cái này tôi xử lí đc lỗi ko thể return đc main)

3 Tạo được khung chương trình của mỗi giao diện chuyển đổi qua lại

4 Xóa API của google dịch - TextToSpeechController, TextToSpeechView 

Mỗi hôm tôi học thêm tí Javafx và CSS để xử lí các nút bấm nữa, mấy cái chức năng và giao diện nào Bình Minh với Thái chưa làm đc thì cứ Note tại đây hoặc nhắn trong nhóm để mn còn xử lí cùng

Hiện tại thì merge r pull còn 1 số lỗi tôi chưa tìm đc cách xử lí ( tôi đọc trên github và chatgpt thì xóa mấy dòng ở các file lỗi sẽ fix đc, Mở github lên r mấy oog thấy có mấy chỗ bôi đỏ là dòng cần xóa)

Ngày 08/04/2025 

1 Cải thiện giao diện CSS : các nút bấm , màu sắc.

2 Thêm chức năng Speak của mỗi từ vựng

3 Chỉnh sửa giao diện Favourite, History và How to Use,

Merge vào testing ko bị lỗi =)) làm vc tuần 7 lần commit là ổn r mấy ôg - tôi ngồi code đau lưng quá 


Ngày 09/04/2025 :
1 Thêm chức năng hiển thị lịch ở tab History

Ngày 12/04/2025 :

1 Thêm giao diện GameView và các chức năng chuyển giao diện

2 Cải thiện giao diện màn hình chính và searchView

3 Sử dụng API của Amuse để tìm từ đồng nghĩa và tìm từ trái nghĩa ( nằm trong giao diện SearchView)

4 Thêm / fix dependency jfonix , okhttp, google.com trong dependency và import trong module info ( xử lí bằng cách xóa depedency r copy chạy lại)

!!! Cần cải thiện thêm phần chuyển giao diện nên dùng tính kế thừa thay vì copy_code trong từng giao diện khác nhau ( coming soon )

Ngày 15/04/2025 :

1 Tạo được lớp cơ sở BaseController để sử dụng lại đoạn code chuyển giao diện.

2 Thái resolved được phần làm Edit and add Word và xử lí đc giao diện favourite.

3 Thử nghiệm game đoán từ Wordle, chưa lấy đc từ ngẫu nhiên từ Dictionarydev hoặc APImuse.

Ngày 20/04/2025 :

1 Sửa đổi lại đồng bộ các giao diện. 

2 Thêm các icons, audio ngắn trong game và màn hình chính

3 Fix bug lỗi merge của thái 

Ngày 28/04/2025

1 Fix lại lỗi âm thanh nhạc troll bị đè vào nhau - nhạc troll nào thế, phần nhạc trol ở main tôi fix r mà.

2 Tạo giao diện setting nhạc nền và fix đc lỗi chuyển giao diện nhạc ko bật

3 Tạo game scramble word(đoán từ được tráo sẵn) và thêm nhiều tính năng khác

4 sửa lại giao diện historyView và giao diện EditWord để xem các từ đã chỉnh sửa - phần word added tôi nhét ở favourite nhé.

5 Tạo chat bot

Ngày 30/04/2025

1 Thái thêm phần translate, tts và đọc images dùng api Azure.

2 Tôi đã training lại con bot để nó thông minh hơn, giờ nó là quái vật Tiếng Anh nhé =))).

3 Phần tts đang có vấn đề khi đọc quá nhiều văn bản -> application sẽ unresponse (đơ) cho đến khi đọc hết văn bản thì sẽ hết, cái này hình như do resquest lớn đâm ra app không load đc tôi sẽ tìm cách fix, nma ae fix đc thì alo nhé.

4 Có vài giao diện đang hơi sơ sài + chuyển giao diện đang chưa mượt.
