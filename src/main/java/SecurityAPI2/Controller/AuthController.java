package SecurityAPI2.Controller;

import SecurityAPI2.Dto.LoginDto;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final LoginDto loginRequest) {
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterDto> register(@Valid @RequestBody RegisterDto registerDto, Errors errors) {
        if(errors.hasErrors()){
            throw new InvalidPasswordFormatException();
        }
        RegisterDto register = userService.register(registerDto);
        return ResponseEntity.ok(register);
    }
}
