package com.smuraha.dao;

import com.smuraha.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDao extends JpaRepository<AppUser,Long> {
    AppUser findAppUserByTelegramUserId(Long userId);
}
