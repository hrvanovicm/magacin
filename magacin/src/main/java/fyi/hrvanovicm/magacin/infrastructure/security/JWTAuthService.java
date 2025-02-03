package fyi.hrvanovicm.magacin.infrastructure.security;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

@Component
public class JWTAuthService {
    @Value("${app.auth.jwt.secret-key}")
    private String privateKey;

    public String generate() {
        Algorithm algo = Algorithm.HMAC256(getSecretKey().getEncoded());

        return JWT.create()
                .withSubject("mirza")
                .withClaim("test", "test2")
                .sign(algo);
    }

    public DecodedJWT decode(String token) {
        Algorithm algo = Algorithm.HMAC256(getSecretKey().getEncoded());
        return JWT.require(algo).build().verify(token);
    }

    private SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    @PostConstruct
    private void generateSecurityKeyOnStartup() throws NoSuchAlgorithmException {
        if(privateKey == null || privateKey.isEmpty()) {
            System.out.println("Generating secret auth key!");
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            privateKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println(privateKey);
        }
    }
}

