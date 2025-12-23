package fs16.webide.web_ide_for.clerk;

import com.fasterxml.jackson.databind.ObjectMapper;
import fs16.webide.web_ide_for.clerk.error.JwtErrorCode;
import fs16.webide.web_ide_for.common.error.CoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Component
@Slf4j
public class ClerkPublicKeyProvider {

    @Value("${clerk.jwks-url}")
    private String jwksUrl;

    private Map<String, PublicKey> keyCache = new HashMap<>();

    public PublicKey getPublicKey(String kid) {
        if (keyCache.containsKey(kid)) {
            return keyCache.get(kid);
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(jwksUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jwks = mapper.readValue(response, Map.class);
            List<Map<String, Object>> keys = (List<Map<String, Object>>) jwks.get("keys");

            for (Map<String, Object> key : keys) {
                if (kid.equals(key.get("kid"))) {
                    PublicKey publicKey = buildPublicKey(key);
                    keyCache.put(kid, publicKey);
                    return publicKey;
                }
            }

            throw new CoreException(JwtErrorCode.PUBLIC_KEY_NOT_FOUND);

        } catch (Exception e) {
            throw new CoreException(JwtErrorCode.JWK_FETCH_FAILED);
        }
    }

    private PublicKey buildPublicKey(Map<String, Object> key) throws Exception {
        String n = (String) key.get("n");
        String e = (String) key.get("e");

        byte[] nBytes = Base64.getUrlDecoder().decode(n);
        byte[] eBytes = Base64.getUrlDecoder().decode(e);

        BigInteger modulus = new BigInteger(1, nBytes);
        BigInteger exponent = new BigInteger(1, eBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return factory.generatePublic(spec);
    }
}