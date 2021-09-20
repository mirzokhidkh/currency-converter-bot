package uz.mk;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
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

    private String toCurrency;
    private String fromCurrency;

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
        SendMessage toMessage = new SendMessage();
        Message fromMessage = update.getMessage();

        if (update.hasCallbackQuery()) {
            EditMessageText editMessageText = new EditMessageText();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = update.getCallbackQuery().getData();

            editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton btn1;
            InlineKeyboardButton btn2;

            switch (callbackData) {
                case USD:
                    editMessageText.setText("Choose one of these:");
                    row1 = new ArrayList<>();
                    btn1 = createInlineKeyboardButton(UZS_USD_WITH_FLAG, UZS_USD);
                    btn2 = createInlineKeyboardButton(USD_UZS_WITH_FLAG, USD_UZS);
                    row1.add(btn1);
                    row1.add(btn2);
                    rowList.add(row1);
                    inlineKeyboardMarkup.setKeyboard(rowList);
                    editMessageText.setReplyMarkup(inlineKeyboardMarkup);
//                    toCurrency = callbackData;
                    break;
                case RUB:
                    editMessageText.setText("Choose one of these:");
                    row1 = new ArrayList<>();
                    btn1 = createInlineKeyboardButton(UZS_RUB_WITH_FLAG, UZS_RUB);
                    btn2 = createInlineKeyboardButton(RUB_USZ_WITH_FLAG, RUB_USZ);
                    row1.add(btn1);
                    row1.add(btn2);
                    rowList.add(row1);
                    inlineKeyboardMarkup.setKeyboard(rowList);
                    editMessageText.setReplyMarkup(inlineKeyboardMarkup);
//                    toCurrency = callbackData;
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


            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else if (update.hasMessage() && fromMessage.hasText()) {
            String inputText = fromMessage.getText();
            Long chatId = fromMessage.getChatId();

            toMessage.setChatId(String.valueOf(chatId));


            if (inputText.matches("^[0-9]+$")) {
                Double amount = Double.parseDouble(inputText);
                String convertedAmount = convertAmount(amount, fromCurrency, toCurrency);
                toMessage.setText(amount + " " + fromCurrency + " = " + convertedAmount + " " + toCurrency);
                try {
                    execute(toMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }


            switch (inputText) {
                case "/start":
                    toMessage.setText("Welcome to our currency converter bot. " +
                            "This bot will help you to convert Uzbek Som into another currency.\n" +
                            "Choose one of on the given currency units.");

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    toMessage.setReplyMarkup(inlineKeyboardMarkup);

                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                    List<InlineKeyboardButton> row1 = new ArrayList<>();
                    InlineKeyboardButton button1 = createInlineKeyboardButton(USD_WITH_FLAG, USD);
                    InlineKeyboardButton button2 = createInlineKeyboardButton(RUB_WITH_FLAG, RUB);
                    row1.add(button1);
                    row1.add(button2);
                    rowList.add(row1);
                    inlineKeyboardMarkup.setKeyboard(rowList);

                    try {
                        execute(toMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;

                case "convert":
                    break;
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


}
