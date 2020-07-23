package org.ibs.cds.gode.entity.store.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.ibs.cds.gode.entity.store.MarkElasticSearchRepo;
import org.ibs.cds.gode.entity.store.condition.ElasticSearchEnabler;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.ibs.cds.gode.system.GodeConstant;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@Configuration
@Conditional(ElasticSearchEnabler.class)
@PropertySource(GodeAppEnvt.GODE_PROPERTIES)
@EnableElasticsearchRepositories(basePackages = GodeConstant.ENTITY_BASE_PACKAGE_NAME,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = MarkElasticSearchRepo.class)
        }
)
@ComponentScan(basePackages = {GodeAppEnvt.ENTITY_BASE_PACKAGE_NAME})
@Slf4j
public class ElasticSearchStoreConfiguration {

    private final Environment environment;

    public ElasticSearchStoreConfiguration(Environment environment) {
        this.environment = environment;
    }

    public HttpHost[] getAddress() {
        String hosts = environment.getProperty("gode.datastore.elasticsearch.hosts");
        log.info("Search store host(s): {}", hosts);
        return Arrays.stream(hosts.split(",")).map(this::parseUrl).toArray(s -> new HttpHost[s]);
    }


    private HttpHost parseUrl(String url) {
        try {
            URL urlObj = new URL(url);
            return new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());
        } catch (MalformedURLException e) {
            throw KnownException.FAILED_TO_START.provide(e, "Failed to init elastic search hosts");
        }
    }

    public String getClusterName() {
        return environment.getProperty("gode.datastore.elasticsearch.cluster", "elasticsearch");
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        String username = environment.getProperty("gode.datastore.elasticsearch.user");
        String password = environment.getProperty("gode.datastore.elasticsearch.password");
        return  new RestHighLevelClient(getBuilder(username, password));
    }


    private RestClientBuilder getBuilder(String username, String password){
        if(username == null && password == null){
            return RestClient.builder(getAddress());
        }
        CredentialsProvider credentialsProvider =new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username, password));
        return RestClient
                .builder(getAddress())
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchTemplate() {
        try {
            return new ElasticsearchRestTemplate(client());
        } catch (Exception e) {
            throw KnownException.FAILED_TO_START.provide(e, "Failed to init elastic search");
        }
    }

}
