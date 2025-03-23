package org.mlesyk.staticdata.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mlesyk.staticdata.model.mappers.ItemGroupMapper;
import org.mlesyk.staticdata.model.mappers.ItemMapper;
import org.mlesyk.staticdata.model.mappers.UniverseSystemMapper;
import org.mlesyk.staticdata.model.rest.ItemRestDTO;
import org.mlesyk.staticdata.model.rest.UniverseSystemRestDTO;
import org.mlesyk.staticdata.service.StaticDataService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StaticDataControllerTest {

    @Mock
    private StaticDataService staticDataService;

    @Mock
    private ItemGroupMapper itemGroupMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UniverseSystemMapper universeSystemMapper;

    @InjectMocks
    private StaticDataController staticDataController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(staticDataController).build();
    }

    @Test
    void testGetAllItems() throws Exception {
        when(staticDataService.getAllItems()).thenReturn(Collections.emptyMap());
        when(itemMapper.entityMapToRestDTOMap(Collections.emptyMap())).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/getItems")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void testGetAllItemNameToIdList() throws Exception {
        when(staticDataService.findItemNameToIdDTO()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/getItemNameToIdList")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetItemById() throws Exception {
        Integer itemId = 1;
        ItemRestDTO itemRestDTO = new ItemRestDTO();
        when(staticDataService.findItemById(itemId)).thenReturn(null);
        when(itemMapper.entityToRestDTO(null)).thenReturn(itemRestDTO);

        mockMvc.perform(get("/getItems/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void testGetAllItemGroups() throws Exception {
        when(staticDataService.getAllItemGroups()).thenReturn(Collections.emptyMap());
        when(itemGroupMapper.entityMapToRestDTOMap(Collections.emptyMap())).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/getGroups")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void testGetAllSolarSystems() throws Exception {
        when(staticDataService.getAllSolarSystems()).thenReturn(Collections.emptyMap());
        when(universeSystemMapper.entityMapToRestDTOMap(Collections.emptyMap())).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/getAllSystems")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void testGetSystemById() throws Exception {
        Integer systemID = 1;
        UniverseSystemRestDTO universeSystemRestDTO = new UniverseSystemRestDTO();
        when(staticDataService.findSystemById(systemID)).thenReturn(null);
        when(universeSystemMapper.entityToRestDTO(null)).thenReturn(universeSystemRestDTO);

        mockMvc.perform(get("/getSystems/{systemID}", systemID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void testGetSystemsById() throws Exception {
        List<Integer> systemIDs = Collections.singletonList(1);
        when(staticDataService.findSystemsById(systemIDs)).thenReturn(Collections.emptyList());
        when(universeSystemMapper.entityListToRestDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/getSystems")
                .param("systemIDs", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetItemIds() throws Exception {
        when(staticDataService.getAllItems()).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/getItemIds")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
