//package com.example.rolebasedauth.controller;
//
//import com.example.rolebasedauth.model.User;
//import com.example.rolebasedauth.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/superadmin")
//public class SuperAdminController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/admin")
//    public ResponseEntity<?> createAdmin(@RequestBody User user) {
//        User existingAdmin = userService.findUserByEmail(user.getEmail());
//
//        if (existingAdmin != null) {
//            return ResponseEntity.badRequest().body("Admin already exists!");
//        }
//
//        User newAdmin = userService.saveAdmin(user);
//        return ResponseEntity.ok(newAdmin);
//    }
//
//    @PutMapping("/admin/{adminId}")
//    public ResponseEntity<?> updateAdmin(@PathVariable Long adminId, @RequestBody User adminDetails) {
//        User existingAdmin = userService.findUserById(adminId);
//
//        if (existingAdmin == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        existingAdmin.setFullname(adminDetails.getFullname());
//        existingAdmin.setEmail(adminDetails.getEmail());
//        // Add more fields as needed, excluding password
//
//        User updatedAdmin = userService.saveUser(existingAdmin);
//        return ResponseEntity.ok(updatedAdmin);
//    }
//
//    @PatchMapping("/admin/{adminId}")
//    public ResponseEntity<?> patchAdmin(@PathVariable Long adminId, @RequestBody User adminDetails) {
//        User existingAdmin = userService.findUserById(adminId);
//
//        if (existingAdmin == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        if (adminDetails.getFullname() != null) {
//            existingAdmin.setFullname(adminDetails.getFullname());
//        }
//        if (adminDetails.getEmail() != null) {
//            existingAdmin.setEmail(adminDetails.getEmail());
//        }
//        // Add more fields as needed, excluding password
//
//        User updatedAdmin = userService.saveUser(existingAdmin);
//        return ResponseEntity.ok(updatedAdmin);
//    }
//
//    @DeleteMapping("/admin/{adminId}")
//    public ResponseEntity<?> deleteAdmin(@PathVariable Long adminId) {
//        User admin = userService.findUserById(adminId);
//        System.out.println("admin  "+admin);
//        if (admin == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        userService.deleteAdminById(adminId);
//        return ResponseEntity.ok().body("Admin deleted successfully");
//    }
//}
