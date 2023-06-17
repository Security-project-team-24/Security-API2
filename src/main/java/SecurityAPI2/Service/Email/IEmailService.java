package SecurityAPI2.Service.Email;

public interface IEmailService {
    void sendApprovedMail(String email,String hmacToken);

    void sendLoginEmail(String token,String email);

    void sendDisapprovedMail(String reason, String email);

    void sendWarningEmail(String message, String subject);

}
