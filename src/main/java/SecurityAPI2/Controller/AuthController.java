package SecurityAPI2.Controller;

import SecurityAPI2.Dto.TokenDto;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Exceptions.TokenExceptions.LoginTokenExpiredException;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.LoginToken;
import SecurityAPI2.Model.User;
import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Dto.LoginDto;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Security.UserDetails.UserDetailsImpl;
import SecurityAPI2.Service.LoginTokenService;
import SecurityAPI2.Service.UserService;
import SecurityAPI2.utils.Email.EmailSender;
import io.jsonwebtoken.Claims;
import org.hibernate.validator.internal.constraintvalidators.hv.ISBNValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    EmailSender emailSender;
    @Autowired
    LoginTokenService loginTokenService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final LoginDto loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String accessToken = jwtUtils.generateAccessToken(authentication);
        final String refreshToken = jwtUtils.generateRefreshToken(authentication);
        return ResponseEntity.ok(new TokenDto(accessToken, refreshToken));
    }

    @PostMapping("/send/login/{email}")
    public ResponseEntity<Void> sendLoginEmail(@Email @PathVariable String email) {
        User user = userService.findByEmail(email);
        UUID loginTokenUUID = UUID.randomUUID();
        String loginToken = jwtUtils.generateLoginToken(loginTokenUUID,email);
        loginTokenService.create(new LoginToken(loginTokenUUID));
        emailSender.sendLoginEmail(loginToken,email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/passwordless/login/{token}")
    public ResponseEntity<TokenDto> passwordlessLogin(@PathVariable String token) {
        Claims jwtToken;
        try{
            jwtToken = jwtUtils.verifyToken(token);   
        } catch (Exception e) {
            throw new LoginTokenExpiredException();
        }
        LoginToken loginToken = loginTokenService.findByUUID(UUID.fromString((String) jwtToken.get("uuid")));
        final String accessToken = jwtUtils.generateAccessToken(jwtToken.getSubject());
        final String refreshToken = jwtUtils.generateRefreshToken(jwtToken.getSubject());
        loginTokenService.delete(loginToken);
        return ResponseEntity.ok(new TokenDto(accessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto, Errors errors) {
        if(errors.hasErrors()){
            throw new InvalidPasswordFormatException();
        }
        UserDto userDto = userMapper.userToUserDto(userService.register(registerDto));
        return ResponseEntity.ok(userDto);
    }
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> current(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        if (authHeader.length() > 7) {
            try {
                User user = jwtUtils.getUserFromToken(authHeader);
                return ResponseEntity.ok(userMapper.userToUserDto(user));
            } catch (final Exception e) {
                return null;
            }

        }
        return ResponseEntity.status(401).build();
    }
}
