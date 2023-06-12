package SecurityAPI2.utils.CV;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.xml.security.Init;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

@Service
public class CVEncryption {
    private final String KEY_STORE_FILE = Dotenv.load().get("KEY_STORE_FILE");
    private final String CERT_ALIAS = Dotenv.load().get("CERT_ALIAS");
    private final String CERT_PASSWORD = Dotenv.load().get("CERT_PASSWORD");

    static {
        Security.addProvider(new BouncyCastleProvider());
        org.apache.xml.security.Init.init();
    }
    
    private Certificate readCertificate() {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12", "SunJSSE");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
            ks.load(in, CERT_PASSWORD.toCharArray());

            if (ks.isKeyEntry(CERT_ALIAS)) {
                return ks.getCertificate(CERT_ALIAS);
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private SecretKey generateDataEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private PrivateKey readPrivateKey() {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12", "SunJSSE");
            
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
            ks.load(in, CERT_PASSWORD.toCharArray());

            if (ks.isKeyEntry(CERT_ALIAS)) {
                return (PrivateKey) ks.getKey(CERT_ALIAS, CERT_PASSWORD.toCharArray());
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Document decrypt(Document doc) {
        PrivateKey privateKey = readPrivateKey();
        try {
            XMLCipher xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
            xmlCipher.setKEK(privateKey);
            NodeList encDataList = doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
            Element encData = (Element) encDataList.item(0);
            xmlCipher.doFinal(doc, encData);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Document encrypt(Document doc)  {
        SecretKey secretKey = generateDataEncryptionKey();
        Certificate certificate = readCertificate();
        try {
            if(certificate == null) throw new CertException("Cert doesnt exist!");
            
            XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
            xmlCipher.init(XMLCipher.ENCRYPT_MODE, secretKey);
            
            XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
            keyCipher.init(XMLCipher.WRAP_MODE, certificate.getPublicKey());
            
            EncryptedKey encryptedKey = keyCipher.encryptKey(doc, secretKey);
            EncryptedData encryptedData = xmlCipher.getEncryptedData();
            
            KeyInfo keyInfo = new KeyInfo(doc);
            keyInfo.addKeyName("Encryption Key");
            keyInfo.add(encryptedKey);
            encryptedData.setKeyInfo(keyInfo);
            
            NodeList cvs = doc.getElementsByTagName("cv");
            Element cv = (Element) cvs.item(0);
            
            xmlCipher.doFinal(doc, doc.getDocumentElement(), true);
            return doc;

        }catch (CertException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
