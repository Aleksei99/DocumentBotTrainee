package com.smuraha.util;

import org.springframework.beans.factory.annotation.Value;

public class Creds {
    @Value(value = "${bot.token}")
    private String botToken;

    public static String getBotToken(){
        return new Creds().botToken;
    }
}
