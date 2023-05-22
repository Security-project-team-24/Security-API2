package SecurityAPI2.Service;

import SecurityAPI2.Dto.TokenDto;
import SecurityAPI2.Exceptions.TokenExceptions.InvalidTokenClaimsException;
import SecurityAPI2.Exceptions.TokenExceptions.RefreshTokenExpiredException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenExpiredException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenInvalidException;
import SecurityAPI2.Exceptions.UserDoesntExistException;
import SecurityAPI2.Exceptions.UserNotActivatedException;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.LoginToken;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.ILoginTokenRepository;
import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Service.Email.IEmailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if(user.getStatus() != Status.ACTIVATED) throw new UserNotActivatedException();
        UUID loginTokenUUID = UUID.randomUUID();
        
        loginTokenRepository.save(new LoginToken(String.valueOf(loginTokenUUID.toString().hashCode())));
        String loginToken = jwtUtils.generateLoginToken(email,loginTokenUUID);
        emailService.sendLoginEmail(loginToken,email);
    }
    public TokenDto authenticateWithOneTimeToken(String loginTokenJwt) {
        try{
            jwtUtils.validateLoginToken(loginTokenJwt);
        }catch(final ExpiredJwtException e){
            throw new TokenExpiredException("Your login link expired.");
        }catch(final Exception e){
            throw new TokenInvalidException("Your login link invalid");
        }
        Claims loginClaims = jwtUtils.getClaimsFromLoginToken(loginTokenJwt);
        System.out.println(loginClaims.get("uuid").toString().hashCode());
        LoginToken loginToken = loginTokenRepository.findByHashedUuid(
                String.valueOf(loginClaims.get("uuid").toString().hashCode())
        );
        if (loginToken == null)
            throw new TokenInvalidException("Your login link invalid");
        loginTokenRepository.delete(loginToken);
        return generateTokens(loginClaims.getSubject());
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
