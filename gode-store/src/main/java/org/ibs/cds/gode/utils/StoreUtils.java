package org.ibs.cds.gode.utils;

import org.ibs.cds.gode.entity.store.StoreType;

import java.util.List;

public class StoreUtils {

    private static final String REPO_PATH = "org.ibs.cds.gode.entity.store.repo";
    private static final String ENTITY_PATH = "org.ibs.cds.gode.entity.type";

    public static StoreEssential essential(StoreType type){
        return  essential(type, false, false, false);
    }

    public static StoreEssential essential(StoreType type, boolean cacheable, boolean asyncStore, boolean cacheAsyncStore){

        switch (type){
            case JPA:
                return new StoreEssential(
                        new JavaArtifact("Entity", "javax.persistence"),
                        new JavaArtifact("JPAEntity", ENTITY_PATH),
                        new JavaArtifact("JPAEntityRepo", REPO_PATH),
                        new JavaArtifact("MarkJPARepo","org.ibs.cds.gode.entity.store"),
                        new JavaArtifact("JPAEntityRepository",REPO_PATH),
                        new JavaArtifact("Id","javax.persistence"),
                        type,
                        null,
                        null
                );
            case MONGODB:
                return new StoreEssential(
                        new JavaArtifact("Document", "org.springframework.data.mongodb.core.mapping"),
                        new JavaArtifact("MongoEntity", ENTITY_PATH),
                        new JavaArtifact("MongoEntityRepo", REPO_PATH),
                        new JavaArtifact("MarkMongoRepo","org.ibs.cds.gode.entity.store"),
                        new JavaArtifact("MongoEntityRepository",REPO_PATH),
                        new JavaArtifact("Id","org.springframework.data.annotation"),
                        type,
                        null,
                        null
                );
            case CASSANDRA:
                return new StoreEssential(
                        new JavaArtifact("Table", "org.springframework.data.cassandra.core.mapping"),
                        new JavaArtifact("CassandraEntity", ENTITY_PATH),
                        new JavaArtifact("CassandraEntityRepo", REPO_PATH),
                        new JavaArtifact("MarkCassandraRepo","org.ibs.cds.gode.entity.store"),
                        new JavaArtifact("CassandraEntityRepository",REPO_PATH),
                        new JavaArtifact("PrimaryKey","org.springframework.data.cassandra.core.mapping"),
                        type,
                        null,
                        null
                );
            case ELASTICSEARCH:
                return new StoreEssential(
                        new JavaArtifact("Document", "org.springframework.data.elasticsearch.annotations"),
                        new JavaArtifact("ElasticSearchEntity", ENTITY_PATH),
                        new JavaArtifact("ElasticSearchRepo", REPO_PATH),
                        new JavaArtifact("MarkElasticSearchRepo","org.ibs.cds.gode.entity.store"),
                        new JavaArtifact("ElasticSearchRepository",REPO_PATH),
                        new JavaArtifact("Id","org.springframework.data.annotation"),
                        type,
                        new JavaArtifact("ElasticSearchRepoImpl", REPO_PATH),
                        List.of(new JavaArtifact("ElasticSearchStoreRequirement", "org.ibs.cds.gode.entity.store.elasticsearch"))
                );
        }
        return  null;
    }
}
