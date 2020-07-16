package org.ibs.cds.gode.http;

import com.mashape.unirest.http.Unirest;
import org.ibs.cds.gode.exception.KnownException;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author manugraj
 */
public class HttpInvoker implements HttpInvoke{

    private HttpRequest request;

    protected HttpInvoker(HttpRequest request) {
        this.request = request;
    }

    public HttpResult call(String url) {
        try {
            var partialApi = Unirest.get(url.concat(this.request.getRequest().getPath()))
                    .headers(this.request.getRequest().getHeader());
            
            if(!CollectionUtils.isEmpty(this.request.getRequest().getQueryParams())){
                 partialApi.queryString(this.request.getRequest().getQueryParams());
            }
            
            if(!CollectionUtils.isEmpty(this.request.getRequest().getPathParams())){
                 this.request.getRequest().getPathParams().entrySet().forEach(pathParam -> partialApi.routeParam(pathParam.getKey(), pathParam.getValue()));
            }
                   
            return new HttpResult(partialApi.asJson(), this.request.getRequest().getResponse());
            
        } catch (Exception ex) {
            throw KnownException.HTTP_EXCEPTION.provide(ex);
        }
    }
}
