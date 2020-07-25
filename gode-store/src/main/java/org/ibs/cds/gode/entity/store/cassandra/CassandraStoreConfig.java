package org.ibs.cds.gode.entity.store.cassandra;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.Session;
import org.ibs.cds.gode.entity.store.MarkCassandraRepo;
import org.ibs.cds.gode.entity.store.condition.CassandraStoreEnabler;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.ibs.cds.gode.system.GodeConstant;
import org.ibs.cds.gode.util.Assert;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.AbstractClusterConfiguration;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DataCenterReplication;
import org.springframework.data.cassandra.core.cql.session.DefaultSessionFactory;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleTupleTypeFactory;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.core.mapping.UserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.convert.CustomConversions;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@EnableTransactionManagement
@Conditional(CassandraStoreEnabler.class)
@EnableCassandraRepositories(basePackages = GodeConstant.ENTITY_BASE_PACKAGE_NAME,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = MarkCassandraRepo.class)
        })
@ComponentScan(GodeConstant.GODE_BASE_PACKAGE_NAME)
@PropertySource(GodeConstant.GODE_PROPERTIES)
@EntityScan(GodeConstant.ENTITY_BASE_PACKAGE_ALL)
public class CassandraStoreConfig extends AbstractClusterConfiguration implements BeanClassLoaderAware {

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


    public SchemaAction getSchemaAction() {
        return schemaAction == null ? SchemaAction.CREATE_IF_NOT_EXISTS :SchemaAction.valueOf(schemaAction);
    }

    @Override
    protected AuthProvider getAuthProvider() {
        return new PlainTextAuthProvider(username, password);
    }

    @Nullable
    private ClassLoader beanClassLoader;


    protected Session getRequiredSession() {
        CassandraSessionFactoryBean factoryBean = this.session();
        Session session = factoryBean.getObject();
        Assert.notNull("Session factory not initialized", session);
        return session;
    }

    @Bean
    public CassandraSessionFactoryBean session() {
        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(this.getRequiredCluster());
        session.setConverter(this.cassandraConverter());
        session.setKeyspaceName(this.getKeyspaceName());
        session.setSchemaAction(this.getSchemaAction());
        session.setStartupScripts(this.getStartupScripts());
        session.setShutdownScripts(this.getShutdownScripts());
        return session;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new DefaultSessionFactory(this.getRequiredSession());
    }

    @Bean
    public CassandraConverter cassandraConverter() {
        try {
            MappingCassandraConverter mappingCassandraConverter = new MappingCassandraConverter(this.cassandraMapping());
            mappingCassandraConverter.setCustomConversions(this.cassandraCustomConvertor());
            return mappingCassandraConverter;
        } catch (ClassNotFoundException var2) {
            throw new IllegalStateException(var2);
        }
    }

    @Bean
    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
        Cluster cluster = this.getRequiredCluster();
        UserTypeResolver userTypeResolver = new SimpleUserTypeResolver(cluster, this.getKeyspaceName());
        CassandraMappingContext mappingContext = new CassandraMappingContext(userTypeResolver, new SimpleTupleTypeFactory(cluster));
        Optional.ofNullable(this.beanClassLoader).ifPresent(mappingContext::setBeanClassLoader);
        mappingContext.setInitialEntitySet(this.getInitialEntitySet());
        CustomConversions customConversions = this.cassandraCustomConvertor();
        mappingContext.setCustomConversions(customConversions);
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.setCodecRegistry(cluster.getConfiguration().getCodecRegistry());
        return mappingContext;
    }

    @Bean
    public CustomConversions cassandraCustomConvertor() {
        return new CassandraCustomConversions(Collections.emptyList());
    }

    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
        return CassandraEntityClassScanner.scan(this.getEntityBasePackages());
    }

    @Bean
    public CassandraAdminTemplate cassandraTemplate() throws Exception {
        return new CassandraAdminTemplate(this.sessionFactory(), this.cassandraConverter());
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public String[] getEntityBasePackages() {
        return new String[]{GodeAppEnvt.ENTITY_TYPE_PACKAGE_NAME};
    }

    @Override
    protected boolean getMetricsEnabled() {
        return false;
    }

    @Override
    protected List<String> getStartupScripts() {

        return super.getStartupScripts();
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return List.of(getKeySpaceSpecification());
    }


    private CreateKeyspaceSpecification getKeySpaceSpecification() {
        CreateKeyspaceSpecification createKeySpaceSpec = CreateKeyspaceSpecification.createKeyspace(keySpace);
        createKeySpaceSpec.ifNotExists(true).withNetworkReplication(DataCenterReplication.of("dc1", 1L));
        return createKeySpaceSpec;
    }
}
