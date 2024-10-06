package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue
    private Long id;

    private Long chatId;

    private String message;

    private LocalDateTime notificationDateTime;

    public NotificationTask() {

    }


}
