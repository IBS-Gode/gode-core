package org.ibs.cds.gode.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ibs.cds.gode.entity.store.StoreType;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreEssential {
    private JavaArtifact entityAnnotation;
    private JavaArtifact entityType;
    private JavaArtifact repo;
    private JavaArtifact repoAnnotation;
    private JavaArtifact repository;
    private JavaArtifact idAnnotation;
    private StoreType type;
    private JavaArtifact customRepo;
    private List<JavaArtifact> customRepoRequirements;

    public Set<String> packages(){
        return Set.of(entityAnnotation.fqn(), entityType.fqn(), repo.fqn(), repository.fqn(), idAnnotation.fqn());
    }

    public boolean isJPA(){
        return type == StoreType.JPA;
    }

    public boolean isMongo(){
        return type == StoreType.MONGODB;
    }
    public boolean isCassandra(){
        return type == StoreType.CASSANDRA;
    }
    public boolean isElasticsearch(){
        return type == StoreType.ELASTICSEARCH;
    }

    public boolean hasCustomRepo(){
        return customRepo != null;
    }

}
