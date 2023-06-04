package com.smuraha.service.impl;

import com.smuraha.dao.AppUserDao;
import com.smuraha.dto.MailParams;
import com.smuraha.entity.AppUser;
import com.smuraha.entity.enums.UserState;
import com.smuraha.service.AppUserService;
import com.smuraha.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return """
                    Вам на почту уже было отправлено письмо.
                    Перейдите по ссылке для подтверждения регистрации.
                    """;
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDao.save(appUser);
        return "Введите ваш email";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (AddressException e) {
            return "Введите корректный email. Для отмены команды введите /cancel";
        }
        Optional<AppUser> optional = appUserDao.findByEmail(email);
        if (optional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setUserState(UserState.BASIC_STATE);
            appUser = appUserDao.save(appUser);

            String hashedId = cryptoTool.hashOf(appUser.getId());
            ResponseEntity<String> response = sendRequestToMailService(hashedId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                String message = String.format("Отправка письма на почту %s не удалась!", email);
                log.error(message);
                appUser.setEmail(null);
                appUserDao.save(appUser);
                return message;
            }
            return """
                    Вам на почту было отправлено письмо.
                    Перейдите по ссылке для подтверждения регистрации.
                    """;
        } else {
            return """
                    Этот email уже используется! Введите /cancel
                    """;
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String hashedId, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        MailParams params = MailParams.builder()
                .id(hashedId)
                .emailTo(email)
                .build();
        HttpEntity<MailParams> httpEntity = new HttpEntity<>(params, httpHeaders);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                httpEntity,
                String.class);
    }
}
