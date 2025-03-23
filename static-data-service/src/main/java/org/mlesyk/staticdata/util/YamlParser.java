package org.mlesyk.staticdata.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import lombok.extern.slf4j.Slf4j;
import org.mlesyk.staticdata.model.yaml.MarketGroupYamlDTO;
import org.mlesyk.staticdata.model.yaml.MarketTypeYamlDTO;
import org.mlesyk.staticdata.model.yaml.UniverseSystemYamlDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class YamlParser {

    private final ObjectMapper yamlObjectMapper;

    @Autowired
    public YamlParser(@Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper) {
        this.yamlObjectMapper = yamlObjectMapper;
    }

    private static final Pattern solarSystemPatternFromZipEntryName = Pattern.compile(".*universe.[a-z]+.(?<regionName>[a-zA-Z0-9\\-]+).(?<constellationName>[a-zA-Z0-9\\-]+).(?<solarSystemName>[a-zA-Z0-9\\-]+)..*");

    public Map<Integer, MarketTypeYamlDTO> parseMarketItems(InputStream is) throws IOException {
        Map<Integer, MarketTypeYamlDTO> marketItemMap = parseMultipleObjectsFromYaml(is, new TypeReference<Map<Integer, MarketTypeYamlDTO>>() {
        });
        for (Map.Entry<Integer, MarketTypeYamlDTO> entry : marketItemMap.entrySet()) {
            entry.getValue().setMarketItemID(entry.getKey());
        }
        return marketItemMap;
    }

    public Map<Integer, MarketGroupYamlDTO> parseMarketItemGroups(InputStream is) throws IOException {
        Map<Integer, MarketGroupYamlDTO> marketItemGroupInfoMap = parseMultipleObjectsFromYaml(is, new TypeReference<Map<Integer, MarketGroupYamlDTO>>() {
        });
        for (Map.Entry<Integer, MarketGroupYamlDTO> entry : marketItemGroupInfoMap.entrySet()) {
            entry.getValue().setMarketGroupID(entry.getKey());
        }
        return marketItemGroupInfoMap;
    }

    public Map<Integer, MarketGroupYamlDTO> parseMarketGroupsWithMarketItemsBound(InputStream inputStreamMarketGroupsData, InputStream inputStreamTypeIDsData) throws IOException {
        Map<Integer, MarketGroupYamlDTO> marketItemGroupInfoMap = parseMarketItemGroups(inputStreamMarketGroupsData);
        Map<Integer, MarketTypeYamlDTO> marketItemMap = parseMarketItems(inputStreamTypeIDsData);
        for (Map.Entry<Integer, MarketTypeYamlDTO> entry : marketItemMap.entrySet()) {
            MarketTypeYamlDTO item = entry.getValue();
            Integer marketGroupID = item.getMarketGroupID();
            if (marketGroupID != null) {
                MarketGroupYamlDTO marketItemGroupInfo = marketItemGroupInfoMap.get(marketGroupID);
                if (marketItemGroupInfo != null) {
                    List<MarketTypeYamlDTO> typesOfMarketGroup = marketItemGroupInfo.getTypes();
                    typesOfMarketGroup.add(item);
                }
            }
        }
        return marketItemGroupInfoMap;
    }

    public Map<Integer, UniverseSystemYamlDTO> readSolarSystemsFromYaml(Map<String, InputStream> zipEntryNameToYamlInputStream) throws IOException {
        Map<Integer, UniverseSystemYamlDTO> solarSystemMap = new HashMap<>();

        for (Map.Entry<String, InputStream> entry : zipEntryNameToYamlInputStream.entrySet()) {
            UniverseSystemYamlDTO solarSystem = parseSingleObjectFromYaml(entry.getValue(), new TypeReference<UniverseSystemYamlDTO>() {
            });
            String zipEntryName = entry.getKey();
            Matcher solarSystemDataMatcher = solarSystemPatternFromZipEntryName.matcher(zipEntryName);
            if (solarSystemDataMatcher.find()) {
                solarSystem.setSolarSystemName(solarSystemDataMatcher.group("solarSystemName"));
                solarSystem.setConstellationName(solarSystemDataMatcher.group("constellationName"));
                solarSystem.setRegionName(solarSystemDataMatcher.group("regionName"));
            }
            solarSystemMap.put(solarSystem.getSolarSystemID(), solarSystem);
        }
        return solarSystemMap;
    }

    private <K, V> Map<K, V> parseMultipleObjectsFromYaml(InputStream yamlFileInputStream, TypeReference<Map<K, V>> type) throws IOException {
        YAMLParser yamlParser = ((YAMLFactory) yamlObjectMapper.getFactory()).createParser(yamlFileInputStream);
        return yamlObjectMapper.readValue(yamlParser, type);
    }

    private <T> T parseSingleObjectFromYaml(InputStream yamlFileInputStream, TypeReference<T> type) throws IOException {
        YAMLParser yamlParser = ((YAMLFactory) yamlObjectMapper.getFactory()).createParser(yamlFileInputStream);
        return yamlObjectMapper.readValue(yamlParser, type);
    }
}