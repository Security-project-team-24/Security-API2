package SecurityAPI2.utils.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value
    ("${spring.mail.username}") private String sender;

    @Async
    public void sendApprovedMail(String email)
    {
        EmailDetails details = new EmailDetails();
        details.setRecipient(email);
        details.setMsgBody("Welcome to our company!<br/>" +
                "You can <a href=\"http://localhost:3000/user/activation/\">Activate your account here!<a/></h2> <br/>");
        details.setSubject("Welcome email from company 24");
        sendEmail(details);
    }

    @Async
    public void sendDisapprovedMail(String reason, String email)
    {
        EmailDetails details = new EmailDetails();
        details.setRecipient(email);
        details.setMsgBody("Sorry to inform you.<br/>" +
                "Your application for registration in our company was denied because:"+ reason);
        details.setSubject("Information from company 24");
        sendEmail(details);
    }
    private void sendEmail(EmailDetails details){
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");
            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody(), true);
            helper.setSubject(details.getSubject());
            javaMailSender.send(mailMessage);
        }
        catch (Exception ignored) {
        }
    }
}
