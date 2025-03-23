package org.mlesyk.staticdata.model.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UniverseSystemYamlDTO {
    private String solarSystemName;
    private String regionName;
    private String constellationName;
    private Double security;
    private Integer solarSystemID;
}