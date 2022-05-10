package ru.vidineev.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:bot.properties")
public class BotConfig {

    @Value("${botUsername}")
    String botUserName;

    @Value("${botToken}")
    String token;
}