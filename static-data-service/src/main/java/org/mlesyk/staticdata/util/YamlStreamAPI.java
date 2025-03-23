package org.mlesyk.staticdata.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class YamlStreamAPI {
    @Value("${app.eve.sde.type_id_filename}")
    private String typeIDsFileName;

    @Value("${app.eve.sde.market_groups_filename}")
    private String marketGroupsFileName;

    @Value("${app.eve.sde.solar_systems_filename}")
    private String solarSystemFileName;

    private final ZipFileReader zipFileReader;

    @Autowired
    public YamlStreamAPI(ZipFileReader zipFileReader) {
        this.zipFileReader = zipFileReader;
    }

    public InputStream getTypeIDsInputStream() throws IOException {
        return zipFileReader.readEntityCollectionFromSingleFile(typeIDsFileName);
    }

    public InputStream getMarketGroupsInputStream() throws IOException {
        return zipFileReader.readEntityCollectionFromSingleFile(marketGroupsFileName);
    }

    public Map<String, InputStream> getSolarSystemInputStream() throws IOException {
        return zipFileReader.readEntityCollectionSingleFilePerEntity(solarSystemFileName);
    }
}