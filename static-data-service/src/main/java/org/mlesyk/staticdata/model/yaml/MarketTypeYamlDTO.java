package org.mlesyk.staticdata.model.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketTypeYamlDTO {
    private Integer marketItemID;
    private Integer marketGroupID;
    private NameIDYamlDTO name;
    private Double volume;
}
