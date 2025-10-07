package com.was;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class WebAuthSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAuthSystemApplication.class, args);
    }

}
