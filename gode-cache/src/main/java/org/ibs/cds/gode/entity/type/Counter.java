package org.ibs.cds.gode.entity.type;

import lombok.Data;
import org.ibs.cds.gode.entity.cache.CacheEntity;
import org.ibs.cds.gode.entity.cache.KeyId;

import java.math.BigInteger;

@Data
public class Counter extends CacheEntity<String> {

    private @KeyId String context;

    private BigInteger value;

    @Override
    public String getId() {
        return context;
    }

    @Override
    public void setId(String context) {
        this.context = context;
    }
}
