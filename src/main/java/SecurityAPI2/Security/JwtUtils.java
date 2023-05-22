package SecurityAPI2.Security;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
	private final String authSecret = Dotenv.load().get("AUTH_SECRET");
	private final String loginSecret = Dotenv.load().get("LOGIN_SECRET");
	private final String registerSecret = Dotenv.load().get("REGISTER_SECRET");
	private final int accessTokenExpirationMs = 1000 * 60 * 15; //15 min
	private final int refreshTokenExpirationMs = 1000 * 60 * 30; //2 sata
	private final int loginTokenExpirationMs = 1000 * 60 * 10; //10 min


	public String generateAccessToken(String subject) {
		return generateAuthToken(subject, accessTokenExpirationMs);
	}
	public String generateRefreshToken(String subject) {
		return generateAuthToken(subject, refreshTokenExpirationMs);
	}
	public String generateLoginToken(String subject, UUID uuid) {return generateLoginJwtToken(subject, refreshTokenExpirationMs,uuid);}
	public String generateRegisterToken(String subject,UUID uuid) {return generateRegisterJwtToken(subject,uuid);}

	public String generateAuthToken(String email, int expirationMs) {
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + expirationMs))
			.signWith(SignatureAlgorithm.HS512, authSecret)
			.compact();
	}
	public String generateLoginJwtToken(String email, int expirationMs,UUID uuid) {
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + expirationMs))
				.claim("uuid", uuid)
				.signWith(SignatureAlgorithm.HS512, loginSecret)
				.compact();
	}
	public String generateRegisterJwtToken(String email,UUID uuid) {
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(dt)
				.claim("uuid", uuid)
				.signWith(SignatureAlgorithm.HS512, registerSecret)
				.compact();
	}

	public String getEmailFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(authSecret).parseClaimsJws(token).getBody().getSubject();
	}
	public Claims getClaimsFromLoginToken(String token) {
		return Jwts.parser().setSigningKey(loginSecret).parseClaimsJws(token).getBody();
	}
	public Claims getClaimsFromRegisterToken(String token) {
		return Jwts.parser().setSigningKey(registerSecret).parseClaimsJws(token).getBody();
	}

	public boolean validateJwtToken(final String authToken) {
		return validateToken(authToken, authSecret);
	}
	public boolean validateLoginToken(final String loginToken) {
		return validateToken(loginToken, loginSecret);
	}
	public boolean validateRegisterToken(final String registerToken) {
		return validateToken(registerToken, registerSecret);
	}

	private boolean validateToken(String token, String secret) {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
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
