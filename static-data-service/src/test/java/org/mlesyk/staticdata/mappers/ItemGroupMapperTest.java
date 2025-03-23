package org.mlesyk.staticdata.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlesyk.staticdata.model.Item;
import org.mlesyk.staticdata.model.ItemGroup;
import org.mlesyk.staticdata.model.mappers.ItemGroupMapper;
import org.mlesyk.staticdata.model.mappers.ItemGroupMapperImpl;
import org.mlesyk.staticdata.model.mappers.ItemMapperImpl;
import org.mlesyk.staticdata.model.rest.ItemGroupRestDTO;
import org.mlesyk.staticdata.model.yaml.MarketGroupYamlDTO;
import org.mlesyk.staticdata.model.yaml.MarketTypeYamlDTO;
import org.mlesyk.staticdata.model.yaml.NameIDYamlDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ItemGroupMapperImpl.class, ItemMapperImpl.class})
class ItemGroupMapperTest {

    @Autowired
    ItemGroupMapper itemGroupMapper;

    @Test
    void yamlDtoToEntityTest() {
        NameIDYamlDTO marketTypeName = new NameIDYamlDTO("ru_TEST", "en_TEST");
        MarketTypeYamlDTO marketTypeYamlDto = new MarketTypeYamlDTO(111, 222, marketTypeName, 99.99);
        NameIDYamlDTO marketGroupName = new NameIDYamlDTO("ru_TEST_GROUP", "en_TEST_GROUP");
        MarketGroupYamlDTO marketGroupYamlDto = new MarketGroupYamlDTO(222, marketGroupName, 2, true, Arrays.asList(marketTypeYamlDto));

        ItemGroup marketGroup = itemGroupMapper.yamlDtoToEntity(marketGroupYamlDto);

        assertThat(marketGroup.getId()).isEqualTo(marketGroupYamlDto.getMarketGroupID());
        assertThat(marketGroup.getParentGroupId()).isEqualTo(marketGroupYamlDto.getParentGroupID());
        assertThat(marketGroup.getName()).isEqualTo(marketGroupYamlDto.getNameID().getEn());
        assertThat(marketGroup.getHasItems()).isEqualTo(marketGroupYamlDto.getHasTypes());
        assertThat(marketGroup.getItems().get(0).getId()).isEqualTo(marketGroupYamlDto.getTypes().get(0).getMarketItemID());
        assertThat(marketGroup.getItems().get(0).getName()).isEqualTo(marketGroupYamlDto.getTypes().get(0).getName().getEn());
        assertThat(marketGroup.getItems().get(0).getVolume()).isEqualTo(marketGroupYamlDto.getTypes().get(0).getVolume());
    }

    @Test
    void entityToRestDTOTest() {
        Item item = new Item(111, 12.32, "testEn", null);
        ItemGroup itemGroup = new ItemGroup(123, 234, true, "testEnGroup", new ArrayList<>());
        itemGroup.getItems().add(item);
        item.setGroup(itemGroup);

        ItemGroupRestDTO marketGroupRestDTO = itemGroupMapper.entityToRestDTO(itemGroup);

        assertThat(itemGroup.getId()).isEqualTo(marketGroupRestDTO.getId());
        assertThat(itemGroup.getParentGroupId()).isEqualTo(marketGroupRestDTO.getParentID());
        assertThat(itemGroup.getHasItems()).isEqualTo(marketGroupRestDTO.getHasItems());
        assertThat(itemGroup.getName()).isEqualTo(marketGroupRestDTO.getName());
        assertThat(itemGroup.getItems().get(0).getId()).isEqualTo(marketGroupRestDTO.getItems().get(0).getId());
        assertThat(itemGroup.getItems().get(0).getVolume()).isEqualTo(marketGroupRestDTO.getItems().get(0).getVolume());
        assertThat(itemGroup.getItems().get(0).getName()).isEqualTo(marketGroupRestDTO.getItems().get(0).getName());
    }
}