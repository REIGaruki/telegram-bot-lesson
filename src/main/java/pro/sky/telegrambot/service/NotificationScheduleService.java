package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@EnableScheduling
public class NotificationScheduleService {

    private Logger logger = LoggerFactory.getLogger(NotificationScheduleService.class);

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private TelegramBot telegramBot;

    @Scheduled(cron = "${spring.application.cron}")
    //spring.application.cron=0 0/1 * * * *
    private void notifyOnDate() {
        logger.info("Seek notifications on: {}", LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        List<NotificationTask> notificationTaskList = repository.findNotificationTaskByNotificationDateTime(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        );
        notificationTaskList.forEach((task) -> {
            long chatId = task.getChatId();
            String messageText = task.getMessage();
            SendMessage sendMessage = new SendMessage(chatId, messageText);
            telegramBot.execute(sendMessage);
            logger.info("Notification has been send");
        });
    }
}
