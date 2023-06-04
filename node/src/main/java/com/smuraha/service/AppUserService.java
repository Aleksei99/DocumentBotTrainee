package com.smuraha.service;

import com.smuraha.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail(AppUser appUser,String email);
}
