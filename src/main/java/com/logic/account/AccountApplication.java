package com.logic.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;

@SpringBootApplication
public class AccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountApplication.class, args);
	}

	@Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
			.registerModule(new Hibernate5JakartaModule())
			.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
			.setSerializationInclusion(Include.NON_NULL);
    }
}
