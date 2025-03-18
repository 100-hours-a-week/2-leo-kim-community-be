import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


public class jwtTest {
    @Test
    public void signed_jwt_create_with_registered_fields() {

        String temp = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJLYXJvbCIsInN1YiI6IkF1dGgiLCJhdWQiOiJLYXJvbCIsImV4cCI6MTc0MjE5NTcyOSwibmJmIjoxNzQyMTA5MzI5LCJpYXQiOjE3NDIxMDkzMjksImp0aSI6Ijg1NGQwYWI5LTRlYTItNGFiMy1iZTIwLTlhNzVmZGNmY2I2MiJ9.Y5rHyt_K4CczxvgLuZbI8G266kHTOLfd-g3slLs-srQ";

        // given
        String issuer = "Karol";
        String subject = "Auth";
        String audience = "Karol";
        Date expiredAt = Date.from(Instant.now().plus(Duration.ofDays(1L)));
        Date NotBeforeAt = Date.from(Instant.now());
        Date issuedAt = Date.from(Instant.now());
        String jwtId = UUID.randomUUID().toString();

        // when
        var key = Keys
                .secretKeyFor(SignatureAlgorithm.HS256);

        var jws = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .setAudience(audience)
                .setExpiration(expiredAt)
                .setNotBefore(NotBeforeAt)
                .setIssuedAt(issuedAt)
                .setId(jwtId)
                .signWith(key)
                .compact();

        // peak
        System.out.println(jws); // eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJLYXJvbCIsInN1YiI6IkF1dGgiLCJhdWQiOiJLYXJvbCIsImV4cCI6MTY3NzUwNzMwOSwibmJmIjoxNjc3NDIwOTA5LCJpYXQiOjE2Nzc0MjA5MDksImp0aSI6IjRkNDM2MDMyLWQ1MTMtNDU4YS1iNzI3LTZmNTlhMDA2YTIzZiJ9.acf9tiMDFilAlJntro1QcLkw-KubYJaEGNzHQeqRo5Q


    }
}
