package org.ibs.cds.gode.entity.cache.ignite;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.*;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.springdata20.repository.config.EnableIgniteRepositories;
import org.ibs.cds.gode.entity.cache.MarkCacheRepo;
import org.ibs.cds.gode.entity.cache.KeyId;
import org.ibs.cds.gode.entity.cache.condition.IgniteCacheEnabler;
import org.ibs.cds.gode.entity.cache.CacheableEntity;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.ibs.cds.gode.util.CacheUtil;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Configuration
@Slf4j
@Conditional(IgniteCacheEnabler.class)
@PropertySource(GodeAppEnvt.GODE_PROPERTIES)
@EnableIgniteRepositories(basePackages = {GodeAppEnvt.ENTITY_BASE_PACKAGE_NAME}, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = MarkCacheRepo.class)
})
@EntityScan(GodeAppEnvt.ENTITY_BASE_PACKAGE_NAME)
public class IgniteCacheConfiguration {

    private static final String GODE_APP = "gode_app";
    private static final String CACHE_WAL_STORE = "cache/walStore";
    private static final String CACHE_WAL_ARCHIVE = "cache/walArchive";
    private static final String CACHE_STORE = "cache/store";
    private static final String GODE_DATA_REGION = "gode_data_region";

    @Autowired
    private Environment envt;

    @Bean
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration config = new IgniteConfiguration();
        String appName = envt.getProperty("gode.cache.ignite.name", GODE_APP);
        config.setIgniteInstanceName(appName);
        config.setPeerClassLoadingEnabled(true);
        config.setGridLogger(new Slf4jLogger());
        config.setClientMode(false);

        boolean persistenceEnabled = envt.getProperty("gode.cache.ignite.persistence", Boolean.class, false);
        log.debug("Ignite peristence enabled: {}", persistenceEnabled);
        if (persistenceEnabled) {
            DataStorageConfiguration dataStorageConfig = new DataStorageConfiguration();
            dataStorageConfig.setStoragePath( CACHE_STORE);
            dataStorageConfig.setWalArchivePath(CACHE_WAL_ARCHIVE);
            dataStorageConfig.setWalPath(CACHE_WAL_STORE);
            dataStorageConfig.setPageSize(envt.getProperty("gode.cache.ignite.persistence.pagesize", Integer.class));
            DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
            dataRegionConfiguration.setName(GODE_DATA_REGION);
            dataRegionConfiguration.setInitialSize(envt.getProperty("gode.cache.ignite.dataregion.size.start", Integer.class));
            dataRegionConfiguration.setMaxSize(envt.getProperty("gode.cache.ignite.dataregion.size.max", Integer.class));
            dataRegionConfiguration.setPersistenceEnabled(true);
            dataStorageConfig.setDataRegionConfigurations(dataRegionConfiguration);
            config.setConsistentId(appName);
            config.setDataStorageConfiguration(dataStorageConfig);
        }

        ConnectorConfiguration connectorConfiguration = new ConnectorConfiguration();
        connectorConfiguration.setPort(envt.getProperty("gode.cache.ignite.connector.port", Integer.class));
        config.setMetricsLogFrequency(envt.getProperty("gode.cache.ignite.metrics.log.frequency", Integer.class));
        config.setQueryThreadPoolSize(envt.getProperty("gode.cache.ignite.threads.pool.query", Integer.class));
        config.setDataStreamerThreadPoolSize(envt.getProperty("gode.cache.ignite.threads.pool.datastreamer", Integer.class));
        config.setManagementThreadPoolSize(envt.getProperty("gode.cache.ignite.threads.pool.management", Integer.class));
        config.setPublicThreadPoolSize(envt.getProperty("gode.cache.ignite.threads.pool.public", Integer.class));
        config.setSystemThreadPoolSize(envt.getProperty("gode.cache.ignite.threads.pool.system", Integer.class));
        config.setRebalanceThreadPoolSize(envt.getProperty("gode.cache.ignite.threads.pool.rebalance", Integer.class));
        config.setAsyncCallbackPoolSize(envt.getProperty("gode.cache.ignite.threads.pool.asynccallback", Integer.class));
        config.setPeerClassLoadingEnabled(false);
        config.setWorkDirectory(Paths.get(".").toAbsolutePath().normalize().toString().concat(File.separator).concat(appName));
        BinaryConfiguration binaryConfiguration = new BinaryConfiguration();
        binaryConfiguration.setCompactFooter(false);

        config.setBinaryConfiguration(binaryConfiguration);

        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
        tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(envt.getProperty("gode.cache.ignite.connector.discovery.addr")));
        tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
        config.setDiscoverySpi(new TcpDiscoverySpi());
        return config;
    }

    @Bean(destroyMethod = "close")
    public Ignite igniteInstance() {
        try {
            IgniteConfiguration config = igniteConfiguration();
            List<CacheConfiguration<?, ?>> caches = new ArrayList<>();
            Reflections reflections = new Reflections(GodeAppEnvt.ENTITY_TYPE_PACKAGE_NAME);
            log.debug("Ignite Scanning > {}", GodeAppEnvt.ENTITY_TYPE_PACKAGE_NAME);
            Set<Class<? extends CacheableEntity>> cacheableEntities = reflections.getSubTypesOf(CacheableEntity.class);
            log.debug("Cache candidates found: {} ", CollectionUtils.isNotEmpty(cacheableEntities));

            for (Class<? extends CacheableEntity> cacheableEntity : cacheableEntities) {
                Field[] fields = cacheableEntity.getDeclaredFields();
                Optional<Field> idField = Stream.of(fields).filter(field -> field.isAnnotationPresent(KeyId.class)).findAny();
                String simpleName = cacheableEntity.getSimpleName();
                if (idField.isPresent() && !cacheableEntity.isInterface()) {
                    createCache(caches, cacheableEntity, idField, simpleName);
                } else {
                    log.error("No cache initialisation for {}", simpleName);
                }
            }
            config.setCacheConfiguration(caches.toArray(new CacheConfiguration[0]));
            Ignite ignite = Ignition.start(config);
            ignite.cluster().active(true);
            return ignite;
        } catch (Throwable e) {
            throw KnownException.FAILED_TO_START.provide(e);
        }
    }

    private void createCache(List<CacheConfiguration<?, ?>> caches, Class<? extends CacheableEntity> cacheableEntity, Optional<Field> idField, String simpleName) {
        Field field = idField.get();
        String cacheName = CacheUtil.getCacheName(simpleName);
        CacheConfiguration<?, ?> cache = new CacheConfiguration<>(cacheName);
        log.debug("Ignite entity scan for {}", cacheName);
        cache.setIndexedTypes(field.getType(), cacheableEntity);
        log.debug("Ignite cache starting for {}", cacheableEntity.getSimpleName());
        if (simpleName.equals("counter")) {
            log.debug("Counter cache set to atomic persistence");
            cache.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        }
        cache.setDataRegionName(GODE_DATA_REGION);
        cache.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_ASYNC);
        caches.add(cache);
    }
}
