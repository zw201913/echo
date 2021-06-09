package com.github.echo.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zouwei
 * @className ResourceReader
 * @date: 2021/6/7 下午10:41
 * @description:
 */
public interface ResourceLoader {

    InputStream getResource(String path) throws IOException;
}
