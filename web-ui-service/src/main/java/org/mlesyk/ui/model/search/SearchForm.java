package org.mlesyk.ui.model.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class SearchForm {
    private Map<String, SearchCriteria> searchCriteriaMap;

    public SearchForm() {
        this.searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchType.GROUPED.name(), new SearchCriteria(SearchType.GROUPED, "/orders"));
        searchCriteriaMap.put(SearchType.RAW.name(), new SearchCriteria(SearchType.RAW, "/orders"));
        searchCriteriaMap.put(SearchType.GROUPED_DIRECT.name(), new SearchCriteria(SearchType.GROUPED_DIRECT, "/orders/grouped"));
        searchCriteriaMap.put(SearchType.RAW_DIRECT.name(), new SearchCriteria(SearchType.RAW_DIRECT, "/orders/raw"));
    }
}
