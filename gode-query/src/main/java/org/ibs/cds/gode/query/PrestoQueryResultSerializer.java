package org.ibs.cds.gode.query;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.ibs.cds.gode.entity.query.exception.GodeQueryResultProcessException;

import java.io.IOException;
import java.sql.*;

public class PrestoQueryResultSerializer extends JsonSerializer<ResultSet> {

    @Override
    public Class<ResultSet> handledType() {
        return ResultSet.class;
    }

    @Override
    public void serialize(ResultSet rs, JsonGenerator jsonGenerator, SerializerProvider provider) throws GodeQueryResultProcessException {

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            String[] columnNames = new String[numColumns];
            int[] columnTypes = new int[numColumns];

            for (int i = 0; i < columnNames.length; i++) {
                columnNames[i] = rsmd.getColumnLabel(i + 1);
                columnTypes[i] = rsmd.getColumnType(i + 1);
            }

            jsonGenerator.writeStartArray();

            while (rs.next()) {

                boolean b;
                long l;
                double d;

                jsonGenerator.writeStartObject();

                for (int i = 0; i < columnNames.length; i++) {

                    jsonGenerator.writeFieldName(columnNames[i]);
                    switch (columnTypes[i]) {

                    case Types.INTEGER:
                        l = rs.getInt(i + 1);
                        if (rs.wasNull()) {
                            jsonGenerator.writeNull();
                        } else {
                            jsonGenerator.writeNumber(l);
                        }
                        break;

                    case Types.BIGINT:
                        l = rs.getLong(i + 1);
                        if (rs.wasNull()) {
                            jsonGenerator.writeNull();
                        } else {
                            jsonGenerator.writeNumber(l);
                        }
                        break;

                    case Types.DECIMAL:
                    case Types.NUMERIC:
                        jsonGenerator.writeNumber(rs.getBigDecimal(i + 1));
                        break;

                    case Types.FLOAT:
                    case Types.REAL:
                    case Types.DOUBLE:
                        d = rs.getDouble(i + 1);
                        if (rs.wasNull()) {
                            jsonGenerator.writeNull();
                        } else {
                            jsonGenerator.writeNumber(d);
                        }
                        break;

                    case Types.NVARCHAR:
                    case Types.VARCHAR:
                    case Types.LONGNVARCHAR:
                    case Types.LONGVARCHAR:
                        jsonGenerator.writeString(rs.getString(i + 1));
                        break;

                    case Types.BOOLEAN:
                    case Types.BIT:
                        b = rs.getBoolean(i + 1);
                        if (rs.wasNull()) {
                            jsonGenerator.writeNull();
                        } else {
                            jsonGenerator.writeBoolean(b);
                        }
                        break;

                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.LONGVARBINARY:
                        jsonGenerator.writeBinary(rs.getBytes(i + 1));
                        break;

                    case Types.TINYINT:
                    case Types.SMALLINT:
                        l = rs.getShort(i + 1);
                        if (rs.wasNull()) {
                            jsonGenerator.writeNull();
                        } else {
                            jsonGenerator.writeNumber(l);
                        }
                        break;

                    case Types.DATE:
                        provider.defaultSerializeDateValue(rs.getDate(i + 1), jsonGenerator);
                        break;

                    case Types.TIMESTAMP:
                        provider.defaultSerializeDateValue(rs.getTimestamp(i + 1), jsonGenerator);
                        break;

                    case Types.BLOB:
                        Blob blob = rs.getBlob(i);
                        provider.defaultSerializeValue(blob.getBinaryStream(), jsonGenerator);
                        blob.free();
                        break;

                    case Types.CLOB:
                        Clob clob = rs.getClob(i);
                        provider.defaultSerializeValue(clob.getCharacterStream(), jsonGenerator);
                        clob.free();
                        break;

                    case Types.ARRAY:
                        throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type ARRAY");

                    case Types.STRUCT:
                        throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type STRUCT");

                    case Types.DISTINCT:
                        throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type DISTINCT");

                    case Types.REF:
                        throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type REF");

                    case Types.JAVA_OBJECT:
                    default:
                        provider.defaultSerializeValue(rs.getObject(i + 1), jsonGenerator);
                        break;
                    }
                }

                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();

        } catch (SQLException | IOException e) {
            throw new GodeQueryResultProcessException(e);
        }
    }
}