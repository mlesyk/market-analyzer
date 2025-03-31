package org.mlesyk.ui.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mlesyk.ui.client.ProfitableOrdersServiceClient;
import org.mlesyk.ui.model.ProfitableOrdersTypeIdGroupView;
import org.mlesyk.ui.model.ProfitableOrdersViewDTO;
import org.mlesyk.ui.model.search.SearchCriteria;
import org.mlesyk.ui.model.search.SearchForm;
import org.mlesyk.ui.model.search.SearchType;
import org.mlesyk.ui.security.EVEOAuth2User;
import org.mlesyk.ui.util.ColumnSorter;
import org.mlesyk.ui.util.CookieUtil;
import org.mlesyk.ui.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class OrdersController {

    private static final String DIRECT_SEARCH_PAGE = "directSearchPage";
    private static final String SEARCH_FORM = "searchForm";
    private static final String SEARCH_CRITERIA = "searchCriteria";
    private static final String ORDERS = "orders";
    private static final String FORWARD_ORDERS = "forward:/orders";
    private static final String GROUPED = "GROUPED";

    private final ProfitableOrdersServiceClient profitableOrdersServiceClient;
    private final OrderUtil orderUtil;
    private final CookieUtil cookieUtil;
    private final ColumnSorter columnSorter;

    @Autowired
    public OrdersController(ProfitableOrdersServiceClient profitableOrdersServiceClient, OrderUtil orderUtil, CookieUtil cookieUtil, ColumnSorter columnSorter) {
        this.profitableOrdersServiceClient = profitableOrdersServiceClient;
        this.orderUtil = orderUtil;
        this.cookieUtil = cookieUtil;
        this.columnSorter = columnSorter;
    }

    @GetMapping(value = {"/orders", "/"})
    public String getOrders(@AuthenticationPrincipal EVEOAuth2User user, Model model,
                            @ModelAttribute(SEARCH_CRITERIA) SearchCriteria searchCriteria,
                            @CookieValue(name = SEARCH_FORM, defaultValue = "") String searchForm,
                            @RequestParam(defaultValue = "false", required = false) String reset,
                            @RequestParam(defaultValue = GROUPED, required = false) String type,
                            HttpServletResponse response, HttpServletRequest request) {
        SearchForm searchFormFilter = !"".equals(searchForm) ? cookieUtil.searchFormCookieDecode(searchForm) : new SearchForm();
        String directSearchPage = (String) request.getAttribute(DIRECT_SEARCH_PAGE);
        if (reset.equals("true")) {
            SearchType searchType = SearchType.valueOf(type);
            searchFormFilter.getSearchCriteriaMap().get(searchType.name()).reset();
            updateCookie(response, searchFormFilter);
        }
        SearchCriteria newSearchCriteria;
        if (directSearchPage != null && !directSearchPage.isEmpty()) {
            newSearchCriteria = searchFormFilter.getSearchCriteriaMap().get(directSearchPage);
        } else {
            Optional<SearchCriteria> searchCriteriaOptional = searchFormFilter.getSearchCriteriaMap()
                    .values().stream()
                    .filter(e -> e.getSearchType().equals(SearchType.GROUPED) || e.getSearchType().equals(SearchType.RAW))
                    .filter(SearchCriteria::isActive).findFirst();
            newSearchCriteria = searchCriteriaOptional.orElseGet(() -> searchFormFilter.getSearchCriteriaMap().get(SearchType.GROUPED.name()));
        }
        model.addAttribute(SEARCH_CRITERIA, newSearchCriteria);
        if (newSearchCriteria.isActive()) {
            addTableData(searchFormFilter, newSearchCriteria, model);
        }
        return ORDERS;
    }

    @PostMapping("/orders")
    public String filterOrders(@AuthenticationPrincipal EVEOAuth2User user, Model model,
                               @ModelAttribute(SEARCH_CRITERIA) SearchCriteria searchCriteria,
                               @CookieValue(name = SEARCH_FORM, defaultValue = "") String searchForm,
                               HttpServletResponse response, HttpServletRequest request) {
        SearchForm searchFormFilter = !"".equals(searchForm) ? cookieUtil.searchFormCookieDecode(searchForm) : new SearchForm();

        String directSearchPage = (String) request.getAttribute(DIRECT_SEARCH_PAGE);
        if (directSearchPage != null && !directSearchPage.isEmpty()) {
            searchCriteria.setSearchType(SearchType.valueOf(directSearchPage));
        } else {
            SearchType searchType = searchCriteria.getSearchType();
            if (searchType.equals(SearchType.GROUPED)) {
                searchFormFilter.getSearchCriteriaMap().get(SearchType.RAW.name()).setActive(false);
            } else {
                searchFormFilter.getSearchCriteriaMap().get(SearchType.GROUPED.name()).setActive(false);
            }
        }

        SearchType searchType = searchCriteria.getSearchType();
        searchCriteria = searchFormFilter.getSearchCriteriaMap().get(searchType.name()).copyFilters(searchCriteria);
        searchCriteria.setActive(true);

        updateCookie(response, searchFormFilter);
        model.addAttribute(SEARCH_CRITERIA, searchCriteria);

        addTableData(searchFormFilter, searchCriteria, model);
        return ORDERS;
    }

    @PostMapping("/orders/raw")
    public String filterOrdersRaw(HttpServletRequest request) {
        request.setAttribute(DIRECT_SEARCH_PAGE, SearchType.RAW_DIRECT.name());
        return FORWARD_ORDERS;
    }

    @GetMapping("/orders/raw")
    public String getOrdersRaw(HttpServletRequest request) {
        request.setAttribute(DIRECT_SEARCH_PAGE, SearchType.RAW_DIRECT.name());
        return FORWARD_ORDERS;
    }

    @PostMapping("/orders/grouped")
    public String filterOrdersGrouped(HttpServletRequest request) {
        request.setAttribute(DIRECT_SEARCH_PAGE, SearchType.GROUPED_DIRECT.name());
        return FORWARD_ORDERS;
    }

    @GetMapping("/orders/grouped")
    public String getOrdersGrouped(HttpServletRequest request) {
        request.setAttribute(DIRECT_SEARCH_PAGE, SearchType.GROUPED_DIRECT.name());
        return FORWARD_ORDERS;
    }

    @PostMapping(value = "/orders_table")
    public String sendOrdersFragment(Model model,
                                     @RequestParam(defaultValue = "tableId", required = false) String tableId,
                                     @RequestParam(defaultValue = "name", required = false) String sortColumn,
                                     @RequestParam(defaultValue = "asc", required = false) String sortDirection,
                                     @CookieValue(name = SEARCH_FORM, defaultValue = "") String searchForm) {
        SearchType searchType = SearchType.valueOf(tableId.replace("table_", "").toUpperCase());
        SearchForm searchFormFilter = !"".equals(searchForm) ? cookieUtil.searchFormCookieDecode(searchForm) : new SearchForm();

        model.addAttribute("sortColumn", sortColumn);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        SearchCriteria activeSearchCriteria = searchFormFilter.getSearchCriteriaMap().get(searchType.name());
        model.addAttribute(SEARCH_CRITERIA, activeSearchCriteria);

        addTableData(searchFormFilter, activeSearchCriteria, model, true, sortColumn, sortDirection);

        if (searchType.equals(SearchType.RAW) || searchType.equals(SearchType.RAW_DIRECT)) {
            return "fragments/tables/orders_table :: orders_table_fragment";
        } else {
            return "fragments/tables/orders_table :: orders_grouped_table_fragment";
        }
    }

    private Map<String, Object> searchFiltersToWebParams(SearchForm searchForm, SearchType searchType) {
        if (searchForm == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> params = new HashMap<>();
        SearchCriteria searchCriteria = searchForm.getSearchCriteriaMap().get(searchType.name());
        if (searchCriteria != null) {
            if (searchCriteria.getMarketItemName() != null) {
                params.put("name", searchCriteria.getMarketItemName());
            }
            if (searchCriteria.getMaxJumpsSafeNS() != null) {
                params.put("maxJumpsSafeNS", searchCriteria.getMaxJumpsSafeNS());
            }
            if (searchCriteria.getMaxJumpsSafeLS() != null) {
                params.put("maxJumpsSafeLS", searchCriteria.getMaxJumpsSafeLS());
            }
            if (searchCriteria.getMaxJumpsSafe() != null) {
                params.put("maxJumpsSafe", searchCriteria.getMaxJumpsSafe());
            }
            if (searchCriteria.getMaxJumpsShort() != null) {
                params.put("maxJumpsShort", searchCriteria.getMaxJumpsShort());
            }
            if (searchCriteria.getMinProfit() != null) {
                params.put("minProfit", searchCriteria.getMinProfit());
            }
        }
        return params;
    }

    private List<ProfitableOrdersViewDTO> fetchProfitableOrders(Map<String, Object> params) {
        List<ProfitableOrdersViewDTO> profitableOrders = Arrays.asList(profitableOrdersServiceClient.findProfitableOrders(params));
        profitableOrders = profitableOrders.stream()
                .filter(e -> !e.getTotalJumps().equals(0) && !e.getTotalJumpsShortest().equals(0))
                .collect(Collectors.toList());
        return profitableOrders;
    }

    private List<ProfitableOrdersViewDTO> filter(SearchCriteria searchCriteria, List<ProfitableOrdersViewDTO> profitableOrders) {
        Long maxVolume = searchCriteria.getMaxVolume();
        if (maxVolume != null && maxVolume != 0) {
            profitableOrders = profitableOrders.stream().filter(e -> e.getOrderVolume() < maxVolume).collect(Collectors.toList());
        }
        Long minProfitPerCubicMeter = searchCriteria.getMinProfitPerCubicMeter();
        if (minProfitPerCubicMeter != null && minProfitPerCubicMeter != 0) {
            profitableOrders = profitableOrders.stream().filter(e -> e.getOrderProfitPerCubicMeter() > minProfitPerCubicMeter).collect(Collectors.toList());
        }
        Long minProfitPerJumpSafe = searchCriteria.getMinProfitPerJumpSafe();
        if (minProfitPerJumpSafe != null && minProfitPerJumpSafe != 0) {
            profitableOrders = profitableOrders.stream().filter(e -> e.getProfitPerJumpSafe() > minProfitPerJumpSafe).collect(Collectors.toList());
        }
        Long minProfitPerJumpShort = searchCriteria.getMinProfitPerJumpShort();
        if (minProfitPerJumpShort != null && minProfitPerJumpShort != 0) {
            profitableOrders = profitableOrders.stream().filter(e -> e.getProfitPerJumpShort() > minProfitPerJumpShort).collect(Collectors.toList());
        }
        return profitableOrders;
    }

    private void updateCookie(HttpServletResponse response, SearchForm searchForm) {
        Cookie cookie = new Cookie(SEARCH_FORM, cookieUtil.searchFormCookieEncode(searchForm));
        cookie.setMaxAge(3600); // Cookie expires after 1 hour
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void addTableData(SearchForm searchForm, SearchCriteria searchCriteria, Model model, boolean sort, String sortColumn, String sortDirection) {
        Map<String, Object> params = searchFiltersToWebParams(searchForm, searchCriteria.getSearchType());

        List<ProfitableOrdersViewDTO> profitableOrders = fetchProfitableOrders(params);
        profitableOrders = filter(searchCriteria, profitableOrders);
        switch (searchCriteria.getSearchType()) {
            case RAW, RAW_DIRECT:
                model.addAttribute(ORDERS, profitableOrders);
                break;
            case GROUPED, GROUPED_DIRECT:
            default:
                Map<ProfitableOrdersTypeIdGroupView, List<ProfitableOrdersViewDTO>> profitableOrdersGroupedByTypeId = orderUtil.getProfitableOrdersGroupedByTypeId(profitableOrders);
                if (sort) {
                    profitableOrdersGroupedByTypeId = columnSorter.sortTypeIdGroupView(sortColumn, sortDirection, profitableOrdersGroupedByTypeId);
                }
                model.addAttribute("ordersByTypeId", profitableOrdersGroupedByTypeId);
                break;
        }
    }

    private void addTableData(SearchForm searchForm, SearchCriteria searchCriteria, Model model) {
        addTableData(searchForm, searchCriteria, model, false, "", "");
    }

}
