package org.ibs.cds.gode.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.query.QueryType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

public class PrestoQueryManager {
    private final static String TEMPLATE ="jdbc:presto://%s:%s";
    private final String jdbc ;
    private Properties properties;
    private ObjectMapper objectMapper;
    public PrestoQueryManager(Pair<String,String> serverPort,Pair<String,String>... properties){
        this.jdbc = String.format(TEMPLATE, serverPort.getKey(), serverPort.getValue());
        this.properties = new Properties();
        if(ArrayUtils.isNotEmpty(properties)){
            Arrays.stream(properties).forEach(k->this.properties.put(k.getKey(), k.getValue()));
        }
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new SimpleModule().addSerializer(new GodeQueryResultSerializer()));
    }


    public <T> String execute(QueryType store, String query, Class<T> classType) throws SQLException, JsonProcessingException {
        String url = jdbc.concat("/").concat(store.getConnector()).concat("/").concat("wynss_dev_r22");
        Connection connection = DriverManager.getConnection(url, properties);
        ResultSet resultSet = connection.createStatement().executeQuery(query);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("results", resultSet);
        return objectMapper.writeValueAsString(objectNode);
    }
}
