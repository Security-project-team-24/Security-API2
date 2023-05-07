package SecurityAPI2.Security;

import SecurityAPI2.Model.User;
import SecurityAPI2.Service.UserService;
import SecurityAPI2.Security.UserDetails.UserDetailsImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	@Autowired
	private UserService userService;
	private final String jwtSecret = "SecuritySecret";
	private final int accessTokenExpirationMs = 1000 * 60 * 15; //15 min
	private final int refreshTokenExpirationMs = 1000 * 60 * 60 * 2; //2 sata

	public String generateAccessToken(final Authentication authentication) {
		return generateToken(authentication, accessTokenExpirationMs);
	}

	public String generateRefreshToken(final Authentication authentication) {
		return generateToken(authentication, refreshTokenExpirationMs);
	}
	public String generateToken(final Authentication authentication, int expirationMs) {
		final UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return Jwts.builder()
			.setSubject(userPrincipal.getEmail())
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + expirationMs))
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact();
	}

	public User getUserFromToken(String token) {
		return userService.findByEmail(parseAndGetEmailFromToken(token));
	}
	public String getEmailFromJwtToken(final String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	private String parseAndGetEmailFromToken(final String authHeader) {
		return getEmailFromJwtToken(authHeader.substring(7));
	}

	public boolean validateJwtToken(final String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (final MalformedJwtException e) {
			System.out.println("Invalid JWT token: " + e.getMessage());
		} catch (final ExpiredJwtException e) {
			System.out.println("JWT token is expired: " + e.getMessage());
		} catch (final UnsupportedJwtException e) {
			System.out.println("JWT token is unsupported: " + e.getMessage());
		} catch (final IllegalArgumentException e) {
			System.out.println("JWT claims string is empty: " + e.getMessage());
		}
		return false;
	}
}
