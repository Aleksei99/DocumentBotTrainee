package com.smuraha.configuration;

import com.smuraha.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestServiceConfig {

    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool cryptoTool() {
        return new CryptoTool(salt);
    }

}
