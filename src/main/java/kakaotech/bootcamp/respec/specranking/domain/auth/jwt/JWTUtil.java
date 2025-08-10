package kakaotech.bootcamp.respec.specranking.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JWTUtil {

    private static final String CATEGORY = "category";
    private static final String USER_ID = "userId";
    private static final String LOGIN_ID = "loginId";

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwts(String tokenCategory, Long userId, String loginId, Long expiredMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiredMs);

        log.info("JWT 토큰 생성 - tokenCategory: {}, userId: {}, expiration: {}", tokenCategory, userId, expiration);

        return Jwts.builder()
                .claim(CATEGORY, tokenCategory)
                .claim(USER_ID, userId)
                .claim(LOGIN_ID, loginId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String getTokenCategory(String token) {
        return extractClaim(token, CATEGORY, String.class);
    }

    public Long getUserId(String token) {
        return extractClaim(token, USER_ID, Long.class);
    }

    public String getLoginId(String token) {
        return extractClaim(token, LOGIN_ID, String.class);
    }

    public Boolean isExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private <T> T extractClaim(String token, String claimName, Class<T> claimType) {
        return extractClaims(token).get(claimName, claimType);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
