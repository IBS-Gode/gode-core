package org.ibs.cds.gode.entity.repo;

import org.ibs.cds.gode.entity.cache.repo.CacheableRepository;
import org.ibs.cds.gode.entity.type.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CounterCacheRepository extends CacheableRepository<Counter, String, CounterCacheRepo> {

    @Autowired
    public CounterCacheRepository(CounterCacheRepo repo) {
        super(repo);
    }

    @Override
    public Optional<Counter> findByAppId(Long appId) {
        return Optional.empty();
    }
}
