package org.mlesyk.staticdata.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemNameToIdDTO {
    private Integer id;
    private String name;
}
