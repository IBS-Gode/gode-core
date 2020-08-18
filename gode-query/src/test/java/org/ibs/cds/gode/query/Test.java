package org.ibs.cds.gode.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.dsl.Query;
import org.ibs.cds.gode.entity.query.dsl.WhereClause;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.entity.query.model.QueryOperation;
import org.ibs.cds.gode.entity.query.parse.RawMongoQueryParser;
import org.ibs.cds.gode.entity.query.parse.RawSQLQueryParser;
import org.ibs.cds.gode.pagination.PageContext;
import org.springframework.data.mongodb.core.query.BasicQuery;

import java.sql.SQLException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws SQLException, JsonProcessingException {
        presto();
    }

    private static void query() {
        QueryConfig query = Query
                            .select("name", "mark")
                            .where("name", QueryOperation.eq, Operand.literal("1"))
                            .and(WhereClause.of("id", QueryOperation.eq, Operand.literal("1"))
                                    , WhereClause.of("std", QueryOperation.eq, Operand.field("name")))
                            .orderBy("id")
                            .desc()
                            .query(Entity.class);

        RawSQLQueryParser<Entity> parser = new RawSQLQueryParser();
        RawMongoQueryParser<Entity> mParser = new RawMongoQueryParser();

        Pair<String, PageContext> parsed = parser.doParse(query);
        Pair<BasicQuery, Entity> mongoParsed = mParser.doParse(query);
        System.out.println(query);
        System.out.println(parsed.getKey());
        System.out.println(mongoParsed.getKey());
    }

    public static void test(String... fields) {
        System.out.println(List.of(fields));
    }

    private static void presto() throws SQLException, JsonProcessingException {
        String query = "select * from entity1";
        PrestoQueryManager qm = new PrestoQueryManager(
                Pair.of("localhost", "8080"),
                Pair.of("user", "dev-user")
//                Pair.of("password", "dev-user")
        );
      List<Entity1> result = List.of(qm.execute(QueryType.MYSQL, query, Entity1[].class));
        result.forEach(System.out::println);
    }
}
