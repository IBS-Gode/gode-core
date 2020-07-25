package org.ibs.cds.gode.util;

import lombok.Data;
import org.ibs.cds.gode.pagination.Sortable;

@Data
public class APIArgument {
    private int pageSize;
    private int pageNumber;
    private Sortable.Type sortOrder = Sortable.Type.ASC;
    private String sortBy;
}
