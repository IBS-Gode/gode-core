package org.ibs.cds.gode.entity.manager;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.ibs.cds.gode.counter.CounterGenerator;
import org.ibs.cds.gode.entity.repo.CounterCacheRepository;
import org.ibs.cds.gode.entity.type.Counter;
import org.ibs.cds.gode.util.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;
import org.ibs.cds.gode.entity.function.EntityFunctionBody;
import org.ibs.cds.gode.entity.function.EntityValidation;

@Service
@Slf4j
public class CounterManager extends PureEntityManager<Counter, String> implements CounterGenerator {

    private IgniteCache<String, Counter> cacheData;

    @Autowired
    public CounterManager(CounterCacheRepository repository, Ignite ignite) {
        super(null, repository);
        cacheData = ignite.getOrCreateCache(CacheUtil.getCacheName("counter"));
    }

    public BigInteger getNextValue(String context) {
        atomicIncrement(context);
        log.debug(LOG_TEMPLATE, "atomic increment", context);
        Counter counter = cacheData.get(context);
        log.debug(LOG_TEMPLATE, "atomic get", counter);
        return counter.getValue();
    }

    public BigInteger getCurrentValue(String context) {
        Counter counter = cacheData.get(context);
        log.debug(LOG_TEMPLATE, "atomic get", counter);
        return counter == null ? BigInteger.ZERO : counter.getValue();
    }

    @Override
    @Synchronized
    public boolean increment(String context) {
        try {
            Counter counter = cacheData.get(context);
            counter.setValue(counter.getValue().add(BigInteger.ONE));
            return this.save(counter) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Synchronized
    public boolean atomicIncrement(String context) {
        cacheData.invoke(context, (mutableEntry, objects) -> {
            Counter counter;
            if (mutableEntry.exists() && mutableEntry.getValue() != null) {
                counter = mutableEntry.getValue();
                counter.setValue(counter.getValue().add(BigInteger.ONE));
                setDefaultFields(counter);
                mutableEntry.setValue(counter);
            } else {
                counter = new Counter();
                counter.setContext(context);
                counter.setValue(BigInteger.ONE);
                setDefaultFields(counter);
                mutableEntry.setValue(counter);
            }
            return null;
        });
        return true;
    }

    @Override
    protected void setDefaultFields(Counter item) {
        Date now = new Date();
        if (item.getCreatedOn() == null) item.setCreatedOn(now);
        item.setUpdatedOn(now);
    }

    @Override
    public <Function extends EntityValidation<Counter>> Optional<Function> validationFunction() {
        return Optional.empty();
    }

    @Override
    public <Function extends EntityFunctionBody<Counter>> Optional<Function> processFunction() {
        return Optional.empty();
    }
}
