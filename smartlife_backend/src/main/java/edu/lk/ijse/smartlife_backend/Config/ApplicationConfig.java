package edu.lk.ijse.smartlife_backend.Config;

import edu.lk.ijse.smartlife_backend.Repository.MemberRepository;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var adminOpt = userRepository.findByEmail(username);
            if (adminOpt.isPresent()) {
                var admin = adminOpt.get();
                return new User(
                        admin.getEmail(),
                        admin.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()))
                );
            }

            var memberOpt = memberRepository.findByEmail(username);
            if (memberOpt.isPresent()) {
                var member = memberOpt.get();
                return new User(
                        member.getEmail(),
                        member.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
                );
            }

            throw new UsernameNotFoundException("User or Member not found with email: " + username);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}