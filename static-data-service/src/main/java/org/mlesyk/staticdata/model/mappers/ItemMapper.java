package org.mlesyk.staticdata.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mlesyk.staticdata.model.Item;
import org.mlesyk.staticdata.model.rest.ItemRestDTO;
import org.mlesyk.staticdata.model.yaml.MarketTypeYamlDTO;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(source = "marketItemID", target = "id")
    @Mapping(source = "volume", target = "volume")
    @Mapping(source = "name.en", target = "name")
    Item yamlDtoToEntity(MarketTypeYamlDTO marketTypeYamlDto);

    List<Item> yamlDtoListToEntityList(List<MarketTypeYamlDTO> marketTypeYamlDtoList);

    @Mapping(source = "group.id", target = "groupId")
    ItemRestDTO entityToRestDTO(Item item);

    List<ItemRestDTO> entityListToRestDTOList(List<Item> itemList);

    Map<Integer, ItemRestDTO> entityMapToRestDTOMap(Map<Integer, Item> itemMap);
}