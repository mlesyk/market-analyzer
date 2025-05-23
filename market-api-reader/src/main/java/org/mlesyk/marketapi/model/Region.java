package org.mlesyk.marketapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

/**
 * https://esi.evetech.net/latest/universe/regions/10000001/?datasource=tranquility&language=en-us
 *
 * {
 *   "constellations": [
 *     20000001,
 *     20000002,
 *     20000003,
 *     20000004,
 *     20000005,
 *     20000006,
 *     20000007,
 *     20000008,
 *     20000009,
 *     20000010,
 *     20000011,
 *     20000012,
 *     20000013,
 *     20000014,
 *     20000015,
 *     20000016
 *   ],
 *   "description": "The Derelik region, sovereign seat of the Ammatar Mandate, became the shield to the Amarrian flank in the wake of the Minmatar Rebellion. Derelik witnessed many hostile exchanges between the Amarr and rebel forces as the latter tried to push deeper into the territory of their former masters. Having held their ground, thanks in no small part to the Ammatars' military efforts, the Amarr awarded the Ammatar with their own province. However, this portion of space shared borders with the newly forming Minmatar Republic as well as the Empire, and thus came to be situated in a dark recess surrounded by hostiles. \n\nGiven the lack of safe routes elsewhere, the local economies of this region were dependent on trade with the Amarr as their primary means of survival. The Ammatar persevered over many decades of economic stagnation and limited trade partners, and their determination has in recent decades been rewarded with an increase in economic prosperity. This harsh trail is a point of pride for all who call themselves Ammatar, and it has bolstered their faith in the Amarrian way to no end.",
 *   "name": "Derelik",
 *   "region_id": 10000001
 * }
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Region {

    private Integer[] constellations;
    private String description;
    private String name;
    private Integer region_id;

    @Override
    public String toString() {
        return "Region {" +
                "constellations=" + Arrays.toString(constellations) +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", region_id=" + region_id +
                '}';
    }
}
