package SecurityAPI2.Controller;

import SecurityAPI2.Crypto.SymetricKeyDecription;
import SecurityAPI2.Dto.*;
import SecurityAPI2.Exceptions.UserBlockedException;
import SecurityAPI2.Exceptions.UserNotActivatedException;
import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.Enum.UserRole;
import SecurityAPI2.Model.Permission;
import SecurityAPI2.Model.Role;
import SecurityAPI2.Model.User;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Service.AuthService;
import SecurityAPI2.Service.UserService;
import SecurityAPI2.utils.CryptoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final AuthService authService;



    @PreAuthorize("isAuthenticated() and hasAuthority('administration')")
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        List<Role> roles = authService.getRoles();
        List<String> rolesMapped = roles.stream()
                .map(role -> role.getName())
                .toList();
        return ResponseEntity.ok(rolesMapped);

    }
    @PreAuthorize("isAuthenticated() and hasAuthority('administration')")
    @GetMapping("/permissions/{role}")
    public ResponseEntity<RolePermissionsDto> getRolePermissions(@PathVariable String role) {
        RolePermissionsDto dto = authService.getRolePermissions(role);
        System.out.println(dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('administration')")
    @PostMapping("/permissions/{role}/commit")
    public ResponseEntity<?> commitPermissions(
            @PathVariable String role,
            @RequestBody List<Permission> permissionsGranted
    ) {

        boolean containsAdministration = permissionsGranted.stream().anyMatch(p -> p.getName().equals("administration"));
        if(!containsAdministration && role.equals("ADMIN")) {
            Permission administration = Permission.builder().name("administration").build();
            permissionsGranted.add(administration);
        }
        permissionsGranted.forEach(permission -> System.out.println(permission.getName()));
        authService.commitPermissions(role, permissionsGranted);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final LoginDto loginRequest, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        System.out.println("stigaop");
        User user = userService.findByEmail(email);
        System.out.println(user);
        if(user == null) throw new BadCredentialsException("Bad credentials!");
        if(user.getStatus() != Status.ACTIVATED) throw new UserNotActivatedException();
        if(user.isBlocked()) throw new UserBlockedException();
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
    public ResponseEntity<TokenDto> passwordlessLogin(@PathVariable String token, HttpServletResponse response) {
        TokenDto data = authService.authenticateWithOneTimeToken(token);
        Cookie cookie = createRefreshTokenCookie(data.getRefreshToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto, Errors errors) {
        if(errors.hasErrors()){
            throw new InvalidPasswordFormatException();
        }
        User registered = userService.register(registerDto);
        UserDto userDto = new UserDto(registered);
        return ResponseEntity.ok(userDto);
    }
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated() and hasAuthority('all')")
    public ResponseEntity<UserDto> current(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        User user = authService.getUserFromToken(authHeader);
        if(user.hasRole(UserRole.ENGINEER)){
            System.out.println("dosao");
            Engineer engineer = userService.getEngineer(user);
            System.out.println("dosao1");
            UserDto userDto = new UserDto(user, new EngineerDto(engineer));
            System.out.println("dosao2");

            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.ok(new UserDto(user));
    }
    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refreshToken(HttpServletRequest request) {
        String refreshToken = getCookie(request, "sec_refresh_token");
        TokenDto data = authService.regenerateRefreshToken(refreshToken);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = createRefreshTokenCookie(null);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
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
