package org.mlesyk.staticdata.model.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NameIDYamlDTO {
    private String ru;
    private String en;
}
