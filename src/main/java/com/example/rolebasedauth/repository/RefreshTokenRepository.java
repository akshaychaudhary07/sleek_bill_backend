package com.example.rolebasedauth.repository;

import com.example.rolebasedauth.model.RefreshToken;
import com.example.rolebasedauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByRefreshToken(String token);
    RefreshToken findByUserId(Long userId);

    @Modifying
    int deleteByUser(User user);

    @Modifying
    @Transactional
    int deleteByUserId(Long userId);

}