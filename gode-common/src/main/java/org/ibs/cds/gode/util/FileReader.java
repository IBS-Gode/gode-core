package org.ibs.cds.gode.util;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 *
 * @author manugraj
 */
public class FileReader extends FileUtils{

    private static final PathMatchingResourcePatternResolver resolver;

    static {
        resolver = new PathMatchingResourcePatternResolver();
    }

    public static String readFile(String location) throws IOException {
       return StringUtils.toEncodedString(resolver.getResource(location).getInputStream().readAllBytes(), Charset.defaultCharset());
    }
}
