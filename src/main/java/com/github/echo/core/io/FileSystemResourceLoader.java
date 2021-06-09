package com.github.echo.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

/**
 * @author zouwei
 * @className FileSystemResourceReader
 * @date: 2021/6/7 下午10:43
 * @description:
 */
public class FileSystemResourceLoader implements ResourceLoader {

    @Override
    public InputStream getResource(String path) throws IOException {
        try {
            return Files.newInputStream(new File(path).toPath());
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }
}
