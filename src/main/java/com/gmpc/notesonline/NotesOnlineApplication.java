package com.gmpc.notesonline;

import com.gmpc.notesonline.note.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotesOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotesOnlineApplication.class, args);
	}
	@Bean
	public IdWorker idWorker() {
		return new IdWorker(1, 1);
	}

}
