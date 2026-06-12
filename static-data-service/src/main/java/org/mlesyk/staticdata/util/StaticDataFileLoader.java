package org.mlesyk.staticdata.util;

import org.mlesyk.staticdata.exception.ResourcesUrlFilePathParsingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.HashCode;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class StaticDataFileLoader {

    public static final String ABS_HREF = "abs:href";

    @Value("${app.sde.file_name}")
    private String sdeFileName;

    @Value("${app.sde.file_path}")
    private String sdeFilePath;

    @Value("${app.sde.checksum_name}")
    private String sdeChecksumURLText;

    @Value("${app.sde.resources_url}")
    private String eveResourcesURL;

    public URI getSDEFileURL() throws IOException, URISyntaxException {
        JsonNode meta = new ObjectMapper().readTree(new URL(eveResourcesURL + "tranquility/latest.jsonl"));
        JsonNode buildNode = meta.get("buildNumber");
        if (buildNode == null || !buildNode.canConvertToLong()) {
            throw new ResourcesUrlFilePathParsingException("Can't parse file URL");
        }
        return new URI(eveResourcesURL + "tranquility/eve-online-static-data-" + buildNode.asLong() + "-yaml.zip");
    }

    public String getRemoteSDEChecksum() throws IOException, URISyntaxException {
        URI sdeChecksumURI = new URI(getAbsoluteFileUrlByText(eveResourcesURL, sdeChecksumURLText).attr(ABS_HREF));
        File fileDestination = new File("./", sdeChecksumURLText);
        FileUtils.copyURLToFile(sdeChecksumURI.toURL(), fileDestination, 5000, 5000);
        List<String> lines = FileUtils.readLines(fileDestination, StandardCharsets.UTF_8);
        return String.join("", lines).trim().toUpperCase();
    }

    public String getLocalSDEChecksum() throws IOException {
        File file = new File(sdeFilePath, sdeFileName);
        HashCode hash = com.google.common.hash.Hashing.md5().hashBytes(FileUtils.readFileToByteArray(file));
        return hash.toString().toUpperCase();
    }

    public void downloadNewSDE() throws IOException, URISyntaxException {
        File file = new File(sdeFilePath, sdeFileName);
        FileUtils.copyURLToFile(getSDEFileURL().toURL(), file, 5000, 5000);
    }

    public boolean sdeExists() {
        File file = new File(sdeFilePath, sdeFileName);
        return file.exists();
    }

    private Element getAbsoluteFileUrlByText(String resourcesURL, String urlText) throws IOException {
        Document doc = Jsoup.connect(resourcesURL).get();
        Elements links = doc.select("a");
        return links.stream().filter(e -> e.text().equals(urlText)).findFirst().orElseThrow(() -> new ResourcesUrlFilePathParsingException("Can't parse file URL"));
    }
}
