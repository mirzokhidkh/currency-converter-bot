package uz.mk;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

import static uz.mk.utils.KeyboardButtons.*;

public class Main extends TelegramLongPollingBot {
    public static void main(String[] args) {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new Main());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    @Override
    public String getBotUsername() {
        return "bot_example";
    }

    @Override
    public String getBotToken() {
        return "1782234473:AAGGmvXKqMckr1ykMxSaGVM88Er_C5d7evI";
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();

        if (update.hasCallbackQuery()) {
            EditMessageText editMessageText = new EditMessageText();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = update.getCallbackQuery().getData();

            editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

            editMessageText.setReplyMarkup(inlineKeyboardMarkup);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            if (callbackData.equals("usd")) {
                editMessageText.setText("Choose one of these:");
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton(UZS_USD);
                btn1.setCallbackData("uzs_usd");
                InlineKeyboardButton btn2 = new InlineKeyboardButton(USD_UZS);
                btn2.setCallbackData("usd_uzs");
                row1.add(btn1);
                row1.add(btn2);
                rowList.add(row1);
            } else if (callbackData.equals("rub")) {
                editMessageText.setText("Choose one of these:");
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton(UZS_RUB);
                btn1.setCallbackData("uzs_rub");
                InlineKeyboardButton btn2 = new InlineKeyboardButton(RUB_USZ);
                btn2.setCallbackData("rub_uzs");
                row1.add(btn1);
                row1.add(btn2);
                rowList.add(row1);
            }

            inlineKeyboardMarkup.setKeyboard(rowList);

            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String inputText = update.getMessage().getText();
            sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));

            if (inputText.equals("/start")) {
                sendMessage.setText("Welcome to our currency converter bot. " +
                        "This bot will help you to convert Uzbek Som into another currency.\n" +
                        "Choose one of on the given currency units/");

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton(USD);
                btn1.setCallbackData("usd");
                InlineKeyboardButton btn2 = new InlineKeyboardButton(RUB);
                btn2.setCallbackData("rub");
                row1.add(btn1);
                row1.add(btn2);
                rowList.add(row1);
                inlineKeyboardMarkup.setKeyboard(rowList);

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }



//                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//                sendMessage.setReplyMarkup(keyboardMarkup);
//                keyboardMarkup.setResizeKeyboard(true);
//                keyboardMarkup.setOneTimeKeyboard(true);
//                keyboardMarkup.setSelective(true);
//
//                List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//                KeyboardRow row1 = new KeyboardRow();
//                row1.add(new KeyboardButton(UZS_USD));
//                row1.add(new KeyboardButton(USD_UZS));
//
//                KeyboardRow row2 = new KeyboardRow();
//                row2.add(new KeyboardButton(UZS_RUB));
//                row2.add(new KeyboardButton(RUB_USZ));
//                keyboardRows.add(row1);
//                keyboardRows.add(row2);
//
//                keyboardMarkup.setKeyboard(keyboardRows);

        }


    }


    private Double convertAmount() {

        return null;
    }


}
