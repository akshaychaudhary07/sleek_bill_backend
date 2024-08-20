package com.example.rolebasedauth.controller;

import com.example.rolebasedauth.model.User;
import com.example.rolebasedauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    // Constructor injection is preferred
    public AdminController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to admin controller";
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminById(@PathVariable Long adminId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentAdminEmail = authentication.getName();
        User currentAdmin = userService.findUserByEmail(currentAdminEmail);

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        if (!currentAdmin.getId().equals(adminId)) {
            throw new AccessDeniedException("Access Denied: You can only access your own data.");
        }

        User admin = userService.findUserById(adminId);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(admin);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = userService.findUserByEmail(authentication.getName());

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        User requestedUser = userService.findUserById(userId);
        if (requestedUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Prevent admins from fetching details of other admins
        if (userService.isAdmin(requestedUser)) {
            throw new AccessDeniedException("Access Denied: You cannot access details of another admin.");
        }

        return ResponseEntity.ok(requestedUser);
    }



    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = userService.findUserByEmail(authentication.getName());

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = userService.findUserByEmail(authentication.getName());

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        User existingUser = userService.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("User already exists!");
        }

        User newUser = userService.saveUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = userService.findUserByEmail(authentication.getName());

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        User existingUser = userService.findUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Update fields excluding password
        existingUser.setFullname(userDetails.getFullname());
        existingUser.setEmail(userDetails.getEmail());

        User updatedUser = userService.saveUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

//    @PutMapping("/user/{userId}/change-password")
//    public ResponseEntity<?> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> passwordDetails) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentAdmin = userService.findUserByEmail(authentication.getName());
//
//        if (!userService.isAdmin(currentAdmin)) {
//            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
//        }
//
//        User existingUser = userService.findUserById(userId);
//        if (existingUser == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // Get the new password from the request
//        String newPassword = passwordDetails.get("password");
//
//        if (newPassword == null || newPassword.isEmpty()) {
//            return ResponseEntity.badRequest().body("Password cannot be empty");
//        }
//
//        existingUser.setPassword(passwordEncoder.encode(newPassword));
//        userService.saveUser(existingUser);
//
//        return ResponseEntity.ok("Password updated successfully");
//    }





    @PatchMapping("/user/{userId}")
    public ResponseEntity<?> patchUser(@PathVariable Long userId, @RequestBody User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = userService.findUserByEmail(authentication.getName());

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        User existingUser = userService.findUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (userDetails.getFullname() != null) {
            existingUser.setFullname(userDetails.getFullname());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }

        User updatedUser = userService.saveUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = userService.findUserByEmail(authentication.getName());

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        User user = userService.findUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUserById(userId);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<?> registerAdmin(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = userService.findUserByEmail(authentication.getName());

        if (!userService.isAdmin(currentAdmin)) {
            throw new AccessDeniedException("Access Denied: You do not have admin privileges.");
        }

        User regUser = userService.findUserByEmail(user.getEmail());
        if (regUser != null) {
            return ResponseEntity.badRequest().body("User already exists!");
        }

        regUser = userService.saveAdmin(user);
        return ResponseEntity.ok().body(regUser);
    }
}
