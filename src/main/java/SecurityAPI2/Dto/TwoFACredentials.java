package SecurityAPI2.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFACredentials {
    String key;
    String qrCodeUrl;
    public TwoFACredentials(String key, String qrCodeUrl) {
        this.key = key;
        this.qrCodeUrl = qrCodeUrl;
    }
}
