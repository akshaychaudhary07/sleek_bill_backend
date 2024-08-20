package com.example.rolebasedauth.service;


import com.example.rolebasedauth.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  // Changed parameter name
        User user = userService.findUserByEmail(email.toLowerCase());  // Changed from username to email

        if (user == null)
            throw new UsernameNotFoundException("User Not Found");

        return MyUserDetails.build(user);
    }

}