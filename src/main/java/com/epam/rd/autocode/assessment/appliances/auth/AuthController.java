package com.epam.rd.autocode.assessment.appliances.auth;

import com.epam.rd.autocode.assessment.appliances.auth.dto.*;
import com.epam.rd.autocode.assessment.appliances.exception.UserNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.User;
import com.epam.rd.autocode.assessment.appliances.repository.UserRepository;
import com.epam.rd.autocode.assessment.appliances.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

//    private final AuthenticationManager authenticationManager;
//    private final JwtTokenProvider tokenProvider;
//    private final UserRepository userRepository;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getUsername(),
//                        loginRequest.getPassword()
//                )
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String jwt = tokenProvider.generateToken(authentication);
//        String refreshToken = tokenProvider.generateRefreshToken(authentication);
//
//        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, refreshToken));
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
//        String requestRefreshToken = request.getRefreshToken();
//
//        if (!tokenProvider.validateToken(requestRefreshToken)) {
//            throw new BadCredentialsException("Invalid refresh token");
//        }
//
//        Long userId = tokenProvider.getUserIdFromJWT(requestRefreshToken);
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//
//        UserPrincipal userPrincipal = UserPrincipal.create(user);
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
//
//        String newToken = tokenProvider.generateToken(authentication);
//        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);
//
//        return ResponseEntity.ok(new JwtAuthenticationResponse(newToken, newRefreshToken));
//    }
}