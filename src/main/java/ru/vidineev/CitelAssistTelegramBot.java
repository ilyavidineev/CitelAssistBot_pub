package ru.vidineev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CitelAssistTelegramBot {

    public static void main(String[] args) {
        System.out.println("Initializing SpringApplication");
        SpringApplication.run(CitelAssistTelegramBot.class, args);
    }
}
