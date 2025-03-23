package org.mlesyk.staticdata.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRestDTO {
    private Integer id;
    private String name;
    private Double volume;
    private Integer groupId;
}
