package com.marcelo.integramodulo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages={"com.marcelo.integramodulo", "com.marcelo.tfg"})
public class IntegramoduloApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(IntegramoduloApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(IntegramoduloApplication.class);
    }

}
