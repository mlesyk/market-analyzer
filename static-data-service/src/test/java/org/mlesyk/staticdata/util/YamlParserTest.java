package org.mlesyk.staticdata.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlesyk.staticdata.config.YamlObjectMapperConfig;
import org.mlesyk.staticdata.model.yaml.MarketGroupYamlDTO;
import org.mlesyk.staticdata.model.yaml.MarketTypeYamlDTO;
import org.mlesyk.staticdata.model.yaml.UniverseSystemYamlDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {YamlParser.class, YamlObjectMapperConfig.class})
class YamlParserTest {

    @Autowired
    private YamlParser yamlReader;

    @Test
    void testParseMarketItemGroups() throws Exception {
        Path marketGroupsTest = Paths.get("src/test/resources/marketGroupsTest.yaml");
        byte[] data = Files.readAllBytes(marketGroupsTest);
        InputStream is = new ByteArrayInputStream(data);

        Map<Integer, MarketGroupYamlDTO> marketItemGroupInfoMap = yamlReader.parseMarketItemGroups(is);

        assertThat(marketItemGroupInfoMap).hasSize(3);

        assertThat(marketItemGroupInfoMap.get(712)).isNotNull();
        assertThat(marketItemGroupInfoMap.get(712).getNameID().getEn()).isEqualTo("Scan Probe Launchers");
        assertThat(marketItemGroupInfoMap.get(712).getParentGroupID()).isEqualTo(1708);
        assertThat(marketItemGroupInfoMap.get(712).getTypes()).isNotNull();
        assertThat(marketItemGroupInfoMap.get(712).getHasTypes()).isTrue();

        assertThat(marketItemGroupInfoMap.get(9)).isNotNull();
        assertThat(marketItemGroupInfoMap.get(9).getNameID().getEn()).isEqualTo("Ship Equipment");
        assertThat(marketItemGroupInfoMap.get(9).getParentGroupID()).isNull();
        assertThat(marketItemGroupInfoMap.get(9).getTypes()).isNotNull();
        assertThat(marketItemGroupInfoMap.get(9).getHasTypes()).isFalse();
    }

    @Test
    void testParseMarketItems() throws Exception {
        Path marketTypeIDsTest = Paths.get("src/test/resources/typeIDsTest.yaml");
        byte[] data = Files.readAllBytes(marketTypeIDsTest);
        InputStream is = new ByteArrayInputStream(data);

        Map<Integer, MarketTypeYamlDTO> marketItemIDsMap = yamlReader.parseMarketItems(is);

        assertThat(marketItemIDsMap).hasSize(4);

        assertThat(marketItemIDsMap.get(21591)).isNotNull();
        assertThat(marketItemIDsMap.get(21591).getName().getEn()).isEqualTo("Computer Chips");
        assertThat(marketItemIDsMap.get(21591).getMarketGroupID()).isEqualTo(1900);
        assertThat(marketItemIDsMap.get(21591).getVolume()).isEqualTo(0.1, withPrecision(0.01));

        assertThat(marketItemIDsMap.get(4260)).isNotNull();
        assertThat(marketItemIDsMap.get(4260).getName().getEn()).isEqualTo("Expanded Probe Launcher II");
        assertThat(marketItemIDsMap.get(4260).getMarketGroupID()).isEqualTo(712);
        assertThat(marketItemIDsMap.get(4260).getVolume()).isEqualTo(5.0, withPrecision(0.1));
    }

