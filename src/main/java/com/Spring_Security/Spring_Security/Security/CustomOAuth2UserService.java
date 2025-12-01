//package com.Spring_Security.Spring_Security.Security;
//
//
//
//import com.Spring_Security.Spring_Security.entity.User;
//import com.Spring_Security.Spring_Security.enums.Provider;
//import com.Spring_Security.Spring_Security.enums.RoleType;
//import com.Spring_Security.Spring_Security.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest)
//            throws OAuth2AuthenticationException {
//
//        OAuth2User oauth2User = super.loadUser(userRequest);
//
//        String email = oauth2User.getAttribute("email");
//        String name = oauth2User.getAttribute("name");
//        String providerId = userRequest.getClientRegistration().getRegistrationId();
//
//        User user = userRepository.findByEmail(email).orElse(null);
//
//        if (user == null) {
//            user = User.builder()
//                    .email(email)
//                    .fullName(name)
//                    .password("OAUTH2_USER")
//                    .role(RoleType.USER)
//                    .provider(Provider.valueOf(providerId.toUpperCase()))
//                    .enabled(true)
//                    .accountNonLocked(true)
//                    .build();
//
//            userRepository.save(user);
//        }
//
//        return oauth2User;
//    }
//}

package com.Spring_Security.Spring_Security.Security;

import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        return super.loadUser(request);
    }
}

