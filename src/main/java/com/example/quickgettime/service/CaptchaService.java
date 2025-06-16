package com.example.quickgettime.service;

import com.example.quickgettime.bean.CaptchaDTO;
import com.example.quickgettime.bean.CaptchaResponse;
import com.example.quickgettime.config.AppConfig;
import com.example.quickgettime.infrastructure.RestTemplateClient;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

/**
 * @author Eric.Lee
 * Date: 2025/6/16
 */
@Service("captchaService")
public class CaptchaService {

    private final RestTemplateClient restTemplateClient;
    private final AppConfig appConfig;
    private final Tesseract tesseract;

    public CaptchaService(RestTemplateClient restTemplateClient,
                          AppConfig appConfig,
                          Tesseract tesseract) {
        this.restTemplateClient = restTemplateClient;
        this.appConfig = appConfig;
        this.tesseract = tesseract;
    }

    /**
     * 獲取驗證碼並判讀。
     * 這個方法會循環獲取驗證碼，直到識別的驗證碼長度為 4 為止。
     *
     * @return 識別的驗證碼
     */
    public CaptchaDTO getCaptchaToken() {
        String captchaCode = "";
        CaptchaResponse base64 = null;
        int maxRetries = 5;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            base64 = this.getCaptchaBase64();
            if (base64 == null || base64.img() == null) {
                System.err.println("取得驗證碼失敗，嘗試次數：" + attempt);
                continue;
            }
            this.saveCaptchaImage(base64);
            captchaCode = this.recognize();

            System.out.println("第 " + attempt + " 次識別結果：" + captchaCode);
            if (captchaCode.length() == 4) {
                return new CaptchaDTO(base64.captchaToken(), captchaCode);
            }
        }
        throw new RuntimeException("超過最大重試次數，無法成功識別驗證碼！");
    }

    /**
     * 獲取驗證碼的 Base64 編碼和圖片 URL。
     * 這個方法會向指定的系統域名發送請求以獲取驗證碼。
     */
    public CaptchaResponse getCaptchaBase64() {
        String url = appConfig.getSystemDomain() + "/genCaptcha";
        try {
            CaptchaResponse response = restTemplateClient.post(
                    url,
                    null,
                    new ParameterizedTypeReference<CaptchaResponse>() {
                    }
            );

            System.out.println("驗證碼：" + response.captchaToken());
            System.out.println("圖片URL：" + response.img());
            return response;
        } catch (Exception e) {
            System.err.println("Error fetching captcha: " + e.getMessage());
        }
        return null;
    }


    /**
     * 將 Base64 編碼的圖片還原為圖片檔案。
     * 這個方法會將 Base64 編碼的圖片寫入到本地檔案系統中。
     *
     * @param response 包含 Base64 編碼圖片的 CaptchaResponse 對象
     */
    public void saveCaptchaImage(CaptchaResponse response) {
        try {
            // 去除開頭 "data:image/png;base64,"（若有的話）
            String base64Data = response.img().replaceFirst("^data:image/\\w+;base64,", "");

            // 解碼 base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // 寫入檔案
            try (FileOutputStream fos = new FileOutputStream("captcha.png")) {
                fos.write(imageBytes);
            }

            System.out.println("圖片已儲存為 captcha.png");

        } catch (Exception e) {
            System.err.println("圖片還原失敗：" + e.getMessage());
        }
    }

    public String recognize() {
        String imagePath = "captcha.png";
        String outputPath = "processed.png";
        try {
            File preprocessed = preprocess(imagePath, outputPath);
            // 使用 Tesseract 進行 OCR 識別
            return tesseract.doOCR(preprocessed)
                    .replaceAll("O", "0")
                    .replaceAll("[^0-9]", "")
                    .trim();
        } catch (Exception e) {
            System.err.println("OCR 失敗：" + e.getMessage());
            return null;
        } finally {
            this.deleteFile(imagePath);
            this.deleteFile(outputPath);
        }
    }

    /**
     * 刪除指定路徑的檔案。
     * 這個方法會檢查檔案是否存在，並嘗試刪除它。
     *
     * @param path 要刪除的檔案路徑
     */
    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists() && file.delete()) {
            System.out.println("已清除檔案：" + path);
        }
    }

    /**
     * 圖片預處理（灰階 → 放大 → 二值化）
     */
    public static File preprocess(String inputPath, String outputPath) throws Exception {
        BufferedImage original = ImageIO.read(new File(inputPath));

        // 放大兩倍
        int width = original.getWidth() * 2;
        int height = original.getHeight() * 2;
        Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage enlarged = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = enlarged.createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();

        // 灰階處理
        BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g2 = gray.getGraphics();
        g2.drawImage(enlarged, 0, 0, null);
        g2.dispose();

        // 二值化（固定閾值法）
        for (int y = 0; y < gray.getHeight(); y++) {
            for (int x = 0; x < gray.getWidth(); x++) {
                int pixel = gray.getRGB(x, y) & 0xFF;
                int newPixel = (pixel < 130) ? 0 : 255;
                int rgb = newPixel << 16 | newPixel << 8 | newPixel;
                gray.setRGB(x, y, rgb);
            }
        }

        File out = new File(outputPath);
        ImageIO.write(gray, "png", out);
        return out;
    }
}
