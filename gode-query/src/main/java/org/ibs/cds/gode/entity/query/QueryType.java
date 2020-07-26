package org.ibs.cds.gode.entity.query;

import lombok.Getter;

public enum QueryType {
    RAW_SQL("raw_sql"),
    JPA("JPA"),
    MYSQL("mysql"),
    CASSANDRA("cassandra"),
    MONGODB("mongodb"),
    ELASTICSEARCH("elasticsearch");

    @Getter
    private String connector;

    QueryType(String connector) {
        this.connector = connector;
    }
}
