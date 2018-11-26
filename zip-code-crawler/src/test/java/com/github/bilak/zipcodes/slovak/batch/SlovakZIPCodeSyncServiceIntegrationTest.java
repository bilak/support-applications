package com.github.bilak.zipcodes.slovak.batch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.github.bilak.zipcodes.slovak.persistence.model.Obec;
import com.github.bilak.zipcodes.slovak.persistence.repository.CityRepository;
import com.github.bilak.zipcodes.slovak.persistence.repository.StreetRepository;

/**
 * Test suite for {@link SlovakZIPCodeSyncService}.
 *
 * @author Lukáš Vasek
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        SlovakZIPCodeSyncServiceIntegrationTest.Config.class
})
public class SlovakZIPCodeSyncServiceIntegrationTest {

    @Autowired
    private SlovakZIPCodeSyncService service;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StreetRepository streetRepository;

    @Test
    @Transactional
    public void testProcessing() {
        service.process();
        assertThat("there should be 10 cities", cityRepository.count(), is(9L));
        assertThat("there should be 10 streets", streetRepository.count(), is(9L));
    }


    @Configuration
    @EnableAutoConfiguration
    @EnableJpaRepositories(basePackageClasses = {CityRepository.class})
    @EntityScan(basePackageClasses = {Obec.class})
    static class Config {

        @Bean
        public SlovakZIPCodeSyncService slovakZIPCodeSyncService(final CityRepository cityRepository,
                final StreetRepository streetRepository) throws IOException {
            final ClassPathResource zipCodesArchive = new ClassPathResource("slovak-zip-codes.zip");
            return new SlovakZIPCodeSyncService(zipCodesArchive.getURL(), cityRepository, streetRepository);
        }

    }
}