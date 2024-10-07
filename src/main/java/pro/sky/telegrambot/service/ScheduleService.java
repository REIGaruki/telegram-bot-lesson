package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class ScheduleService {

    @Autowired
    NotificationScheduleService notificationScheduleService;

    @Scheduled(cron = "${spring.application.cron}")
    //spring.application.cron=0 0/1 * * * *
    private void notifyOn() {
        notificationScheduleService.notifyOnDate();
    }
}
