package org.mlesyk.staticdata.util;

import org.mlesyk.staticdata.exception.ZipFileReaderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class ZipFileReader {

    @Value("${app.eve.sde.file_name}")
    private String sdeFileName;

    @Value("${app.eve.sde.file_path}")
    private String sdeFilePath;

    public InputStream readEntityCollectionFromSingleFile(String fileName) throws IOException {
        File sdeFile = new File(sdeFilePath, sdeFileName);
        try (ZipFile zipFile = new ZipFile(sdeFile)) {
            Optional<? extends ZipEntry> zipEntryFromArchive = zipFile.stream().filter(e -> e.getName().endsWith(fileName)).findFirst();
            if (zipEntryFromArchive.isPresent()) {
                return zipFile.getInputStream(zipEntryFromArchive.get());
            } else {
                throw new IOException("File with name " + fileName + " was not found");
            }
        }
    }

    public Map<String, InputStream> readEntityCollectionSingleFilePerEntity(String fileName) throws IOException {
        File sdeFile = new File(sdeFilePath, sdeFileName);
        try (ZipFile zipFile = new ZipFile(sdeFile)) {
            return zipFile.stream().filter(e -> e.getName().endsWith(fileName)).collect(Collectors.toMap(ZipEntry::getName, zipEntry -> {
                try {
                    return zipFile.getInputStream(zipEntry);
                } catch (IOException e) {
                    throw new ZipFileReaderException("Error while reading zip file", e);
                }
            }));
        }
    }
}