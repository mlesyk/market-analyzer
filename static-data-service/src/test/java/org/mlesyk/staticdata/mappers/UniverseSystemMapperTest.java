package org.mlesyk.staticdata.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlesyk.staticdata.model.UniverseSystem;
import org.mlesyk.staticdata.model.mappers.UniverseSystemMapper;
import org.mlesyk.staticdata.model.mappers.UniverseSystemMapperImpl;
import org.mlesyk.staticdata.model.rest.UniverseSystemRestDTO;
import org.mlesyk.staticdata.model.yaml.UniverseSystemYamlDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UniverseSystemMapperImpl.class})
class UniverseSystemMapperTest {

    @Autowired
    UniverseSystemMapper universeSystemMapper;

    @Test
    void yamlDtoToEntityTest() {
        UniverseSystemYamlDTO universeSystemYamlDto = new UniverseSystemYamlDTO("systemName","regionName", "constellationName", 0.44, 123);
        UniverseSystem universeSystem = universeSystemMapper.yamlDtoToEntity(universeSystemYamlDto);

        assertThat(universeSystem.getConstellationName()).isEqualTo(universeSystemYamlDto.getConstellationName());
        assertThat(universeSystem.getRegionName()).isEqualTo(universeSystemYamlDto.getRegionName());
        assertThat(universeSystem.getSecurity()).isEqualTo(universeSystemYamlDto.getSecurity());
        assertThat(universeSystem.getSystemID()).isEqualTo(universeSystemYamlDto.getSolarSystemID());
        assertThat(universeSystem.getSystemName()).isEqualTo(universeSystemYamlDto.getSolarSystemName());
    }

    @Test
    void entityToRestDTOTest() {
        UniverseSystem universeSystem = new UniverseSystem("systemName","regionName", "constellationName", 0.44, 123);
        UniverseSystemRestDTO universeSystemRestDTO = universeSystemMapper.entityToRestDTO(universeSystem);

        assertThat(universeSystem.getConstellationName()).isEqualTo(universeSystemRestDTO.getConstellationName());
        assertThat(universeSystem.getRegionName()).isEqualTo(universeSystemRestDTO.getRegionName());
        assertThat(universeSystem.getSecurity()).isEqualTo(universeSystemRestDTO.getSecurity());
        assertThat(universeSystem.getSystemID()).isEqualTo(universeSystemRestDTO.getSystemID());
        assertThat(universeSystem.getSystemName()).isEqualTo(universeSystemRestDTO.getSystemName());
    }
}