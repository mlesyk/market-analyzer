package org.mlesyk.staticdata.service;

import org.mlesyk.staticdata.model.Item;
import org.mlesyk.staticdata.model.ItemGroup;
import org.mlesyk.staticdata.model.UniverseSystem;
import org.mlesyk.staticdata.model.rest.ItemNameToIdDTO;
import org.mlesyk.staticdata.repository.ItemGroupRepository;
import org.mlesyk.staticdata.repository.ItemRepository;
import org.mlesyk.staticdata.repository.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StaticDataService {

    private final ItemGroupRepository itemGroupRepository;
    private final ItemRepository itemRepository;
    private final SystemRepository systemRepository;

    @Autowired
    public StaticDataService(ItemGroupRepository itemGroupRepository, ItemRepository itemRepository, SystemRepository systemRepository) {
        this.itemGroupRepository = itemGroupRepository;
        this.itemRepository = itemRepository;
        this.systemRepository = systemRepository;
    }

    public Map<Integer, Item> getAllItems() {
        return Streamable.of(itemRepository.findAll()).stream().collect(Collectors.toMap(Item::getId, Function.identity()));
    }

    public Item findItemById(Integer id) {
        return itemRepository.findById(id).orElse(null);
    }

    public List<ItemNameToIdDTO> findItemNameToIdDTO() {
        return itemRepository.findAllItemNameToIdDTO();
    }

    public Map<Integer, ItemGroup> getAllItemGroups() {
        return Streamable.of(itemGroupRepository.findAll()).stream().collect(Collectors.toMap(ItemGroup::getId, Function.identity()));
    }

    public Map<Integer, UniverseSystem> getAllSolarSystems() {
        return Streamable.of(systemRepository.findAll()).stream().collect(Collectors.toMap(UniverseSystem::getSystemID, Function.identity()));
    }

    public UniverseSystem findSystemById(Integer id) {
        return systemRepository.findById(id).orElse(null);
    }

    public List<UniverseSystem> findSystemsById(@RequestParam List<Integer> ids) {
        return systemRepository.findUniverseSystemBySystemIDIn(ids);
    }
}

