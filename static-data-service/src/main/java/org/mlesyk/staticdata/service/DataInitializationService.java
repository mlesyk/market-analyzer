package org.mlesyk.staticdata.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.staticdata.exception.DataInitializationException;
import org.mlesyk.staticdata.model.Item;
import org.mlesyk.staticdata.model.ItemGroup;
import org.mlesyk.staticdata.model.mappers.ItemGroupMapper;
import org.mlesyk.staticdata.model.mappers.UniverseSystemMapper;
import org.mlesyk.staticdata.repository.ItemGroupRepository;
import org.mlesyk.staticdata.repository.SystemRepository;
import org.mlesyk.staticdata.util.Healthcheck;
import org.mlesyk.staticdata.util.StaticDataFileLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class DataInitializationService {

    private final ItemGroupMapper itemGroupMapper;
    private final UniverseSystemMapper universeSystemMapper;
    private final YamlService yamlService;
    private final SystemRepository systemRepository;
    private final ItemGroupRepository itemGroupRepository;
    private final StaticDataFileLoader staticDataFileLoader;
    private final Healthcheck healthcheck;

    @Autowired
    public DataInitializationService(ItemGroupMapper itemGroupMapper, UniverseSystemMapper universeSystemMapper, YamlService yamlService, SystemRepository systemRepository, ItemGroupRepository itemGroupRepository, StaticDataFileLoader staticDataFileLoader, Healthcheck healthcheck) {
        this.itemGroupMapper = itemGroupMapper;
        this.universeSystemMapper = universeSystemMapper;
        this.yamlService = yamlService;
        this.systemRepository = systemRepository;
        this.itemGroupRepository = itemGroupRepository;
        this.staticDataFileLoader = staticDataFileLoader;
        this.healthcheck = healthcheck;
    }

    public void initializeData() {
        downloadSDEArchive();
        loadSystemsData();
        loadMarketItemsData();
    }

    private void downloadSDEArchive() {
        try {
            if (!staticDataFileLoader.sdeExists()) {
                log.info("Downloading SDE");
                staticDataFileLoader.downloadNewSDE();
            }
            healthcheck.setSdeDownloaded(true);
        } catch (IOException | URISyntaxException e) {
            throw new DataInitializationException("Failed to download SDE data", e);
        }
    }

    private void loadSystemsData() {
        try {
            if (systemRepository.count() == 0) {
                log.info("Filling UniverseSystem database");
                systemRepository.saveAll(universeSystemMapper.yamlDtoListToEntityList(new ArrayList<>(yamlService.getAllSolarSystems().values())));
            }
            healthcheck.setSystemRepositoryInitialized(true);
        } catch (IOException e) {
            throw new DataInitializationException("Failed to load systems data", e);
        }
    }

    private void loadMarketItemsData() {
        try {
            if (itemGroupRepository.count() == 0) {
                log.info("Filling Market Items database");
                Collection<ItemGroup> values = itemGroupMapper.yamlDtoListToEntityList(new ArrayList<>(yamlService.getAllMarketItemGroupsWithMarketItems().values()));
                for (ItemGroup group : values) {
                    for (Item item : group.getItems()) {
                        item.setGroup(group);
                    }
                }
                itemGroupRepository.saveAll(values);
            }
            healthcheck.setItemGroupRepositoryInitialized(true);
        } catch (IOException e) {
            throw new DataInitializationException("Failed to load items data", e);
        }
    }
}