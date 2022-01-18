package com.safetynet.alert;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class AlertApplication {

    public static void main(String[] args) {

        SpringApplication.run(AlertApplication.class, args);

    }

}
