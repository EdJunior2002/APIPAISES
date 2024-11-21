package com.meuprojeto.apipaises.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API HELLO WORLD")
                        .description("SEJA BEM-VINDO A API HELLO WORLD")
                        .version("v1")
                        .contact(new Contact()
                                .name("GitHub")
                                .url("https://github.com/EdJunior2002/APIPAISES")
                        ));

    }
}