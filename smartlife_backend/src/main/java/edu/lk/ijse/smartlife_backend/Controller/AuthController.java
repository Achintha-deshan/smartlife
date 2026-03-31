package edu.lk.ijse.smartlife_backend.Controller;

import edu.lk.ijse.smartlife_backend.DTO.APIResponse;
import edu.lk.ijse.smartlife_backend.DTO.AuthDTO;
import edu.lk.ijse.smartlife_backend.DTO.RegisterDTO;
import edu.lk.ijse.smartlife_backend.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<String>> signupAdmin(@Valid @RequestBody RegisterDTO registerDTO) {
        String result = userService.saveUser(registerDTO, "ADMIN");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "User registered successfully", result));
    }

    @PostMapping("/signin")
    public ResponseEntity<APIResponse<Object>> loginUser(@Valid @RequestBody AuthDTO authDTO) {
        Object authData = userService.authenticate(authDTO);
        return ResponseEntity.ok(new APIResponse<>(200, "Login successful", authData));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-member")
    public ResponseEntity<APIResponse<String>> addFamilyMember(@Valid @RequestBody RegisterDTO registerDTO) {
        String result = userService.saveUser(registerDTO, "MEMBER");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Family member added successfully", result));
    }
}