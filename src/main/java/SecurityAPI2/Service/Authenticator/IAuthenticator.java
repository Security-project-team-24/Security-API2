package SecurityAPI2.Service.Authenticator;

import SecurityAPI2.Dto.TwoFACredentials;

public interface IAuthenticator {
    TwoFACredentials generateCredentials(String userEmail);
    boolean authorize(String secret, String userEnteredCode);
}
