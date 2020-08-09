package org.ibs.cds.gode.query;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embedded;
import java.util.Date;

@Data
@ToString
@NoArgsConstructor
public class Entity1 {

    private String eid;
    private boolean active;
    private Long appId;
    private Date createdOn;
    private Date updatedOn;
    private String name;
    @Embedded
    private IObj sample;
    private String test2;
}
