package SecurityAPI2.Service;

import SecurityAPI2.Dto.RolePermissionsDto;
import SecurityAPI2.Dto.TokenDto;
import SecurityAPI2.Exceptions.TokenExceptions.InvalidTokenClaimsException;
import SecurityAPI2.Exceptions.TokenExceptions.RefreshTokenExpiredException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenExpiredException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenInvalidException;
import SecurityAPI2.Exceptions.UserBlockedException;
import SecurityAPI2.Exceptions.UserDoesntExistException;
import SecurityAPI2.Exceptions.UserNotActivatedException;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.LoginToken;
import SecurityAPI2.Model.Permission;
import SecurityAPI2.Model.Role;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.ILoginTokenRepository;
import SecurityAPI2.Repository.IPermissionRepository;
import SecurityAPI2.Repository.IRoleRepository;
import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Service.Email.IEmailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final ILoginTokenRepository loginTokenRepository;
    private final IEmailService emailService;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    public void createOneTimeToken(String email) {
        User user = userService.findByEmail(email);
        if(user == null) throw new UserDoesntExistException();
        if(user.isBlocked()) throw new UserBlockedException();
        if(user.getStatus() != Status.ACTIVATED) throw new UserNotActivatedException();
        UUID loginTokenUUID = UUID.randomUUID();
        
        loginTokenRepository.save(new LoginToken(String.valueOf(loginTokenUUID.toString().hashCode())));
        String loginToken = jwtUtils.generateLoginToken(email,loginTokenUUID);
        emailService.sendLoginEmail(loginToken,email);
    }
    public TokenDto authenticateWithOneTimeToken(String loginTokenJwt) {
        Claims loginClaims;
        try{
            jwtUtils.validateLoginToken(loginTokenJwt);
            loginClaims = jwtUtils.getClaimsFromLoginToken(loginTokenJwt);
        }catch(final ExpiredJwtException e){
            throw new TokenExpiredException("Your login link expired.");
        }catch(final Exception e){
            throw new TokenInvalidException("Your login link invalid");
        }
        LoginToken loginToken = loginTokenRepository.findByHashedUuid(
                String.valueOf(loginClaims.get("uuid").toString().hashCode())
        );
        if (loginToken == null)
            throw new TokenInvalidException("Your login link invalid");
        loginTokenRepository.delete(loginToken);
        User user = userService.findByEmail(loginClaims.getSubject());
        if(user.isBlocked()) throw new UserBlockedException();
        return generateTokens(loginClaims.getSubject());
    }
    public TokenDto generateTokens(String email) {
        String refreshToken = jwtUtils.generateRefreshToken(email);
        String accessToken = jwtUtils.generateAccessToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

    public TokenDto regenerateRefreshToken(String refreshToken) {
        boolean isRefreshTokenValid = jwtUtils.validateJwtToken(refreshToken);
        if(!isRefreshTokenValid)
            throw new RefreshTokenExpiredException();
        String email = jwtUtils.getEmailFromJwtToken(refreshToken);
        User user = userService.findByEmail(email);
        if(user.isBlocked()) throw new UserBlockedException();
        String accessToken = jwtUtils.generateAccessToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

    public User getUserFromToken(String authHeader) {
        String token = getTokenFromAuthHeader(authHeader);
        String email = jwtUtils.getEmailFromJwtToken(token);
        User user = userService.findByEmail(email);
        if (user == null)
            throw new InvalidTokenClaimsException();
        return user;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public RolePermissionsDto getRolePermissions(String role) {
        List<Permission> granted = permissionRepository.findPermissionsByRolesName(role);
        List<Permission> notGranted = permissionRepository.findPermissionsNotGranted(role);
        return new RolePermissionsDto(notGranted, granted);
    }

    public void commitPermissions(String name, List<Permission> permissions) {
        permissionRepository.removeAllPermissionsByRole(name);
        Role role = roleRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("No roles found with given name!"));
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
    }

    private String getTokenFromAuthHeader(String header) {
        if(header == null) return null;
        String[] chunks = header.split(" ");
        if(chunks.length < 2) return null;
        return chunks[1];
    }


}