    @Test
    void testParseMarketGroupsWithMarketItemsBound() throws IOException {
        Path marketGroupsTest = Paths.get("src/test/resources/marketGroupsTest.yaml");
        byte[] marketGroupsData = Files.readAllBytes(marketGroupsTest);
        InputStream inputStreamMarketGroups = new ByteArrayInputStream(marketGroupsData);

        Path marketTypeIDsTest = Paths.get("src/test/resources/typeIDsTest.yaml");
        byte[] marketTypeIDsData = Files.readAllBytes(marketTypeIDsTest);
        InputStream inputStreamTypeIDs = new ByteArrayInputStream(marketTypeIDsData);

        Map<Integer, MarketGroupYamlDTO> marketItemGroupInfoMapBound = yamlReader.parseMarketGroupsWithMarketItemsBound(inputStreamMarketGroups, inputStreamTypeIDs);

        assertThat(marketItemGroupInfoMapBound.get(712)).isNotNull();
        assertThat(marketItemGroupInfoMapBound.get(712).getNameID().getEn()).isEqualTo("Scan Probe Launchers");
        assertThat(marketItemGroupInfoMapBound.get(712).getParentGroupID()).isEqualTo(1708);
        assertThat(marketItemGroupInfoMapBound.get(712).getTypes()).isNotNull();
        assertThat(marketItemGroupInfoMapBound.get(712).getHasTypes()).isTrue();

        assertThat(marketItemGroupInfoMapBound.get(712).getTypes()).hasSize(2);
        Optional<MarketTypeYamlDTO> marketTypeOpt1 = marketItemGroupInfoMapBound.get(712).getTypes().stream().filter(e -> e.getMarketItemID().equals(4258)).findFirst();
        assertThat(marketTypeOpt1).isPresent();
        MarketTypeYamlDTO marketType1 = marketTypeOpt1.get();
        assertThat(marketType1.getMarketItemID()).isEqualTo(4258);
        assertThat(marketType1.getMarketGroupID()).isEqualTo(712);
        assertThat(marketType1.getName().getEn()).isEqualTo("Core Probe Launcher II");

        Optional<MarketTypeYamlDTO> marketTypeOpt2 = marketItemGroupInfoMapBound.get(712).getTypes().stream().filter(e -> e.getMarketItemID().equals(4260)).findFirst();
        assertThat(marketTypeOpt2).isPresent();
        MarketTypeYamlDTO marketType2 = marketTypeOpt2.get();
        assertThat(marketType2.getMarketItemID()).isEqualTo(4260);
        assertThat(marketType2.getMarketGroupID()).isEqualTo(712);
        assertThat(marketType2.getName().getEn()).isEqualTo("Expanded Probe Launcher II");
    }

    @Test
    void testParseSolarSystems() throws IOException {
        Path solarSystemTest = Paths.get("src/test/resources/solarsystem.staticdata");
        byte[] solarSystemData = Files.readAllBytes(solarSystemTest);
        InputStream inputStreamSolarSystem = new ByteArrayInputStream(solarSystemData);
        Map<String, InputStream> testPair = new HashMap<String, InputStream>();
        testPair.put("sde\\fsd\\universe\\eve\\Delve\\XPJ1-6\\KFIE-Z\\solarsystem.staticdata", inputStreamSolarSystem);

        Map<Integer, UniverseSystemYamlDTO> systemEntityTest = yamlReader.readSolarSystemsFromYaml(testPair);

        Integer solarSystemIDExpected = 30004709;
        String solarSystemNameExpected = "KFIE-Z";
        String regionNameExpected = "Delve";
        String constellationNameExpected = "XPJ1-6";
        Double securityExpected = -0.003492582069175354;
        Double precision = 0.0000001;
        assertThat(systemEntityTest.get(30004709).getConstellationName()).isEqualTo(constellationNameExpected);
        assertThat(systemEntityTest.get(30004709).getRegionName()).isEqualTo(regionNameExpected);
        assertThat(systemEntityTest.get(30004709).getSolarSystemName()).isEqualTo(solarSystemNameExpected);
        assertThat(systemEntityTest.get(30004709).getSolarSystemID()).isEqualTo(solarSystemIDExpected);
        assertThat(systemEntityTest.get(30004709).getSecurity()).isEqualTo(securityExpected, withPrecision(precision));
    }

}
