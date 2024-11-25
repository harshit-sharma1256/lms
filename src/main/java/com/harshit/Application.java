package com.harshit;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(        info = @Info(
        title = "Library Management System",
        version = "1.0",
        description = "Book API",
        contact = @Contact(url = "http://gigantic-server.com", name = "Harshit", email = "Harshit.Sharma@target.com")
))
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}