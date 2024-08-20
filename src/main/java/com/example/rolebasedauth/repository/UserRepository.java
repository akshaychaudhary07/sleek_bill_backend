package com.example.rolebasedauth.repository;

import com.example.rolebasedauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmailIgnoreCase(String email);

    User findByEmailIgnoreCase(String email);


}