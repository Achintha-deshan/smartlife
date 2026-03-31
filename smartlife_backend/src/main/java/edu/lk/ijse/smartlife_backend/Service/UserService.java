package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.AuthDTO;
import edu.lk.ijse.smartlife_backend.DTO.AuthResponseDTO;
import edu.lk.ijse.smartlife_backend.DTO.RegisterDTO;
import edu.lk.ijse.smartlife_backend.Entity.Member;
import edu.lk.ijse.smartlife_backend.Entity.Role;
import edu.lk.ijse.smartlife_backend.Entity.User;
import edu.lk.ijse.smartlife_backend.Repository.MemberRepository;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import edu.lk.ijse.smartlife_backend.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationProvider authenticationProvider;

    public String saveUser(RegisterDTO registerDTO, String roleStr) {
        String lastId = userRepository.findLastUserId();
        String nextID;

        if (lastId == null) {
            nextID = "U001";
        } else {
            int idNum = Integer.parseInt(lastId.substring(1));
            nextID = String.format("U%03d", idNum + 1);
        }

        User user = User.builder()
                .userId(nextID)
                .fullName(registerDTO.getFullName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(Role.valueOf(registerDTO.getRole().toUpperCase()))
                .build();

        userRepository.save(user);

        return "User registered successfully as " + roleStr + " with ID: " + nextID;
    }

    public AuthResponseDTO authenticate(AuthDTO authDTO) {
        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authDTO.getEmail(),
                        authDTO.getPassword()
                )
        );

        Optional<Member> memberOpt = memberRepository.findByEmail(authDTO.getEmail());
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            String token = jwtUtil.generateToken(member.getEmail());
            return new AuthResponseDTO(
                    token,
                    "MEMBER",
                    member.getMemberId(),
                    member.getAdmin().getUserId()
            );
        }

        Optional<User> userOpt = userRepository.findByEmail(authDTO.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getEmail());
            return new AuthResponseDTO(
                    token,
                    "ADMIN",
                    user.getUserId(),
                    null
            );
        }

        throw new UsernameNotFoundException("User not found: " + authDTO.getEmail());
    }}