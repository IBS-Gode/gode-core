package org.ibs.cds.gode.entity.store;

public enum StoreType implements IStoreType {
    JPA,
    MONGODB,
    CASSANDRA,
    NEO4J,
    ELASTICSEARCH
}
