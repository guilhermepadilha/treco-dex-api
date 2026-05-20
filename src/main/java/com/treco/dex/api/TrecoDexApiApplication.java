package com.treco.dex.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.treco.dex.api"})
public class TrecoDexApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrecoDexApiApplication.class, args);
    }
}
