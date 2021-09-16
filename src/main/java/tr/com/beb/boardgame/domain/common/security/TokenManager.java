package tr.com.beb.boardgame.domain.common.security;

import java.security.Key;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import tr.com.beb.boardgame.configuration.ApplicationProperties;
import tr.com.beb.boardgame.domain.model.user.UserId;

@Component
public class TokenManager {

    private Key secretKey;

    public TokenManager(ApplicationProperties applicationProperties) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(applicationProperties.getTokenSecretKey()));
    }

    public String generateJWT(UserId userId) {
        return Jwts.builder().setSubject(userId.getValue()).signWith(secretKey).compact();
    }

    public UserId parseJWT(String jwt) {
        String userIdFromToken = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody().getSubject();
        return new UserId(userIdFromToken);
    }

}
