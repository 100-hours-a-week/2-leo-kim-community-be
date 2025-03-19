package org.community.util.jwtutil;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.community.common.user.UserResponseMessage;
import org.community.global.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Component
public class JwtUtil {

    private final String secretKey;
    private final long ACCESS_EXPIRATION_TIME;
    private final long REFRESH_EXPIRATION_TIME;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.accesstoken-validity-in-seconds}") long accessTokenValidity,
            @Value("${jwt.refreshtoken-validity-in-seconds}") long refreshTokenValidity
    ) {
        this.secretKey = secretKey;
        this.ACCESS_EXPIRATION_TIME = accessTokenValidity;
        this.REFRESH_EXPIRATION_TIME = refreshTokenValidity;
    }

    public SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String getEmailFromJwt(String jwt) {
        return getClaims(jwt).get("email", String.class);
    }

    public Long getUserIdFromJwt(String jwt) {
        String newJwt = jwt.substring(7);
        return getClaims(newJwt).get("userId", Long.class);
    }

    public Boolean isExpired(String jwt) {
        return getClaims(jwt).getExpiration().before(new Date());
    }

    /**
     * JWT 토큰 생성
     */
    public TokenInfo createToken(String email, Long userId) {
        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + ACCESS_EXPIRATION_TIME);
        Date refreshExpiration = new Date(now.getTime() + REFRESH_EXPIRATION_TIME);

        String access = Jwts.builder()     // header 자동 포함
                .claim("email", email)            // 여기부터 payload
                .claim("userId", userId)
//                .claim("Role", role)  // Role이 지금은 없다.
                .expiration(accessExpiration)
                .notBefore(now)
                .issuedAt(now)
                .signWith(getKey(), Jwts.SIG.HS256)        // signature
                .compact();

        String refresh = Jwts.builder()
                .expiration(refreshExpiration)
                .issuedAt(now)
                .signWith(getKey(),Jwts.SIG.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    /**
     * JWT 토큰 정보 추출
     */
    public Authentication getAuthentication(String jwt) {
        Claims claims = getClaims(jwt);

        Long userId = Optional.ofNullable(claims.get("userId", Long.class))
                .orElseThrow(() -> new RuntimeException("잘못된 토큰입니다."));

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails principal = new CustomUser(userId, claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰 검증
     *
     * @return
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(UserResponseMessage.JWT_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new CustomException(UserResponseMessage.JWT_INVALID);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(UserResponseMessage.JWT_UNSUPPORTED);
        } catch (IllegalArgumentException | SecurityException e) {
            throw new CustomException(UserResponseMessage.JWT_VERIFICATION_FAILED);
        }
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}