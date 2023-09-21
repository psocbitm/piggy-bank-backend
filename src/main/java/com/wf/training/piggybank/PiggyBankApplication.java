package com.wf.training.piggybank;

import com.wf.training.piggybank.config.CorsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CorsConfig.class)
public class PiggyBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiggyBankApplication.class, args);
	}

}
