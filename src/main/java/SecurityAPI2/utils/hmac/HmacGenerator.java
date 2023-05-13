package SecurityAPI2.utils.hmac;

import io.github.cdimascio.dotenv.Dotenv;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class HmacGenerator {
    public static String generate(String data) {
        Digest digest = new SHA256Digest();
        Dotenv dotenv = Dotenv.load();
        HMac hMac = new HMac(digest);
        hMac.init(new KeyParameter(dotenv.get("HMAC_PASSWORD").getBytes()));

        byte[] hmacIn = data.getBytes();
        hMac.update(hmacIn, 0, hmacIn.length);
        byte[] hmacOut = new byte[hMac.getMacSize()];

        hMac.doFinal(hmacOut, 0);
        return bytesToHex(hmacOut);
    }
    private static String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
