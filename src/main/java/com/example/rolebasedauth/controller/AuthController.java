package com.example.rolebasedauth.controller;

import com.example.rolebasedauth.model.RefreshToken;
import com.example.rolebasedauth.model.User;
import com.example.rolebasedauth.payload.JwtResponse;
import com.example.rolebasedauth.payload.RefreshTokenResponse;
import com.example.rolebasedauth.security.jwt.JwtUtils;
import com.example.rolebasedauth.security.jwt.RefreshTokenService;
import com.example.rolebasedauth.service.MyUserDetails;
import com.example.rolebasedauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect credentials!");
        }

        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        final String jwt = jwtUtils.generateToken(myUserDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(myUserDetails.getId());

        List<String> roles = myUserDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse("Bearer ", jwt, refreshToken.getRefreshToken(), myUserDetails.getId(), myUserDetails.getFullname(), myUserDetails.getUsername(), roles));
    }

    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User regUser = userService.findUserByEmail(user.getEmail());
        if (regUser != null) {
            return ResponseEntity.badRequest().body("User already exists!");
        }

        regUser = userService.saveUser(user);
        return ResponseEntity.ok().body(regUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, Long> userid) {
        refreshTokenService.deleteByUserId(userid.get("id"));
        return ResponseEntity.ok().body("User logged out");
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody Map<String, String> refreshToken) {
        RefreshToken token = refreshTokenService.findByRefreshToken(refreshToken.get("token"));
        if (token != null && refreshTokenService.verifyExpiration(token) != null) {
            User user = token.getUser();
            Map<String, Object> claims = new HashMap<>();
            claims.put("ROLES", user.getRoles().stream().map(item -> item.getRole()).collect(Collectors.toList()));
            String jwt = jwtUtils.createToken(claims, user.getEmail());
            return ResponseEntity.ok(new RefreshTokenResponse("Bearer", jwt, refreshToken.get("token")));
        }

        return ResponseEntity.badRequest().body("Refresh token expired!");
    }
}
