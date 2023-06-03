package com.smuraha.service.impl;

import com.smuraha.dao.AppUserDao;
import com.smuraha.dao.RawDataDao;
import com.smuraha.entity.AppDocument;
import com.smuraha.entity.AppPhoto;
import com.smuraha.entity.AppUser;
import com.smuraha.entity.RawData;
import com.smuraha.entity.enums.UserState;
import com.smuraha.exceptions.UploadFileException;
import com.smuraha.service.FileService;
import com.smuraha.service.MainService;
import com.smuraha.service.ProducerService;
import com.smuraha.service.enums.ServiceCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.smuraha.entity.enums.UserState.BASIC_STATE;
import static com.smuraha.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.smuraha.service.enums.ServiceCommands.*;

@Service
@Log4j
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final FileService fileService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        Message message = update.getMessage();
        AppUser appUser = findOrSaveAppUser(update);
        UserState userState = appUser.getUserState();
        String text = message.getText();
        String output = "";

        ServiceCommands command = fromValue(text);
        if (CANCEL.equals(command)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO добавить обработку
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        Long chatId = message.getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        //TODO доделать
        try {
            AppDocument doc = fileService.processDoc(message);
            String answer = "Документ успешно загружен! Ссылка для скачивания http://test.com/download/123";
            sendAnswer(answer, chatId);
        }catch (UploadFileException e){
            log.error(e);
            String error = "Загрузка не удалась! Попробуйте позже.";
            sendAnswer(error,chatId);
        }

    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {
        UserState userState = appUser.getUserState();
        if (!appUser.getIsActive()) {
            String error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            String error = "Отмените текущую команду с помощбю /cancel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            //TODO доделать
            String answer = "Фото успешно загружено! Ссылка для скачивания http://test.com/download/456";
            sendAnswer(answer, chatId);
        }catch (UploadFileException e){
            log.error(e);
            String error = "Загрузка фото не удалась! Попробуйте позже.";
            sendAnswer(error,chatId);
        }
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Команда отменена!";
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        ServiceCommands command = fromValue(cmd);
        if (REGISTRATION.equals(command)) {
            //TODO добавить рег-цию
            return "Временно не доступно!";
        } else if (HELP.equals(command)) {
            return help();
        } else if (START.equals(command)) {
            return "Приветствую! Чтобы просмотреть список команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы просмотреть список команд введите /help";
        }
    }

    private String help() {
        return """
                Список доступных команд:
                    /cancel - отмена выполнения текущей команды
                    /registration - регистрация пользователя.
                """;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDao.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(true)
                    .userState(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }
}
