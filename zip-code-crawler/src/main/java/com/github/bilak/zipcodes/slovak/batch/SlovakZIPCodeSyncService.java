package com.github.bilak.zipcodes.slovak.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import com.github.bilak.zipcodes.slovak.persistence.model.Obec;
import com.github.bilak.zipcodes.slovak.persistence.model.Ulica;
import com.github.bilak.zipcodes.slovak.persistence.repository.CityRepository;
import com.github.bilak.zipcodes.slovak.persistence.repository.StreetRepository;
import com.github.bilak.zipcodes.util.ExcelReader;
import lombok.extern.slf4j.Slf4j;

/**
 * Service which synchronizes slovak ZIP codes.
 *
 * @author Lukáš Vasek
 */
@Slf4j
public class SlovakZIPCodeSyncService {

    private final URL downloadUrl;
    private final CityRepository cityRepository;
    private final StreetRepository streetRepository;

    public SlovakZIPCodeSyncService(
            final URL downloadUrl,
            final CityRepository cityRepository,
            final StreetRepository streetRepository) {
        this.downloadUrl = downloadUrl;
        this.cityRepository = cityRepository;
        this.streetRepository = streetRepository;
    }

    @Scheduled(cron = "${zip-code-crawler.sk-download-cron:0 0 23 ? * SAT}")
    public void process() {
        logger.debug("SK ZIP Codes sync start");
        final File tempDirectory = createTemDirectory();
        try {
            final Collection<FileSystemResource> zipCodeFiles = getZIPCodeFiles(tempDirectory);
            syncStreets(getFile(zipCodeFiles, "ULICE.xlsx"));
            syncCities(getFile(zipCodeFiles, "OBCE.xlsx"));
        } finally {
            if (tempDirectory.exists()) {
                FileSystemUtils.deleteRecursively(tempDirectory);
            }
        }
        logger.debug("SK ZIP Codes sync end");
    }

    private FileSystemResource getFile(final Collection<FileSystemResource> zipCodeFiles, final String fileName) {
        return zipCodeFiles
                .stream()
                .filter(r -> fileName.contains(r.getFilename()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unable to get [%s]. Was something changed?", fileName)));
    }

    private void syncStreets(final FileSystemResource streets) {
        final Stream<Ulica> streetStream = ExcelReader.load(streetMapper(), streets.getFile());
        streetRepository.deleteAll();
        streetRepository.saveAll(streetStream.collect(Collectors.toList()));
        logger.debug("[{}] streets exists", streetRepository.count());

    }

    private Function<Row, Ulica> streetMapper() {
        return row -> {
            int i = 0;
            return Ulica.builder()
                    .id(String.valueOf(row.getRowNum()))
                    .dulica(ExcelReader.asString(row.getCell(i++)))
                    .ulica(ExcelReader.asString(row.getCell(i++)))
                    .psc(ExcelReader.asString(row.getCell(i++)))
                    .dposta(ExcelReader.asString(row.getCell(i++)))
                    .posta(ExcelReader.asString(row.getCell(i++)))
                    .poznamka(ExcelReader.asString(row.getCell(i++)))
                    .obce(ExcelReader.asString(row.getCell(i)))
                    .build();
        };
    }

    private void syncCities(final FileSystemResource cities) {
        final Stream<Obec> citiesStream = ExcelReader.load(cityMapper(), cities.getFile());
        cityRepository.deleteAll();
        cityRepository.saveAll(citiesStream.collect(Collectors.toList()));
        logger.debug("[{}] cities exists", cityRepository.count());
    }

    private Function<Row, Obec> cityMapper() {
        return row -> {
            int i = 0;
            return Obec.builder()
                    .id(String.valueOf(row.getRowNum()))
                    .dobec(ExcelReader.asString(row.getCell(i++)))
                    .obec(ExcelReader.asString(row.getCell(i++)))
                    .okres(ExcelReader.asString(row.getCell(i++)))
                    .psc(ExcelReader.asString(row.getCell(i++)))
                    .dposta(ExcelReader.asString(row.getCell(i++)))
                    .posta(ExcelReader.asString(row.getCell(i++)))
                    .kodOkresu(getFullNumber(ExcelReader.asString(row.getCell(i++))))
                    .kraj(ExcelReader.asString(row.getCell(i)))
                    .build();
        };
    }

    private String getFullNumber(final String source) {
        if (source != null && source.contains(".")) {
            return source.substring(0, source.indexOf('.'));
        }
        return source;
    }

    private Collection<FileSystemResource> getZIPCodeFiles(final File tempDirectory) {
        final FileSystemResource zipCodeArchive = downloadZIPCodeFile(tempDirectory);
        return unpackZIPCodeArchive(zipCodeArchive);
    }

    private Collection<FileSystemResource> unpackZIPCodeArchive(final FileSystemResource zipCodeArchive) {
        if (zipCodeArchive.exists()) {
            try (final ZipFile zipFile = new ZipFile(zipCodeArchive.getFile())) {

                return zipFile.stream()
                        .map(zipEntry -> unzipFile(zipCodeArchive.getFile().getParentFile(), zipFile, zipEntry))
                        .collect(Collectors.toList());
            } catch (final IOException e) {
                throw new IllegalArgumentException(String.format("Unable to unzip file [%s]", zipCodeArchive.toString()));
            } finally {
                if (zipCodeArchive.exists()) {
                    FileSystemUtils.deleteRecursively(zipCodeArchive.getFile());
                }
            }
        }
        return Collections.emptyList();
    }

    private FileSystemResource unzipFile(final File targetDir, final ZipFile zipFile, final ZipEntry zipEntry) {
        logger.debug("Going to unzip file [{}] to [{}]", zipEntry.getName(), targetDir);
        final FileSystemResource outputFile = new FileSystemResource(new File(targetDir, zipEntry.getName()));
        try (final FileOutputStream fos = new FileOutputStream(outputFile.getFile())) {
            FileCopyUtils.copy(zipFile.getInputStream(zipEntry), fos);
            return outputFile;
        } catch (final IOException e) {
            throw new IllegalStateException(String.format("Unable to unzip file [%s]", zipEntry.getName()));
        }
    }

    private FileSystemResource downloadZIPCodeFile(final File tempDirectory) {
        final File tempZipFile = new File(tempDirectory, "zip-codes.zip");
        logger.debug("Going to download file [{}] to [{}]", downloadUrl, tempZipFile);
        try (final ReadableByteChannel channel = Channels.newChannel(downloadUrl.openStream());
             final FileOutputStream fileOutputStream = new FileOutputStream(tempZipFile)) {

            fileOutputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

            return new FileSystemResource(tempZipFile);
        } catch (final IOException e) {
            throw new IllegalStateException(String.format("Unable to download file from [%s]", downloadUrl.toString()));
        }
    }

    private File createTemDirectory() {
        try {
            return Files.createTempDirectory("zip-codes").toFile();
        } catch (final IOException e) {
            throw new IllegalStateException("Unable to crate temp directory", e);
        }
    }
}
