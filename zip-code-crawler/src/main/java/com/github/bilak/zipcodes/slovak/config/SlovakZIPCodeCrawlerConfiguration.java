package com.github.bilak.zipcodes.slovak.config;

import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.bilak.zipcodes.slovak.batch.SlovakZIPCodeSyncService;
import com.github.bilak.zipcodes.slovak.persistence.repository.CityRepository;
import com.github.bilak.zipcodes.slovak.persistence.repository.StreetRepository;

/**
 * Configuration for Slovak ZIP code crawler.
 *
 * @author Lukáš Vasek
 */
@Configuration
public class SlovakZIPCodeCrawlerConfiguration {

    @Bean
    public SlovakZIPCodeSyncService slovakZIPCodeSyncService(
            @Value("${zip-code-crawler.sk-download-url}") final URL downloadUrl,
            final CityRepository cityRepository,
            final StreetRepository streetRepository) {
        return new SlovakZIPCodeSyncService(downloadUrl, cityRepository, streetRepository);
    }
}
