package com.smuraha.service.impl;

import com.smuraha.dao.AppUserDao;
import com.smuraha.entity.AppUser;
import com.smuraha.service.UserActivationService;
import com.smuraha.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;

    @Override
    public boolean activate(String cryptoUserId) {
        Long id = cryptoTool.idOf(cryptoUserId);
        Optional<AppUser> userOptional = appUserDao.findById(id);
        if(userOptional.isPresent()){
            AppUser user = userOptional.get();
            user.setIsActive(true);
            appUserDao.save(user);
            return true;
        }
        return false;
    }
}
