package SecurityAPI2.Crypto;

import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAKeyConverter {

    public static PrivateKey getPrivateKeyFromString(String privateKeyString) {
        privateKeyString = privateKeyString
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);

            // Create the PKCS8EncodedKeySpec
            BigInteger modulus = new BigInteger(1, privateKeyBytes);
            BigInteger privateExponent = new BigInteger(1, privateKeyBytes);

            // Create the RSAPrivateCrtKeySpec
            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                    modulus, BigInteger.ZERO, privateExponent, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);

            // Generate the PrivateKey object
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PublicKey getPublicKeyFromString(String publicKeyString)  {
       publicKeyString = publicKeyString
              .replace("-----BEGIN PUBLIC KEY-----", "")
               .replace("-----END PUBLIC KEY-----", "")
               .replaceAll("\\s", "");

        try{
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            // Generate the PublicKey object
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

            // Generate the PublicKey object
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            return publicKey;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
