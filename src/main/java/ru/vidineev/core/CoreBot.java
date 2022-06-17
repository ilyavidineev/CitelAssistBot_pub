package ru.vidineev.core;

import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vidineev.config.BotConfig;
import ru.vidineev.entities.RecordString;
import ru.vidineev.service.CSVFileService;
import ru.vidineev.service.SendMessageOperationService;

import java.util.List;
import java.util.Objects;

import static ru.vidineev.constants.Constants.*;

@Component
public class CoreBot extends TelegramLongPollingBot {

    private static final Logger log = Logger.getLogger(CoreBot.class);
    private final BotConfig config;
    private final SendMessageOperationService sendMessageService;
    private final CSVFileService csvFileService;
    private List<RecordString> records;

    @SneakyThrows
    @Autowired
    public CoreBot(SendMessageOperationService sendMessageService, CSVFileService csvFileService, BotConfig config) {
        this.sendMessageService = sendMessageService;
        this.csvFileService = csvFileService;
        this.config = config;
        log.info("Application: Starting. Main construction done");
    }

    @SneakyThrows
    public void onUpdateReceived(Update update) {

        if (records == null) {
            log.info("Application: Records in memory is empty. Trying to access to datafiles");
            records = csvFileService.ReadCSVData();
            log.info("Application: Data from CSV-file was read successfully");
        }

        String msgData = getDataFromUpdate(update);

        if (msgData != null && !msgData.isBlank()) {

            for (RecordString rec : records) {
                if (Objects.equals(rec.getNumber(), msgData)) {
                    switch (rec.getMethod()) {
                        case CSV_MESSAGE_METHOD_EDIT -> execute(
                                sendMessageService
                                        .editMessage(update, rec.getText(), rec.getExitNums()));
                        case CSV_MESSAGE_METHOD_CREATE -> execute(
                                sendMessageService
                                        .createNewMessage(update, rec.getText(), rec.getExitNums()));
                    }
                }
            }
        }
    }

    private String getDataFromUpdate(Update update) {
        String msgData = "";

        if (update.hasCallbackQuery()) {
            msgData = update.getCallbackQuery().getData();
            log.info("ChatId:" + update.getCallbackQuery().getMessage().getChatId() + ", username: "
                    + update.getCallbackQuery().getMessage().getChat().getUserName()
                    + ". Received message with CallbackQuery-data: " + msgData);
        } else if (update.hasMessage() || update.getMessage().getText().startsWith("/")) {
            msgData = update.getMessage().getText();
            log.info("ChatId:" + update.getMessage().getChatId() + ", username: "
                    + update.getMessage().getChat().getUserName()
                    + ". Received message with command: " + msgData);
        }
        return msgData;
    }

    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }
}