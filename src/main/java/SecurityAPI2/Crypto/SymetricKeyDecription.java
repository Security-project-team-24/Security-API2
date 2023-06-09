package SecurityAPI2.Crypto;

import io.github.cdimascio.dotenv.Dotenv;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Base64;

public class SymetricKeyDecription {
    public static String decrypt(String encryptedInput, byte[] key) {
        try {
            //byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

            // Decode the Base64 string to get the combined bytes
            byte[] combinedBytes = Base64.getDecoder().decode(encryptedInput);

            // Extract the IV bytes from the combined bytes
            byte[] ivBytes = Arrays.copyOfRange(combinedBytes, 0, 16); // 16 bytes for AES-128, 32 bytes for AES-256

            // Extract the encrypted bytes from the combined bytes
            byte[] encryptedBytes = Arrays.copyOfRange(combinedBytes, 16, combinedBytes.length);

            // Create the AES key spec and initialization vector spec
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            // Create the AES cipher in CBC mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            // Decrypt the encrypted bytes
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert the decrypted bytes to plaintext
            String plaintext = new String(decryptedBytes, StandardCharsets.UTF_8);

            return plaintext;



//
//            Dotenv dotenv = Dotenv.load();
//            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, keySpec);
//            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedInput);
//            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
//            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Something wrong with decrypthing data!");
        }
    }
}
