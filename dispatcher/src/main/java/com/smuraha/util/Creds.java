package com.smuraha.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Creds {
    @Value(value = "${bot.token}")
    private String botToken;
}
