package org.ibs.cds.gode.entity.store.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.elasticsearch.client.RestHighLevelClient;
import org.ibs.cds.gode.entity.query.parse.ESQueryParser;
import org.ibs.cds.gode.entity.store.condition.ElasticSearchEnabler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Conditional(ElasticSearchEnabler.class)
@Data
public class ElasticSearchStoreRequirement {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final String type;
    private final ESQueryParser queryParser;

    @Autowired
    public ElasticSearchStoreRequirement(RestHighLevelClient client, ObjectMapper mapper, Environment environment){
        this.client = client;
        this.objectMapper = mapper;
        this.type = environment.getProperty("gode.datastore.elasticsearch.doctype");
        this.queryParser = new ESQueryParser();
    }
}
