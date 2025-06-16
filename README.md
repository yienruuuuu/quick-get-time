# Quick Get Time 自動查詢幾點上班系統

一套基於 Spring Boot + Tesseract OCR 的自動化打卡紀錄查詢工具，
支援自動識別圖片驗證碼、自動登入與查詢當日打卡時間。

## 🔧 安裝與執行

### 前置需求

- JDK 17+
- Gradle
- [Tesseract OCR](https://github.com/tesseract-ocr/tesseract) 5.x
    - 建議安裝目錄：`C:\Program Files\Tesseract-OCR`
    - 環境變數：
      ```
      TESSDATA_PREFIX=C:\Program Files\Tesseract-OCR\tessdata
      ```

---

### 本地執行

```bash
# 建置 JAR
./gradlew clean build

# 啟動應用程式
java -jar build/libs/quick-get-time-0.0.1-SNAPSHOT.jar
