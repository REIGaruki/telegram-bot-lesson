package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InputService {

    @Value("${telegram.bot.starting-message}")
    private String startMessage;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private NotificationRepository repository;

    private SendMessage sendStartMessage(Message message) {
        logger.info("Processing start message");
        String name = message.chat().firstName() + " " + message.chat().lastName();
        String messageText = "Привет, " + name + "!";
        return new SendMessage(message.chat().id(), messageText);
    }

    private SendMessage sendNotificationTaskMessage(Message message) {
        logger.info("Processing notification task");
        String scheduledDateTimeText = message.text().substring(0, 16);
        String scheduledMessage = message.text().substring(17);
        LocalDateTime scheduledDateTime = LocalDateTime.parse(
                scheduledDateTimeText,
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        repository.save(new NotificationTask(message.chat().id(), scheduledMessage, scheduledDateTime));
        String messageText = "Ожидайте уведомление \"" + scheduledMessage
                + "\" в назначенное время: " + scheduledDateTimeText;
        return new SendMessage(message.chat().id(), messageText);
    }

    private SendMessage sendNotAvailableMessage(Message message) {
        return new SendMessage(message.chat().id(), "Неверная команда");
    }

    public SendMessage manageUpdateMessage(Message message) {
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
        Matcher matcher = pattern.matcher(message.text());
        if (message.text().equals(startMessage)) {
            return sendStartMessage(message);
        } else if (matcher.matches()) {
            return sendNotificationTaskMessage(message);
        } else {
            return sendNotAvailableMessage(message);
        }
    }
}
