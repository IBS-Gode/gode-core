package org.ibs.cds.gode.entity.store.cassandra;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.PlainTextAuthProvider;
import org.ibs.cds.gode.entity.store.MarkCassandraRepo;
import org.ibs.cds.gode.entity.store.MarkJPARepo;
import org.ibs.cds.gode.entity.store.condition.CassandraStoreEnabler;
import org.ibs.cds.gode.entity.store.condition.JPAStoreEnabler;
import org.ibs.cds.gode.system.GodeConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Conditional(CassandraStoreEnabler.class)
@EnableJpaRepositories(basePackages = GodeConstant.ENTITY_BASE_PACKAGE_NAME,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = MarkCassandraRepo.class)
        })
@ComponentScan(GodeConstant.GODE_BASE_PACKAGE_NAME)
@PropertySource(GodeConstant.GODE_PROPERTIES)
@EntityScan(GodeConstant.ENTITY_BASE_PACKAGE_ALL)
public class CassandraStoreConfig extends AbstractCassandraConfiguration {

    @Value("${gode.datastore.cassandra.contact-points:placeholder}")
    private String contactPoints;

    @Value("${gode.datastore.cassandra.port:0000}")
    private int port;

    @Value("${gode.datastore.cassandra.keyspace:placeholder}")
    private String keySpace;

    @Value("${gode.datastore.cassandra.username}")
    private String username;

    @Value("${gode.datastore.cassandra.password}")
    private String password;

    @Value("${gode.datastore.cassandra.schema-action}")
    private String schemaAction;

    @Override
    protected String getKeyspaceName() {
        return keySpace;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.valueOf(schemaAction);
    }

    @Override
    protected AuthProvider getAuthProvider() {
        return new PlainTextAuthProvider(username, password);
    }
}
