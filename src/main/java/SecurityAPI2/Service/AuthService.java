package SecurityAPI2.Service;

import SecurityAPI2.Dto.TokenDto;
import SecurityAPI2.Exceptions.TokenExceptions.InvalidTokenClaimsException;
import SecurityAPI2.Exceptions.TokenExceptions.LoginTokenInvalidException;
import SecurityAPI2.Exceptions.TokenExceptions.RefreshTokenExpiredException;
import SecurityAPI2.Exceptions.UserDoesntExistException;
import SecurityAPI2.Model.LoginToken;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.ILoginTokenRepository;
import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Service.Email.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final ILoginTokenRepository loginTokenRepository;
    private final IEmailService emailService;

    public void createOneTimeToken(String email) {
        User user = userService.findByEmail(email);
        if(user == null)
            throw new UserDoesntExistException();
        UUID loginTokenUUID = UUID.randomUUID();
        String loginToken = jwtUtils.generateLoginToken(loginTokenUUID,email);
        loginTokenRepository.save(new LoginToken(loginTokenUUID));
        emailService.sendLoginEmail(loginToken,email);
    }
    public TokenDto authenticateWithOneTimeToken(String token) {
        String uuidStr = jwtUtils.getUuidFromJwtToken(token);
        String email = jwtUtils.getEmailFromJwtToken(token);
        UUID uuid = UUID.fromString(uuidStr);
        LoginToken loginToken = loginTokenRepository.findByUuid(uuid);
        if (loginToken == null)
            throw new LoginTokenInvalidException();
        loginTokenRepository.delete(loginToken);
        return generateTokens(email);
    }
    public TokenDto generateTokens(String email) {
        String refreshToken = jwtUtils.generateRefreshToken(email);
        String accessToken = jwtUtils.generateAccessToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

    public TokenDto regenerateRefreshToken(String refreshToken) {
        boolean isRefreshTokenValid = jwtUtils.validateJwtToken(refreshToken);
        if(!isRefreshTokenValid)
            throw new RefreshTokenExpiredException();
        String email = jwtUtils.getEmailFromJwtToken(refreshToken);
        String accessToken = jwtUtils.generateAccessToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

    public User getUserFromToken(String authHeader) {
        String token = getTokenFromAuthHeader(authHeader);
        String email = jwtUtils.getEmailFromJwtToken(token);
        User user = userService.findByEmail(email);
        if (user == null)
            throw new InvalidTokenClaimsException();
        return user;
    }

    private String getTokenFromAuthHeader(String header) {
        if(header == null) return null;
        String[] chunks = header.split(" ");
        if(chunks.length < 2) return null;
        return chunks[1];
    }
}