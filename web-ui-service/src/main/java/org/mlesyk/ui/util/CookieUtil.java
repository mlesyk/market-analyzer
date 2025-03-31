package org.mlesyk.ui.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mlesyk.ui.model.search.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@Slf4j
public class CookieUtil {

    private final ObjectMapper objectMapper;

    @Autowired
    public CookieUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String searchFormCookieEncode(SearchForm searchForm) {
        log.debug("searchFormCookieEncode searchForm " + searchForm);
        return jsonToBase64String(writeSearchForm(searchForm));
    }

    public SearchForm searchFormCookieDecode(String encodedString) {
        log.debug("searchFormCookieDecode encodedString " + encodedString);
        return readSearchForm(base64StringToJson(encodedString));
    }

    private String jsonToBase64String(String json) {
        log.debug("jsonToBase64String json " + json);

        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    private String base64StringToJson(String data) {
        log.debug("base64StringToJson data " + data);

        return new String(Base64.getDecoder().decode(data.getBytes()));
    }

    private SearchForm readSearchForm(String searchFormJson) {
        try {
            log.debug("readSearchForm searchFormJson " + searchFormJson);
            return objectMapper.readValue(searchFormJson, SearchForm.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String writeSearchForm(SearchForm searchForm) {
        try {
            log.debug("writeSearchForm searchForm " + searchForm);
            return objectMapper.writeValueAsString(searchForm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
