package SecurityAPI2;

import SecurityAPI2.Service.Interfaces.IStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.security.SecureRandom;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SecurityApi2Application {

	int strength = 10;
	@Resource
	IStorageService storageService;
	public static void main(String[] args) {
		SpringApplication.run(SecurityApi2Application.class, args);
	}

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder(strength, new SecureRandom());
	}

}
