package org.mlesyk.staticdata.model.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketGroupYamlDTO {
    private Integer marketGroupID;
    private NameIDYamlDTO nameID;
    private Integer parentGroupID;
    private Boolean hasTypes;
    private List<MarketTypeYamlDTO> types;

    public List<MarketTypeYamlDTO> getTypes() {
        if (types == null) {
            types = new ArrayList<>();
        }
        return types;
    }
}