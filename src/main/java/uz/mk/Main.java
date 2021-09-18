package uz.mk;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

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
            sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
            String callbackText = update.getCallbackQuery().getData();
            if (callbackText.equals("name")) {
                sendMessage.setText("John");
            } else if (callbackText.equals("surname")) {
                sendMessage.setText("Doe");
            } else {
                sendMessage.setText("I don't understand");

            }
        }else if (update.hasMessage()){
            String inputText = update.getMessage().getText();
            sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));

            if (inputText.equals("/start")) {
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton("What is your name");
                btn1.setCallbackData("name");
                InlineKeyboardButton btn2 = new InlineKeyboardButton("What is your surname");
                btn2.setCallbackData("surname");
                row1.add(btn1);
                row1.add(btn2);
                rowList.add(row1);

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton btn2_1 = new InlineKeyboardButton("What");
                btn2_1.setCallbackData("Hello");
                row2.add(btn2_1);
                rowList.add(row2);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setText("Welcome to our bot");


            } else {
                if (inputText.toLowerCase().equals("hello")) {
                    sendMessage.setText("How are you");
                } else {
                    sendMessage.setText("I don't understand");

                }
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                sendMessage.setReplyMarkup(keyboardMarkup);
                keyboardMarkup.setResizeKeyboard(true);
                keyboardMarkup.setOneTimeKeyboard(true);
                keyboardMarkup.setSelective(true);

                List<KeyboardRow> keyboardRows = new ArrayList<>();

                KeyboardRow row1 = new KeyboardRow();
                KeyboardButton button1 = new KeyboardButton("hello");
                KeyboardButton button2 = new KeyboardButton("bye");
                row1.add(button1);
                row1.add(button2);

                KeyboardRow row2 = new KeyboardRow();
                row2.add("and you ?");

                keyboardRows.add(row1);
                keyboardRows.add(row2);

                keyboardMarkup.setKeyboard(keyboardRows);
            }
        }



        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
