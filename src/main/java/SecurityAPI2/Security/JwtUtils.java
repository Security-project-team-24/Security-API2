package SecurityAPI2.Security;

import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
	private final String jwtSecret = "SecuritySecret";
	private final int accessTokenExpirationMs = 1000 * 60 * 15; //15 min
	private final int refreshTokenExpirationMs = 1000 * 60 * 30; //2 sata
	private final int loginTokenExpirationMs = 1000 * 60 * 10; //10 min

	public String generateAccessToken(String subject) {
		return generateToken(subject, accessTokenExpirationMs);
	}
	public String generateRefreshToken(String subject) {
		return generateToken(subject, refreshTokenExpirationMs);
	}

	public String generateLoginToken(UUID uuid, String email) {
		return Jwts.builder()
				.setSubject(email)
				.claim("uuid", uuid)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + loginTokenExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String generateToken(String email, int expirationMs) {
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + expirationMs))
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact();
	}

	public String getEmailFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	public String getUuidFromJwtToken(String token) {
		return (String)Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("uuid");
	}

	public boolean validateJwtToken(final String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (final MalformedJwtException e) {
			System.out.println("Invalid JWT token: " + e.getMessage());
		} catch (final ExpiredJwtException e) {
			System.out.println("Token expired " + e.getMessage());
		} catch (final UnsupportedJwtException e) {
			System.out.println("Unsupported token " + e.getMessage());
		} catch (final IllegalArgumentException e) {
			System.out.println("JWT claims string is empty: " + e.getMessage());
		}
		return false;
	}
}
