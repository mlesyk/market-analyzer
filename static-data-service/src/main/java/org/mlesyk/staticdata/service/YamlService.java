package org.mlesyk.staticdata.service;

import org.mlesyk.staticdata.model.yaml.MarketGroupYamlDTO;
import org.mlesyk.staticdata.model.yaml.MarketTypeYamlDTO;
import org.mlesyk.staticdata.model.yaml.UniverseSystemYamlDTO;
import org.mlesyk.staticdata.util.YamlParser;
import org.mlesyk.staticdata.util.YamlStreamAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class YamlService {

    private final YamlParser yamlReader;
    private final YamlStreamAPI zipFileStreamAPI;

    @Autowired
    public YamlService(YamlParser yamlReader, YamlStreamAPI zipFileStreamAPI) {
        this.yamlReader = yamlReader;
        this.zipFileStreamAPI = zipFileStreamAPI;
    }

    public Map<Integer, MarketGroupYamlDTO> getAllMarketItemGroupsWithMarketItems() throws IOException {
        return yamlReader.parseMarketGroupsWithMarketItemsBound(zipFileStreamAPI.getMarketGroupsInputStream(), zipFileStreamAPI.getTypeIDsInputStream());
    }

    public Map<Integer, MarketTypeYamlDTO> getAllMarketItems() throws IOException {
        return yamlReader.parseMarketItems(zipFileStreamAPI.getTypeIDsInputStream());
    }

    public Map<Integer, MarketGroupYamlDTO> getAllMarketItemGroups() throws IOException {
        return yamlReader.parseMarketItemGroups(zipFileStreamAPI.getMarketGroupsInputStream());
    }

    public Map<Integer, UniverseSystemYamlDTO> getAllSolarSystems() throws IOException {
        return yamlReader.readSolarSystemsFromYaml(zipFileStreamAPI.getSolarSystemInputStream());
    }
}
