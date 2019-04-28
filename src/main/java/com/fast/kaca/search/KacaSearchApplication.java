package com.fast.kaca.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author sys
 */
@EnableScheduling
@SpringBootApplication
public class KacaSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(KacaSearchApplication.class, args);
    }

}
