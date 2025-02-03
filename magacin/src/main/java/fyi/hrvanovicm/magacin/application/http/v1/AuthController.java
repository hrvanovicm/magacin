package fyi.hrvanovicm.magacin.application.http.v1;

import fyi.hrvanovicm.magacin.infrastructure.security.JWTAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private JWTAuthService jwtAuthService;

    @GetMapping("register")
    public String generateToken() throws CertificateException, NoSuchAlgorithmException, InvalidKeySpecException {
        return jwtAuthService.generate();
    }

    @GetMapping("login")
    public String getToken(
            @RequestParam String token
    ) throws CertificateException, NoSuchAlgorithmException, InvalidKeySpecException {
        return jwtAuthService.decode(token).getClaim("test").asString();
    }
}
