package ru.practicum.shareit;

import jakarta.validation.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.core.config.JsonNullableValueExtractor;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

	@Bean
	static LocalValidatorFactoryBean localValidatorFactoryBean() {

		return new LocalValidatorFactoryBean() {
			@Override
			protected void postProcessConfiguration(Configuration<?> configuration) {
				configuration.addValueExtractor(new JsonNullableValueExtractor());
				super.postProcessConfiguration(configuration);
			}
		};
	}
}
