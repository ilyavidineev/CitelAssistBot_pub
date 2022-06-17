package ru.vidineev.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vidineev.core.CoreBot;

import java.util.ArrayList;
import java.util.List;

import static ru.vidineev.constants.Constants.*;

@Service
public class SendMessageOperationService {

    private static final Logger log = Logger.getLogger(CoreBot.class);

    @Autowired
    public SendMessageOperationService() {
    }

    public SendMessage createNewMessage(Update update, String text, List<String> buttons) {

        // Select where from are we get chatId and userName values
        String chatId;
        String userName;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            userName = update.getCallbackQuery().getMessage().getChat().getUserName();
        }
        else {
            chatId = update.getMessage().getChatId().toString();
            userName = update.getMessage().getChat().getUserName();
        }

        InlineKeyboardMarkup keyboardMarkup = setInlineKeyboard(buttons);

        log.info("ChatId:" + chatId + ", username: " + userName + ". Creating new message.");

        return SendMessage.builder()
                .replyMarkup(keyboardMarkup)
                .chatId(chatId)
                .parseMode(ParseMode.HTML) // For markdown in HTML-mode
                .text(text.translateEscapes())  // For correct view of escaped strings
                .build();
    }

    public EditMessageText editMessage(Update update, String text, List<String> buttons) {

        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        InlineKeyboardMarkup keyboardMarkup = setInlineKeyboard(buttons);

        log.info("ChatId:" + chatId
                + ", username: "
                + update.getCallbackQuery().getMessage().getChat().getUserName() + ". Editing message with id:" + messageId);

        return EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text.translateEscapes())
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboardMarkup)
                .build();
    }

    public InlineKeyboardMarkup setInlineKeyboard(List<String> buttons) {

        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i = i + CSV_ARGS_COLS_QTY_PER_ONE) {

            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();

            String answer = buttons.get(i);
            String answerType = buttons.get(i + 1);
            String answerText = buttons.get(i + 2);

            InlineKeyboardButton button = new InlineKeyboardButton();

            button.setText(answerText);
            if (answerType.equals(CSV_INLINE_BUTTON_TYPE_CALLBACKDATA)) {
                button.setCallbackData(answer);
            } else if (answerType.equals(CSV_INLINE_BUTTON_TYPE_URL)) {
                button.setUrl(answer);
            }

            keyboardRow.add(button);
            keyboardList.add(keyboardRow);
        }
        return InlineKeyboardMarkup.builder().keyboard(keyboardList).build();
    }
}
