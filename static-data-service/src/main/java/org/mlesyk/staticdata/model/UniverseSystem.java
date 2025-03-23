package org.mlesyk.staticdata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "systems")
public class UniverseSystem {

    private String systemName;
    private String regionName;
    private String constellationName;
    private Double security;
    @Id
    private Integer systemID;
}

