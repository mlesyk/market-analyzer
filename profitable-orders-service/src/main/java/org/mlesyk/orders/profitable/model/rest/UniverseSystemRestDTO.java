package org.mlesyk.orders.profitable.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UniverseSystemRestDTO {
    private String systemName;
    private String regionName;
    private String constellationName;
    private Double security;
    private Integer systemID;
}
