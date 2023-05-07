package SecurityAPI2.Dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
public class TokenDto {
    String accessToken;
    String refreshToken;
}
