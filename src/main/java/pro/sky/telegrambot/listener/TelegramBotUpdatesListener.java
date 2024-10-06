package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationRepository repository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                Chat chat = update.message().chat();
                long chatId = chat.id();
                Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
                Matcher matcher = pattern.matcher(update.message().text());
                if (update.message().text().equals("/start")) {
                    String name = chat.firstName() + " " + chat.lastName();
                    String messageText = "Привет, " + name + "!";
                    SendMessage sendMessage = new SendMessage(chatId, messageText);
                    telegramBot.execute(sendMessage);
                } else if (matcher.matches()) {
                    String scheduledDateTimeText = update.message().text().substring(0, 16);
                    String scheduledMessage = update.message().text().substring(17);
                    LocalDateTime sheduledDateTime = LocalDateTime.parse(
                            scheduledDateTimeText,
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    String messageText = scheduledDateTimeText + ' ' + scheduledMessage;
                    repository.save(new NotificationTask(chatId, scheduledMessage, sheduledDateTime));
                    SendMessage sendMessage = new SendMessage(chatId, messageText);
                    telegramBot.execute(sendMessage);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
    }

}
