package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.AuthRequest;
import sv.edu.udb.dto.AuthResponse;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.dto.UserDto;
import sv.edu.udb.exception.BadRequestException;
import sv.edu.udb.model.Role;
import sv.edu.udb.model.User;
import sv.edu.udb.repository.UserRepository;
import sv.edu.udb.service.JwtService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user)));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("El username ya está registrado");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .age(request.getAge())
                .role(Role.ROLE_USER)
                .build();
        return ResponseEntity.ok(new UserDto(userRepository.save(user)));
    }
}
