package uz.mk.repository;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mk.model.Currency;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static uz.mk.utils.InlineButton.*;
import static uz.mk.utils.KeyboardButtons.*;
import static uz.mk.utils.KeyboardButtons.UZS;

public class ConverterController extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterController.class);

    private String toCurrency;
    private String fromCurrency;
    private String lastCallbackData;

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
        Message message = update.getMessage();


        if (update.hasCallbackQuery() && !update.getCallbackQuery().getData().isEmpty()) {
            EditMessageText editMessageText = new EditMessageText();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            User user = callbackQuery.getFrom();
            message = callbackQuery.getMessage();
            String data = callbackQuery.getData();

            LOGGER.info("messageId: " + message.getMessageId() + "  User_Name: " + user.getFirstName() + "  message: " + data);


            editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());


            int i = 1;

            while (i-- != 0) {
//                lastCallbackData = callbackData;
                switch (data) {
                    case BACK:
                        i++;
                        break;
                    case USD:
                        editMessageText.setText("Choose one of these:");
                        editMessageText.setReplyMarkup(keyboardMarkup(
                                rowCollection(
                                        row(
                                                keyboardButton(EmojiParser.parseToUnicode(":uz:UZS :arrows_counterclockwise: USD:us:"), UZS_USD),
                                                keyboardButton(EmojiParser.parseToUnicode(":us:USD :arrows_counterclockwise: UZS:uz:"), USD_UZS)
                                        ),
                                        row(keyboardButton(BACK, BACK))
                                )));
                        break;
                    case RUB:
                        editMessageText.setText("Choose one of these:");
                        editMessageText.setReplyMarkup(keyboardMarkup(
                                rowCollection(
                                        row(
                                                keyboardButton(EmojiParser.parseToUnicode(":uz:UZS :arrows_counterclockwise: RUB:ru:"), UZS_USD),
                                                keyboardButton(EmojiParser.parseToUnicode(":ru:RUB :arrows_counterclockwise: UZS:uz:"), USD_UZS)
                                        ),
                                        row(keyboardButton(BACK, BACK))
                                )));
                        break;
                    case UZS_USD:
                        fromCurrency = UZS;
                        toCurrency = USD;
                        editMessageText.setText("Enter amount which is converting :");
                        break;
                    case USD_UZS:
                        fromCurrency = USD;
                        toCurrency = UZS;
                        editMessageText.setText("Enter amount which is converting :");
                        break;
                    case UZS_RUB:
                        fromCurrency = UZS;
                        toCurrency = RUB;
                        editMessageText.setText("Enter amount which is converting :");
                        break;
                    case RUB_USZ:
                        fromCurrency = RUB;
                        toCurrency = UZS;
                        editMessageText.setText("Enter amount which is converting :");
                        break;

                }
            }


            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


        } else if (update.hasMessage() && message.hasText()) {
            String inputText = message.getText();
            Long chatId = message.getChatId();

            sendMessage.setChatId(String.valueOf(chatId));

            boolean isDigits = inputText.matches("^[0-9]+$");

            if (isDigits && (fromCurrency != null && toCurrency != null)) {
                Double amount = Double.parseDouble(inputText);
                String convertedAmount = convertAmount(amount, fromCurrency, toCurrency);
                sendMessage.setText(amount + " " + fromCurrency + " = " + convertedAmount + " " + toCurrency);
                clearCurrencies();

                executeMessage(sendMessage);
            } else if (isDigits) {
                sendMessage.setText("Please click the given buttons");
                clearCurrencies();

                executeMessage(sendMessage);
            }

            switch (inputText) {

                case "/start":
                    sendMessage.setText("Welcome to our currency converter bot. " +
                            "This bot will help you to convert Uzbek Som into another currency.");

                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    sendMessage.setReplyMarkup(keyboardMarkup);
                    keyboardMarkup.setResizeKeyboard(true);
                    keyboardMarkup.setOneTimeKeyboard(true);
                    keyboardMarkup.setSelective(true);

                    List<KeyboardRow> keyboardRows = new ArrayList<>();

                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRow.add(new KeyboardButton(CONVERTER));
                    keyboardRows.add(keyboardRow);

                    keyboardMarkup.setKeyboard(keyboardRows);

                    executeMessage(sendMessage);
                    break;

                case CONVERTER:
                    sendMessage.setText("Choose one of on the given currency units.");
                    sendMessage.setReplyMarkup(keyboardMarkup(
                            rowCollection(
                                    row(
                                            keyboardButton(EmojiParser.parseToUnicode("USD:us:"), USD),
                                            keyboardButton(EmojiParser.parseToUnicode("RUB:ru:"), RUB)
                                    ),
                                    row(keyboardButton(BACK,BACK)))));
                    executeMessage(sendMessage);
                    break;
            }


        }


    }


    private String convertAmount(Double amount, String fromCcy, String toCcy) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<Currency> currencies = new ArrayList<>();
        String convertedAmount = null;

        try {
            URL url = new URL("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
            URLConnection urlConnection = url.openConnection();
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            Type type = new TypeToken<ArrayList<Currency>>() {
            }.getType();
            currencies = gson.fromJson(reader, type);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ccy;
        if (fromCcy.equals(UZS)) {
            ccy = toCcy;
        } else {
            ccy = fromCcy;
        }

        DecimalFormat decimalFormat = new DecimalFormat("##0.###");

        for (Currency currency : currencies) {
            if (currency.getCcy().equals(ccy)) {
                if (fromCcy.equals(UZS)) {
                    convertedAmount = decimalFormat.format(amount / Double.parseDouble(currency.getRate()));
                } else {
                    convertedAmount = String.valueOf(amount * Double.parseDouble(currency.getRate()));
                }
                break;
            }
        }
        return convertedAmount;
    }


    public InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }


    public void clearCurrencies() {
        fromCurrency = null;
        toCurrency = null;
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
