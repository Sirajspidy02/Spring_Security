//package com.Spring_Security.Spring_Security.Security;
//
//
//
//import com.Spring_Security.Spring_Security.repository.UserRepository;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtProvider jwtProvider;
//    private final UserRepository userRepository;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication)
//            throws IOException, ServletException {
//
//        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//        String email = oauth2User.getAttribute("email");
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        String accessToken = jwtProvider.generateAccessToken(
//                user.getEmail(),
//                user.getRole().name()
//        );
//
//        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
//
//        // Redirect to frontend with tokens
//        String redirectUrl = String.format(
//                "http://localhost:3000/oauth/callback?access=%s&refresh=%s",
//                accessToken, refreshToken
//        );
//
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
//    }
//}

package com.Spring_Security.Spring_Security.Security;

import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        response.sendRedirect("/login-success");
    }
}

