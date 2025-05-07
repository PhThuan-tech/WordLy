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

1 Thái thêm phần translate, tts và đọc images dùng api Azure, xóa vài cái không quan trọng đi(không liên quan đến file của mng nhé).

2 Tôi đã training lại con bot để nó thông minh hơn, giờ nó là quái vật Tiếng Anh nhé =))).

3 Phần tts đang có vấn đề khi đọc quá nhiều văn bản -> application sẽ unresponse (đơ) cho đến khi đọc hết văn bản thì sẽ hết, cái này hình như do resquest lớn đâm ra app không load đc tôi sẽ tìm cách fix, nma ae fix đc thì alo nhé.

4 Có vài giao diện đang hơi sơ sài + chuyển giao diện đang chưa mượt.

Ngày  01.05.2025

1. Bình Minh thêm chức năng dừng nhạc ở MAINVIEW khi ấn các nút khác.
2. Cải thiện tốc độ xử lí khi thực hiện chuyển tab (Sử dụng THREAD - Xử lí đồng thời). Tạm thời mới áp dụng cho HISTORY TAB.
3. BOT đang có vấn đề khi đang chat nếu user có ấn enter thì đang bị kéo theo câu trả lời (Tự test đi, không thể miêu tả kĩ được).

Ngày 02/05/2025

1. Thêm đọc image ocr, hủy voice.
2. Training lại promt cho bot, promt của user.
3. Sửa phần bình minh bảo, sẽ không kéo theo câu trả lời nữa.
4. Giao diện đang sơ sài + chuyển vẫn không mượt :v
5. Phần tts nếu cho nhiều từ, hết bị đơ, nhưng lại không dừng lại bằng nút được (ít từ thì nút dừng được). Tôi sẽ tìm cách fix.

Ngày 03/05/2025
1. Thay đổi giao diện toàn bộ các scence cho gọn.
2. Gộp các chức năng đặc biệt vào Advance Features cho đỡ nhiều nút
3. Fix lỗi Game Wordle (loại bỏ DictionaryDev) và thêm gợi ý bằng tiếng việt
4. Mở rộng tính năng lưu các từ Syn and An vào trie
Mấy file game_data của Bminh đag bị mất, t ko bt ai xóa nx chạy ko đc các game ấy.
Animation chuyển giao diện thì t tìm hiểu bị chậm là do phần initialize ( kiểu theo cơ chế stack) nên là phần code trong intialize dài thì code sẽ chạy tuần tự rất lâu, nên là chia nhỏ ra snhé

Ngày 4 -> 6 /05/2025
1. Thay đổi giao diện GameView, Xóa các comment ko cần thiết.
2. Phần TTS chạy ổn định hơn, có thêm chức năng SpeechToText, chuyển đổi dịch nhiều loại ngôn ngữ, chức năng đọc file văn bản để dịch.
3. Fix đc lỗi ko đọc đc file txt ( Nguyên nhân là do thêm đuôi .txt trong gitignore chỉ cần xóa trong .gitignore là chạy đc các game sử dụng)
Nhiệm vụ cuối là tìm lỗi ở các giao diện và comment ở các dòng code quan trọng như lấy API, đọc file đuôi .txt, sử dụng interface, abstract như thế nào. Đặc biệt là comment cả phần try-catch(xử lí ngoại lệ ntn nên tìm hiểu xem thường thì code sẽ xảy ra loại nào ví dụ như Lỗi dường dẫn URL, Lỗi mạng, Trang web đúng URL nhưng bị sập hoặc hết key dùng API).
Sắp xong r ae ơi, cố lên còn ôn lý thuyết cuối kì nữa !!
