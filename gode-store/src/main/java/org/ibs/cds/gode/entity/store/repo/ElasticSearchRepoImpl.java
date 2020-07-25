package org.ibs.cds.gode.entity.store.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.ibs.cds.gode.entity.generic.Try;
import org.ibs.cds.gode.entity.query.QueryStore;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.entity.query.parse.QueryParser;
import org.ibs.cds.gode.entity.store.elasticsearch.ElasticSearchStoreRequirement;
import org.ibs.cds.gode.entity.type.ElasticSearchEntity;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.pagination.*;
import org.ibs.cds.gode.util.PageUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ElasticSearchRepoImpl<Entity extends ElasticSearchEntity<Id>,Id extends Serializable> implements ElasticSearchRepo<Entity,Id> {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final String type;
    private static final String WILD_CARD_QUERY_TMPL="*%s*";
    private final String indexName;
    private final Class<Entity> entityClass;
    private final QueryParser<SearchRequest> queryPaser;

    public ElasticSearchRepoImpl(ElasticSearchStoreRequirement elasticSearchStoreRequirement) {
        this.client = elasticSearchStoreRequirement.getClient();
        this.objectMapper = elasticSearchStoreRequirement.getObjectMapper();
        this.type = elasticSearchStoreRequirement.getType();
        TypeToken<Entity> typeToken = new TypeToken<Entity>(getClass()) { };
        this.entityClass = (Class<Entity>) typeToken.getRawType();
        this.indexName = resolveIndexName();
        this.queryPaser = elasticSearchStoreRequirement.getQueryParser();

    }

    protected String resolveIndexName() {
        try {
            return resolveIndexName(entityClass.getConstructor().newInstance());
        } catch (Exception e) {
            throw KnownException.FAILED_TO_START.provide(e);
        }
    }

    protected String resolveIndexName(Entity entity) {
        return entity.getIndexName();
    }

    public Entity save(Entity entity) throws IOException {
        Entity foundEntity = this.findById(entity.getId(), entity.getIndexName());
        if(foundEntity == null) return insert(entity);
        return update(entity);
    }

    public Entity insert(Entity entity) throws IOException {
        IndexRequest indexRequest = new IndexRequest(entity.getIndexName(), type, entity.getId().toString())
                .source(toMap(entity), XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT);
        return entity;
    }

    public Entity update(Entity entity) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(
                entity.getIndexName(),
                type,
                entity.getId().toString());
        updateRequest.doc(toMap(entity));
        client.update(updateRequest, RequestOptions.DEFAULT);
        return entity;

    }

    public Optional<Entity> findById(Id id) throws IOException {
        GetRequest getRequest = new GetRequest(getIndexName(), type, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();
        return MapUtils.isEmpty(resultMap) ? Optional.empty() : Optional.of(toEntity(resultMap));
    }

    @Override
    public List<Entity> findAllById(List<Id> id){
        return id.stream().map(i-> (Optional<Entity>)Try.code( (Id j)-> findById(j, indexName)).catchWith(KnownException.QUERY_FAILED).run(i)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public Entity findById(Id id, String indexName) throws IOException {
        GetRequest getRequest = new GetRequest(indexName, type, id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();
        return MapUtils.isEmpty(resultMap) ? null : toEntity(resultMap);
    }

    public PagedData<Entity> findAll(PageContext context) throws IOException {
        String indexName = getIndexName();
        flush(indexName);
        return findAll(QueryBuilders.matchAllQuery(), indexName, context);
    }

    public PagedData<Entity> findAny(String text,PageContext context) throws IOException {
        return text == null ? PageUtils.emptyPage() : findAll(wildCardQueryBuilder(text), getIndexName(), context);
    }

    public PagedData<Entity> findAll(String query, PageContext context) throws IOException {
        String indexName = getIndexName();
        return query == null ? this.findAll(indexName, context) : findAll(QueryBuilders.simpleQueryStringQuery(query), indexName, context);
    }

    @Override
    public String getIndexName() {
        return indexName;
    }

    @Override
    public Class<Entity> getEntityType() {
        return entityClass;
    }

    @Override
    public Optional<Entity> findByAppId(Long appId) {
        return Optional.empty();
    }

    @Override
    public Stream<Entity> findByActive(boolean active) throws IOException {
        return Stream.empty();
    }

    @Override
    public PagedData<Entity> findByActive(boolean active, PageContext pageable) throws IOException {
        return PageUtils.emptyPage();
    }

    @NotNull
    public QueryStringQueryBuilder wildCardQueryBuilder(String text) {
        return QueryBuilders.queryStringQuery(String.format(WILD_CARD_QUERY_TMPL,text.toLowerCase()));
    }

    public SearchResponse search(QueryBuilder query, PageContext context) throws IOException {
        return client.search(searchRequest(query, context), RequestOptions.DEFAULT);
    }

    @NotNull
    private SearchRequest searchRequest(QueryBuilder query, PageContext context) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(query)
                .from((context.getPageNumber() - 1) * context.getPageSize())
                .size(context.getPageSize())
                .trackTotalHits(true);
        if(CollectionUtils.isNotEmpty(context.getSortOrder())){
            context.getSortOrder().stream().forEach(sortOrder-> sourceBuilder.sort(sortOrder.getField(),fromSortType(sortOrder.getSortType())));
        }
        return new SearchRequest(getIndexName())
                .source(sourceBuilder);
    }

    private SortOrder fromSortType(Sortable.Type type){
        switch (type){
            case ASC: default: return SortOrder.ASC;
            case DESC: return SortOrder.DESC;
        }
    }

    private Map<String, Object> toMap(Entity entity) {
        return objectMapper.convertValue(entity, Map.class);
    }

    @SneakyThrows
    private Entity toEntity(Map<String, Object> map){
        return objectMapper.convertValue(map, getEntityType());
    }

    @SneakyThrows
    private Entity toEntity(String json){
        return objectMapper.readValue(json, getEntityType());
    }

    public PagedData<Entity> findAll(QueryBuilder query, String indexName, PageContext pageContext) throws IOException {
        SearchRequest searchRequest = searchRequest(query, pageContext);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getPage(query.toString(), pageContext, searchResponse);
    }

    @NotNull
    private PagedData<Entity> getPage(String query, PageContext pageContext, SearchResponse searchResponse) {
        List<Entity> result = new ArrayList<>();
        PagedData<Entity> pagedData = new PagedData<>();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            Entity entity = toEntity(hit.getSourceAsString());
            result.add(entity);
        }
        pagedData.setData(result);
        ResponsePageContext responsePageContext = new ResponsePageContext(pageContext);
        responsePageContext.setTotalCount(searchResponse.getHits().getTotalHits());
        pagedData.setContext(new QueryContext(responsePageContext, query));
        return pagedData;
    }

    private void flush(String indexName) throws IOException {
        String endPoint = String.join("/", indexName, "_flush");
        client.getLowLevelClient().performRequest(new Request("POST", endPoint));
    }

    @Override
    public PagedData<Entity> findAll(QueryConfig<Entity> queryConfig) {
        Pair<SearchRequest, PageContext> parsedValue = queryPaser.parse(queryConfig);
        try {
            return getPage(null, parsedValue.getRight(), client.search(parsedValue.getLeft(), RequestOptions.DEFAULT));
        } catch (IOException e) {
            throw KnownException.QUERY_FAILED.provide(e);
        }
    }
}