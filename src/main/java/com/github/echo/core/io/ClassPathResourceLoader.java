package com.github.echo.core.io;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zouwei
 * @className ClassPathResourceLoader
 * @date: 2021/6/8 下午3:10
 * @description:
 */
public class ClassPathResourceLoader implements ResourceLoader {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    private final ClassLoader classLoader;

    public ClassPathResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public InputStream getResource(String path) throws IOException {
        if (StringUtils.startsWith(path, CLASSPATH_URL_PREFIX)) {
            path = StringUtils.substringAfter(path, CLASSPATH_URL_PREFIX);
        }
        return this.classLoader.getResourceAsStream(path);
    }
}
