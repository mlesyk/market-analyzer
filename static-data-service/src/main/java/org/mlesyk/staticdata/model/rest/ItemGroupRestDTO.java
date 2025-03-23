package org.mlesyk.staticdata.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ItemGroupRestDTO {

    private Integer id;
    private String name;
    private Integer parentID;
    private Boolean hasItems;
    private List<ItemRestDTO> items;

    public ItemGroupRestDTO() {
        items = new ArrayList<>();
    }
}