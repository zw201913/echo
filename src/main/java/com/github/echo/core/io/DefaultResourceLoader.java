package com.github.echo.core.io;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zouwei
 * @className DefaultResourceLoader
 * @date: 2021/6/7 下午11:32
 * @description:
 */
public class DefaultResourceLoader implements ResourceLoader {

    @Override
    public InputStream getResource(String path) throws IOException {
        if (StringUtils.startsWith(path, ClassPathResourceLoader.CLASSPATH_URL_PREFIX)) {
            return new ClassPathResourceLoader(this.getClass().getClassLoader()).getResource(path);
        }
        return new FileSystemResourceLoader().getResource(path);
    }
}
