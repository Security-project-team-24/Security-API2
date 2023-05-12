package SecurityAPI2.Controller;

import SecurityAPI2.Dto.TokenDto;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.User;
import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Dto.LoginDto;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final LoginDto loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String accessToken = jwtUtils.generateAccessToken(authentication);
        final String refreshToken = jwtUtils.generateRefreshToken(authentication);
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
