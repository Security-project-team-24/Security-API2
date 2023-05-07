package SecurityAPI2.Dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenDto {
    String accessToken;
    String refreshToken;
}
