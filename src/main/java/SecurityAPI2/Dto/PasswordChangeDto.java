package SecurityAPI2.Dto;

import java.time.LocalDateTime;

import SecurityAPI2.utils.ValidPassword;
import lombok.Data;
import lombok.Value;

@Data
public class PasswordChangeDto {
	String oldPassword;
	@ValidPassword
	String newPassword;
	String confirmPassword;
}
