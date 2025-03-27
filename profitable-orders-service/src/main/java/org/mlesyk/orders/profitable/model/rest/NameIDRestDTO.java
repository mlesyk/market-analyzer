package org.mlesyk.orders.profitable.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NameIDRestDTO {
    private String ru;
    private String en;
}
