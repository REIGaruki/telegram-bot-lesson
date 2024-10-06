package pro.sky.telegrambot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.service.OutputService;


@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {

	@Autowired
	private OutputService outputService;

	@Scheduled(cron = "0 0/1 * * * *")
	public void notifyOnDate() {
		outputService.notifyOnDate();
	}

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

}
