package SecurityAPI2.Service.Authenticator;

import SecurityAPI2.Dto.TwoFACredentials;
import SecurityAPI2.Service.Storage.IStorageService;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleTwoFactorAuthenticator implements IAuthenticator{
    private final IStorageService storageService;
    @Override
    public TwoFACredentials generateCredentials(String userEmail) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();

        // Generate a new secret key
        GoogleAuthenticatorKey key = gAuth.createCredentials();

        // Get the secret key as a string
        String secretKey = key.getKey();
        String appName = "SecurityApp";
        String qrCodeUrl = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                appName, userEmail, secretKey, appName);
        MultipartFile qrCodeImage = generateQRCodeAsMultipartFile(qrCodeUrl, 500, 500);
        System.out.println("qr name" + qrCodeImage.getOriginalFilename());
        String url = "";
        try{
            url = storageService.uploadFile(qrCodeImage);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
        // Display the QR code image in an <img> tag
//        String base64Image = encodeToBase64(qrCodeImage);
//        System.out.println("base" + base64Image);
        return new TwoFACredentials(secretKey, url);
    }

    @Override
    public boolean authorize(String secretKey, String userEnteredCode) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean isCodeValid = gAuth.authorize(secretKey, Integer.parseInt(userEnteredCode));

        if (isCodeValid)
            return true;
        return false;
    }

    private static MultipartFile generateQRCodeAsMultipartFile(String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 10);
            QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.L, hints);

            int qrCodeSize = qrCode.getMatrix().getWidth();
            int finalWidth = Math.max(qrCodeSize, width);
            int finalHeight = Math.max(qrCodeSize, height);
            BufferedImage qrCodeImage = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = qrCodeImage.createGraphics();
            graphics.setBackground(Color.WHITE);
            graphics.clearRect(0, 0, finalWidth, finalHeight);
            graphics.setColor(Color.BLACK);

            byte [][] scaledMatrix = scaleQRCodeMatrix(qrCode.getMatrix().getArray(), 5);

            for (int x = 0; x < scaledMatrix.length; x++) {
                for (int y = 0; y < scaledMatrix[0].length; y++) {
                    if (scaledMatrix[x][y] == 1) {
                        graphics.fillRect(x , y, 1, 1);
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return new MockMultipartFile("qrcode.png","qrcode.png","image/png",imageBytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static String encodeToBase64(BufferedImage image){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            ImageIO.write(image, "png", outputStream);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static byte[][] scaleQRCodeMatrix(byte[][] qrCodeMatrix, int scalingFactor) {
        int rows = qrCodeMatrix.length;
        int columns = qrCodeMatrix[0].length;

        int scaledRows = rows * scalingFactor;
        int scaledColumns = columns * scalingFactor;

        byte[][] scaledMatrix = new byte[scaledRows][scaledColumns];

        // Scale each module of the QR code
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                byte module = qrCodeMatrix[i][j];

                // Scale the module to a larger size
                for (int k = 0; k < scalingFactor; k++) {
                    for (int l = 0; l < scalingFactor; l++) {
                        scaledMatrix[i * scalingFactor + k][j * scalingFactor + l] = module;
                    }
                }
            }
        }

        return scaledMatrix;
    }

}
