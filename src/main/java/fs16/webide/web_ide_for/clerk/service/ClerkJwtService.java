package fs16.webide.web_ide_for.clerk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fs16.webide.web_ide_for.clerk.ClerkPublicKeyProvider;
import fs16.webide.web_ide_for.clerk.error.JwtErrorCode;
import fs16.webide.web_ide_for.common.error.CoreException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class ClerkJwtService {

    private final ClerkPublicKeyProvider clerkPublicKeyProvider;

    @Value("${clerk.issuer}")
    private String issuer;

    public ClerkJwtService(ClerkPublicKeyProvider clerkPublicKeyProvider){
        this.clerkPublicKeyProvider = clerkPublicKeyProvider;
    }

    public Map<String, Object> validate(String token){
        try {
            // 1. JWT에서 kid 추출
            String kid = getKidFromToken(token);

            // 2. kid로 Public Key 가져오기
            PublicKey publicKey = clerkPublicKeyProvider.getPublicKey(kid);

            // 3. JWT 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            ObjectMapper mapper = new ObjectMapper();
            return new ObjectMapper().convertValue(claims, Map.class);

        } catch (Exception e){
            throw new CoreException(JwtErrorCode.JWT_VALIDATION_FAILED,e);

        }
    }

    private String getKidFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String header = new String(Base64.getUrlDecoder().decode(parts[0]));

            return   (String) new ObjectMapper().readValue(header, Map.class).get("kid");
        } catch (Exception e) {
            throw new CoreException(JwtErrorCode.JWT_KID_FAILED,e);
        }
    }
}