package com.smuraha.dao;

import com.smuraha.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNullApi;

import java.util.Optional;

public interface AppUserDao extends JpaRepository<AppUser,Long> {
    Optional<AppUser> findByTelegramUserId(Long userId);
    Optional<AppUser> findByEmail(String email);
}
