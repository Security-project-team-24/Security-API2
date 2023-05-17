package SecurityAPI2.Controller;

import SecurityAPI2.Dto.TokenDto;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Exceptions.UserNotActivatedException;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.User;
import SecurityAPI2.Dto.LoginDto;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Service.AuthService;
import SecurityAPI2.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final LoginDto loginRequest, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if(userService.findByEmail(email).getStatus() != Status.ACTIVATED) throw new UserNotActivatedException();
        Authentication authStrategy = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authStrategy);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto data = authService.generateTokens(email);
        Cookie cookie = createRefreshTokenCookie(data.getRefreshToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/send/login/{email}")
    public ResponseEntity<Void> sendLoginEmail(@Email @PathVariable String email) {
        authService.createOneTimeToken(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/passwordless/login/{token}")
    public ResponseEntity<TokenDto> passwordlessLogin(@PathVariable String token) {
        TokenDto data = authService.authenticateWithOneTimeToken(token);
        return ResponseEntity.ok(data);
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
        User user = authService.getUserFromToken(authHeader);
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }
    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refreshToken(HttpServletRequest request) {
        String refreshToken = getCookie(request, "sec_refresh_token");
        TokenDto data = authService.regenerateRefreshToken(refreshToken);
        return ResponseEntity.ok(data);
    }

    private String getCookie(HttpServletRequest req, String name) {
        return Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("sec_refresh_token", refreshToken);
        cookie.setMaxAge(1000 * 60 * 3);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
