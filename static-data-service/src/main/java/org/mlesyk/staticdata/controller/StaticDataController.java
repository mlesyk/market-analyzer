package org.mlesyk.staticdata.controller;

import org.mlesyk.staticdata.model.mappers.ItemGroupMapper;
import org.mlesyk.staticdata.model.mappers.ItemMapper;
import org.mlesyk.staticdata.model.mappers.UniverseSystemMapper;
import org.mlesyk.staticdata.model.rest.ItemGroupRestDTO;
import org.mlesyk.staticdata.model.rest.ItemNameToIdDTO;
import org.mlesyk.staticdata.model.rest.ItemRestDTO;
import org.mlesyk.staticdata.model.rest.UniverseSystemRestDTO;
import org.mlesyk.staticdata.service.StaticDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class StaticDataController {

    private final StaticDataService staticDataService;
    private final ItemGroupMapper itemGroupMapper;
    private final ItemMapper itemMapper;
    private final UniverseSystemMapper universeSystemMapper;

    @Autowired
    public StaticDataController(StaticDataService staticDataService, ItemGroupMapper itemGroupMapper, ItemMapper itemMapper, UniverseSystemMapper universeSystemMapper) {
        this.staticDataService = staticDataService;
        this.itemGroupMapper = itemGroupMapper;
        this.itemMapper = itemMapper;
        this.universeSystemMapper = universeSystemMapper;
    }

    @GetMapping(value = "/getItems", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer, ItemRestDTO> getAllItems() {
        return itemMapper.entityMapToRestDTOMap(staticDataService.getAllItems());
    }

    @GetMapping(value = "/getItemNameToIdList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemNameToIdDTO> getAllItemNameToIdList() {
        return staticDataService.findItemNameToIdDTO();
    }

    @GetMapping("/getItems/{itemId}")
    public ItemRestDTO getItemById(@PathVariable Integer itemId) {
        return itemMapper.entityToRestDTO(staticDataService.findItemById(itemId));
    }

    @GetMapping(value = "/getGroups", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer, ItemGroupRestDTO> getAllItemGroups() {
        return itemGroupMapper.entityMapToRestDTOMap(staticDataService.getAllItemGroups());
    }

    @GetMapping(value = "/getAllSystems", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer, UniverseSystemRestDTO> getAllSolarSystems() {
        return universeSystemMapper.entityMapToRestDTOMap(staticDataService.getAllSolarSystems());
    }

    @GetMapping(value = "/getSystems/{systemID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UniverseSystemRestDTO getSystemById(@PathVariable Integer systemID) {
        return universeSystemMapper.entityToRestDTO(staticDataService.findSystemById(systemID));
    }

    @GetMapping(value = "/getSystems", produces = MediaType.APPLICATION_JSON_VALUE)
    public UniverseSystemRestDTO[] getSystemsById(@RequestParam List<Integer> systemIDs) {
        return universeSystemMapper.entityListToRestDTOList(staticDataService.findSystemsById(systemIDs)).toArray(new UniverseSystemRestDTO[0]);
    }

    @GetMapping(value = "/getItemIds", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer[] getItemIds() {
        return staticDataService.getAllItems().keySet().toArray(new Integer[0]);
    }
}
