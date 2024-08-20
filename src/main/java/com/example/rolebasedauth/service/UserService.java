package com.example.rolebasedauth.service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.rolebasedauth.model.Role;
import com.example.rolebasedauth.model.User;
import com.example.rolebasedauth.repository.RefreshTokenRepository;
import com.example.rolebasedauth.repository.RoleRepository;
import com.example.rolebasedauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    @Autowired
    @Lazy
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public User findUserByEmail(String email) {  // Changed method name
        return userRepository.findByEmailIgnoreCase(email.toLowerCase());  // Changed from username to email
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        Role userRole = roleRepository.findByRole("ROLE_USER");
        user.setRoles(Arrays.asList(userRole));

        return userRepository.save(user);
    }

    public User saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        Role adminRole = roleRepository.findByRole("ROLE_ADMIN");
        user.setRoles(Arrays.asList(adminRole));

        return userRepository.save(user);
    }

    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getRole()));
    }

    // Method to get all non-admin users
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !isAdmin(user))
                .collect(Collectors.toList());
    }

    public List<User> getAllAdmins() {
        Role adminRole = roleRepository.findByRole("ROLE_ADMIN");

        if (adminRole != null) {
            System.out.println("hey");
            // Fetch all users and filter those who have the ROLE_ADMIN role
            return userRepository.findAll().stream()
                    .filter(user -> isAdmin(user))
                    .collect(Collectors.toList());
        }

        return List.of(); // Return an empty list if the admin role is not found
    }

    public void deleteUserById(Long id) {
        User user = findUserById(id);

        if (user != null && isAdmin(user)) {
            throw new IllegalArgumentException("Cannot delete an admin.");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteAdminById(Long id) {
        User user = findUserById(id);

        if (user == null) {
            throw new IllegalArgumentException("Admin not found.");
        }

        if (!isAdmin(user)) {
            throw new IllegalArgumentException("The specified user is not an admin.");
        }
        refreshTokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }


}