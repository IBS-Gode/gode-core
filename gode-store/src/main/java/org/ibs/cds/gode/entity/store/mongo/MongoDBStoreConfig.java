package org.ibs.cds.gode.entity.store.mongo;

import org.ibs.cds.gode.entity.store.MarkMongoRepo;
import org.ibs.cds.gode.entity.store.condition.MongoDBEnabler;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.ibs.cds.gode.system.GodeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


@Configuration
@Conditional(MongoDBEnabler.class)
@EnableMongoRepositories(basePackages = GodeConstant.ENTITY_BASE_PACKAGE_NAME,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = MarkMongoRepo.class)
        }
)
@ComponentScan(GodeConstant.GODE_BASE_PACKAGE_NAME)
@PropertySource(GodeAppEnvt.GODE_PROPERTIES)
@EntityScan(GodeConstant.ENTITY_BASE_PACKAGE_ALL)
public class MongoDBStoreConfig extends AbstractMongoClientConfiguration {

    private final Environment environment;

    @Autowired
    public MongoDBStoreConfig(Environment environment) {
        this.environment = environment;  
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString string = new ConnectionString(environment.getProperty("gode.datastore.mongodb.uri"));
        return MongoClients.create(string);
    }

    @Override
    protected String getDatabaseName() {
        return mongoClient().getDatabase(environment.getProperty("gode.datastore.mongodb.database.name")).getName();
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());

    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter(mongoDbFactory(), customConversions(), mongoMappingContext(customConversions()) ));
    }

    @Bean 
    public MongoTransactionManager mongoTransactionManager() {
        return new MongoTransactionManager(mongoDbFactory());
    }
}
