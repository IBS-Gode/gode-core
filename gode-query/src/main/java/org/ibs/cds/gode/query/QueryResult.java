package org.ibs.cds.gode.query;

import lombok.Data;

import java.util.List;

@Data
public class QueryResult<T> {

    private List<T> results;
}
