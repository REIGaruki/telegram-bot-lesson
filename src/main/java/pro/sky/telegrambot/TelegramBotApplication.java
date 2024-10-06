package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {

	@Autowired
	private TelegramBot telegramBot;

	@Autowired
	private NotificationRepository repository;

	@Scheduled(cron = "0 0/1 * * * *")
	public void notifyOnDate() {
		List<NotificationTask> notificationTaskList = repository.findNotificationTaskByNotificationDateTime(
				LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
		);
		notificationTaskList.forEach((task) -> {
			long chatId = task.getChatId();
			String messageText = task.getMessage();
			SendMessage sendMessage = new SendMessage(chatId, messageText);
			telegramBot.execute(sendMessage);
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

}
