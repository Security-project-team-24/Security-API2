package SecurityAPI2.Service;

import SecurityAPI2.Exceptions.TokenExceptions.LoginTokenExpiredException;
import SecurityAPI2.Exceptions.TokenExceptions.LoginTokenInvalidException;
import SecurityAPI2.Model.LoginToken;
import SecurityAPI2.Repository.ILoginTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoginTokenService {
    @Autowired
    ILoginTokenRepository loginTokenRepository;
    
    public LoginToken findByUUID(UUID uuid){
        LoginToken loginToken = loginTokenRepository.findByUuid(uuid);
        if(loginToken == null) throw new LoginTokenInvalidException();
        return loginToken;
    }
    public void create(LoginToken token){
        loginTokenRepository.save(token);
    }
    public void delete(LoginToken token){
        loginTokenRepository.delete(token);
    }
}
