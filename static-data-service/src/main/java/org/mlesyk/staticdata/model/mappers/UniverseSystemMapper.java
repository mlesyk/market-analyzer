package org.mlesyk.staticdata.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mlesyk.staticdata.model.UniverseSystem;
import org.mlesyk.staticdata.model.rest.UniverseSystemRestDTO;
import org.mlesyk.staticdata.model.yaml.UniverseSystemYamlDTO;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UniverseSystemMapper {

    @Mapping(source = "solarSystemName", target = "systemName")
    @Mapping(source = "solarSystemID", target = "systemID")
    UniverseSystem yamlDtoToEntity(UniverseSystemYamlDTO universeSystemYamlDto);

    List<UniverseSystem> yamlDtoListToEntityList(List<UniverseSystemYamlDTO> universeSystemYamlDtoList);

    UniverseSystemRestDTO entityToRestDTO(UniverseSystem universeSystem);

    List<UniverseSystemRestDTO> entityListToRestDTOList(List<UniverseSystem> universeSystemList);

    Map<Integer, UniverseSystemRestDTO> entityMapToRestDTOMap(Map<Integer, UniverseSystem> universeSystemMap);
}
