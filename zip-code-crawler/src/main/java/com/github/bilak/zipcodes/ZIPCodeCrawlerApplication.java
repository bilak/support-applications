package com.github.bilak.zipcodes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.github.bilak.zipcodes.slovak.batch.SlovakZIPCodeSyncService;

/**
 * ZIP Code crawler main application runner.
 *
 * @author Lukáš Vasek
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ZIPCodeCrawlerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ZIPCodeCrawlerApplication.class, args);
    }

    @Bean
    CommandLineRunner initializer(final SlovakZIPCodeSyncService zipCodeSyncService,
            @Value("${zip-code-crawler.run-on-startup:false}") final Boolean runOnStartup) {
        return runner -> {
            if (runOnStartup) {
                zipCodeSyncService.process();
            }
        };
    }
}
