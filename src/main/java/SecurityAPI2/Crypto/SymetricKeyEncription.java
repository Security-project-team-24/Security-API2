package SecurityAPI2.Crypto;

import io.github.cdimascio.dotenv.Dotenv;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class SymetricKeyEncription {
    public static String encrypt(String input, byte[] key) {
        try {

//            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

            // Generate a random IV (Initialization Vector)
            byte[] ivBytes = new byte[16]; // 16 bytes for AES-128, 32 bytes for AES-256
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(ivBytes);

            // Create the AES key spec and initialization vector spec
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            // Create the AES cipher in CBC mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            // Encrypt the plaintext
            byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            // Concatenate the IV and encrypted bytes
            byte[] combinedBytes = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, combinedBytes, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combinedBytes, ivBytes.length, encryptedBytes.length);

            // Encode the combined bytes as a Base64 string
            String encryptedBase64 = Base64.getEncoder().encodeToString(combinedBytes);

            return encryptedBase64;

//            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
//            byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(encryptedBytes);

//            Dotenv dotenv = Dotenv.load();
//            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
//            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
//            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Payload could not be encrypted!");
        }
    }
}
