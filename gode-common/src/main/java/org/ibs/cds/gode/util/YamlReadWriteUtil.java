package org.ibs.cds.gode.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class YamlReadWriteUtil {

    private final static ObjectMapper ymlMapper;

    static {
        ymlMapper = new ObjectMapper(new YAMLFactory());
    }

    public static <T> T read(URL url, Class<T> className) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return ymlMapper.readValue(url, className);
    }

    public static <T> T readResource(String location, Class<T> className) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return read(resolver.getResource(location).getURL(), className);
    }

    public static <T> T readFile(File file, Class<T> className) throws IOException {
        return ymlMapper.readValue(file, className);
    }

    public static <T> String toString(T object) throws IOException {
        return ymlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
