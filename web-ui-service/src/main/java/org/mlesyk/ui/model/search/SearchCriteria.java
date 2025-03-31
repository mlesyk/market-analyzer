package org.mlesyk.ui.model.search;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchCriteria {
    private String url;
    private SearchType searchType;
    private boolean active;
    private String marketItemName;
    private Long minProfit;
    private Long maxVolume;
    private Long maxJumpsSafeNS;
    private Long maxJumpsSafeLS;
    private Long maxJumpsSafe;
    private Long maxJumpsShort;
    private Long minProfitPerCubicMeter;
    private Long minProfitPerJumpSafe;
    private Long minProfitPerJumpShort;

    public SearchCriteria(SearchType searchType, String url) {
        this.searchType = searchType;
        this.url = url;
    }

    public SearchCriteria copyFilters(SearchCriteria searchCriteria) {
        this.marketItemName = searchCriteria.marketItemName;
        this.minProfit = searchCriteria.minProfit;
        this.maxVolume = searchCriteria.maxVolume;
        this.maxJumpsSafeNS = searchCriteria.maxJumpsSafeNS;
        this.maxJumpsSafeLS = searchCriteria.maxJumpsSafeLS;
        this.maxJumpsSafe = searchCriteria.maxJumpsSafe;
        this.maxJumpsShort = searchCriteria.maxJumpsShort;
        this.minProfitPerCubicMeter = searchCriteria.minProfitPerCubicMeter;
        this.minProfitPerJumpSafe = searchCriteria.minProfitPerJumpSafe;
        this.minProfitPerJumpShort = searchCriteria.minProfitPerJumpShort;
        return this;
    }

    public void reset() {
        this.active = false;
        this.marketItemName = "";
        this.minProfit = null;
        this.maxVolume = null;
        this.maxJumpsSafeNS = null;
        this.maxJumpsSafeLS = null;
        this.maxJumpsSafe = null;
        this.maxJumpsShort = null;
        this.minProfitPerCubicMeter = null;
        this.minProfitPerJumpSafe = null;
        this.minProfitPerJumpShort = null;
    }
}
