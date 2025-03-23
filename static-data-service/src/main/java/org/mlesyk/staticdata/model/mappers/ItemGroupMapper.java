package org.mlesyk.staticdata.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mlesyk.staticdata.model.ItemGroup;
import org.mlesyk.staticdata.model.rest.ItemGroupRestDTO;
import org.mlesyk.staticdata.model.yaml.MarketGroupYamlDTO;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ItemMapper.class)
public interface ItemGroupMapper {

    @Mapping(source = "marketGroupID", target = "id")
    @Mapping(source = "nameID.en", target = "name")
    @Mapping(source = "parentGroupID", target = "parentGroupId")
    @Mapping(source = "hasTypes", target = "hasItems")
    @Mapping(source = "types", target = "items")
    ItemGroup yamlDtoToEntity(MarketGroupYamlDTO marketGroupYamlDto);

    List<ItemGroup> yamlDtoListToEntityList(List<MarketGroupYamlDTO> marketTypeYamlDtoList);

    @Mapping(source = "parentGroupId", target = "parentID")
    ItemGroupRestDTO entityToRestDTO(ItemGroup itemGroup);

    List<ItemGroupRestDTO> entityListToRestDTOList(List<ItemGroup> itemGroupList);

    Map<Integer, ItemGroupRestDTO> entityMapToRestDTOMap(Map<Integer, ItemGroup> itemGroupMap);
}