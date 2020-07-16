package org.ibs.cds.gode.http;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import java.util.Map;
import java.util.Set;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.http.configuration.RequestType;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author manugraj
 */
public class HttpInvokerWithBody implements HttpInvoke {

    private final HttpRequest request;

    protected HttpInvokerWithBody(HttpRequest request) {
        this.request = request;
    }

    @Override
    public HttpResult call(String url) {
        try {
            RequestType requestType = this.request.getRequest().getRequestType();

            HttpRequestWithBody partialApi = api(requestType, url);

            partialApi.headers(this.request.getRequest().getHeader());

            if (!CollectionUtils.isEmpty(this.request.getRequest().getQueryParams())) {
                partialApi.queryString(this.request.getRequest().getQueryParams());
            }

            if (!CollectionUtils.isEmpty(this.request.getRequest().getPathParams())) {
                Set<Map.Entry<String, String>> entrySet = this.request.getRequest().getPathParams().entrySet();
                for (Map.Entry<String, String> routeParam : entrySet) {
                    partialApi.routeParam(routeParam.getKey(), routeParam.getValue());
                }
            }
            partialApi.body(this.request.getRawRequest());
            return new HttpResult(partialApi.asJson(), this.request.getRequest().getResponse());

        } catch (Exception ex) {
            throw KnownException.HTTP_EXCEPTION.provide(ex);
        }
    }

    private HttpRequestWithBody api(RequestType requestType, String url) {
        switch (requestType) {
            case POST:
            default:
                return Unirest.post(url.concat(this.request.getRequest().getPath()));
            case PUT:
                return Unirest.put(url.concat(this.request.getRequest().getPath()));
            case PATCH:
                return Unirest.patch(url.concat(this.request.getRequest().getPath()));
            case DELETE:
                return Unirest.delete(url.concat(this.request.getRequest().getPath()));
        }
    }

}
