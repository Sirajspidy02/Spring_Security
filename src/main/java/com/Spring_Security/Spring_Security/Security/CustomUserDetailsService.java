//package com.Spring_Security.Spring_Security.Security;
//
//
////import com.Spring_Security.Spring_Security.entity.User;
////import com.Spring_Security.Spring_Security.repository.UserRepository;
////import lombok.RequiredArgsConstructor;
////import org.springframework.security.core.authority.SimpleGrantedAuthority;
////import org.springframework.security.core.userdetails.UserDetails;
////import org.springframework.security.core.userdetails.UserDetailsService;
////import org.springframework.security.core.userdetails.UsernameNotFoundException;
////import org.springframework.stereotype.Service;
////
////import java.util.Collections;
////
////@Service
////@RequiredArgsConstructor
////public class CustomUserDetailsService implements UserDetailsService {
////
////    private final UserRepository userRepository;
////
////    @Override
////    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
////        User user = userRepository.findByEmail(email)
////                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
////
////        return org.springframework.security.core.userdetails.User.builder()
////                .username(user.getEmail())
////                .password(user.getPassword())
////                .authorities(Collections.singletonList(
////                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
////                ))
////                .accountExpired(false)
////                .accountLocked(!user.isAccountNonLocked())
////                .credentialsExpired(false)
////                .disabled(!user.isEnabled())
////                .build();
////    }
////}
//
//package com.Spring_Security.Spring_Security.Security;
//
//import com.Spring_Security.Spring_Security.entity.User;
//import com.Spring_Security.Spring_Security.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository repository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = repository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getEmail())
//                .password(user.getPassword())
//                .disabled(!user.isEnabled())
//                .accountLocked(user.isLocked())
//                .authorities("ROLE_" + user.getRole().name())
//                .build();
//    }
//}
//

package com.Spring_Security.Spring_Security.Security;

import com.Spring_Security.Spring_Security.entity.User;
import com.Spring_Security.Spring_Security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .accountLocked(!user.isAccountNonLocked())   // FIXED HERE
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}

