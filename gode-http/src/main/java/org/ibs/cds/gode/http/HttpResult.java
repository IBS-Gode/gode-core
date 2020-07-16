package org.ibs.cds.gode.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.http.configuration.Result;

/**
 *
 * @author manugraj
 */
public class HttpResult {

    private final HttpResponse<JsonNode> rawResponse;
    private final ObjectMapper objectMapper;
    private List<Result> resultConfiguration;
    private final String rawResponseString;

    protected HttpResult(HttpResponse<JsonNode> rawResponse, List<Result> resultConfiguration) {
        this.rawResponse = rawResponse;
        this.objectMapper = new ObjectMapper();
        this.resultConfiguration = resultConfiguration;
        this.rawResponseString = readResponse();
    }

    public int getStatus() {
        return this.rawResponse.getStatus();
    }

    public String getStatusText() {
        return this.rawResponse.getStatusText();
    }

    public Map<String, List<String>> getHeaders() {
        return this.rawResponse.getHeaders();
    }

    public String rawResponse() {
        return this.rawResponseString;
    }

    public Map<String, Object> preparedResponse() {
        if (CollectionUtils.isNotEmpty(resultConfiguration)) {
            DocumentContext document = JsonPath.parse(rawResponseString);
            return this.resultConfiguration.stream()
                    .map(resultConf -> {
                        try {
                            var value = document.read("$.".concat(resultConf.getValueAt()), resultConf.getType() == null ? String.class : resultConf.getType().getClassType());
                            return value == null ? null : Pair.of(resultConf.getName(), value);
                        } catch (Exception e) {
                            return null;
                        }

                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));
        }
        return Collections.emptyMap();
    }

    private String readResponse() {
        try {
            return StringUtils.toEncodedString(rawResponse.getRawBody().readAllBytes(), Charset.defaultCharset());
        } catch (IOException ex) {
            throw KnownException.HTTP_EXCEPTION.provide(ex, "Result data could not retrived");
        }
    }

    public <T> T response(Class<T> classType) throws IOException {
        return this.objectMapper.readValue(rawResponseString, classType);
    }
}
