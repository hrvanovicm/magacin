package fyi.hrvanovicm.magacin.infrastructure.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import fyi.hrvanovicm.magacin.domain.account.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthorisationFilter extends OncePerRequestFilter {
    private JWTAuthService jwtAuthService;

    @Autowired
    UserService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeaderValue = request.getParameter("Authorization");

        if(authHeaderValue == null || !authHeaderValue.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String providedToken = authHeaderValue.substring(7);

        try {
            DecodedJWT decodedJWT = jwtAuthService.decode(providedToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(decodedJWT.getSubject());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exception) {
            // Do nothing
        }

        filterChain.doFilter(request, response);
    }

    @Autowired
    public void setJWTService(JWTAuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }
}
