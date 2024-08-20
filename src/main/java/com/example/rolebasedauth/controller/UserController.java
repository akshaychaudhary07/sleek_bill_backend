package com.example.rolebasedauth.controller;

import com.example.rolebasedauth.model.User;
import com.example.rolebasedauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String welcome() {
        return "Welcome to user controller";
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userService.findUserByEmail(currentUserEmail);

        User user = userService.findUserById(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.ok(user);
        }

        if (userService.isAdmin(currentUser) && !userService.isAdmin(user)) {
            return ResponseEntity.ok(user);
        }

        throw new AccessDeniedException("Access Denied: You can only access your own profile or view other users' profiles if you are an admin.");
    }
}
