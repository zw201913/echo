package com.github.echo;

import com.github.echo.config.Const;
import com.github.echo.core.io.DefaultResourceLoader;
import com.github.echo.core.io.ResourceLoader;
import com.github.echo.server.EchoServer;
import com.github.echo.server.EchoVersion;
import com.github.echo.util.CommandLineUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author zouwei
 * @className StartUp
 * @date: 2021/6/7 下午2:36
 * @description:
 */
@Slf4j
public class StartUp {

    private static final String ECHO_SERVER_NAME = "EchoServer";

    /**
     * 入库函数
     *
     * @param args
     */
    public static void main(String[] args) {
        start(args);
    }

    /**
     * 启动函数
     *
     * @param args
     */
    private static void start(String[] args) {
        // 创建EchoServer
        EchoServer server = createEchoServer(args);
        // 启动服务器
        server.start();
    }

    /**
     * 创建服务器对象
     *
     * @param args
     * @return
     */
    private static EchoServer createEchoServer(String[] args) {
        // 初始化配置
        Map<String, String> configuration = initConfig(args);
        return new EchoServer(configuration);
    }

    /**
     * 初始化配置
     *
     * @param args
     * @return
     */
    private static Map<String, String> initConfig(String[] args) {
        // 先设置版本号
        System.setProperty(EchoVersion.ECHO_VERSION_KEY, EchoVersion.CURRENTLY_VERSION);
        // 解析输入参数得到CommandLine
        CommandLine commandLine = parseCommandLine(args);
        // 获取配置文件路径
        String configFile = getConfigFilePath(commandLine);
        // 读取配置文件
        try {
            return readConfig(configFile, new DefaultResourceLoader());
        } catch (IOException e) {
            // 配置文件读取失败
            System.exit(-1);
        }
        return null;
    }

    /**
     * 读取配置文件
     *
     * @param configFile
     * @throws IOException
     */
    private static Map<String, String> readConfig(String configFile, ResourceLoader resourceLoader)
            throws IOException {
        InputStream configInputStream = resourceLoader.getResource(configFile);
        Properties properties = new Properties();
        properties.load(configInputStream);
        Map<String, String> configMap = Maps.newHashMap();
        for (Map.Entry<Object, Object> e : properties.entrySet()) {
            configMap.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
        }
        log.info("配置文件{}加载完成", configFile);
        return configMap;
    }

    /**
     * 获取配置文件路径
     *
     * @param commandLine
     * @return
     */
    private static String getConfigFilePath(CommandLine commandLine) {
        String configKey = "c";
        String configFile;
        // 指定配置文件
        if (commandLine.hasOption(configKey)) {
            configFile = commandLine.getOptionValue(configKey);
        } else {
            configFile = Const.DEFAULT_CONFIG_FILE;
        }
        return configFile;
    }

    /**
     * 解析命令行
     *
     * @param args
     * @return
     */
    private static CommandLine parseCommandLine(String[] args) {
        // 1.定义命令行参数
        Options options =
                CommandLineUtil.buildCommandLineOptions(
                        new Option("h", "help", false, "print help"),
                        new Option("c", true, "echo config file."));

        // 2.解析命令行
        CommandLine commandLine = CommandLineUtil.parse(options, args);
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        if (Objects.isNull(commandLine)) {
            hf.printHelp(ECHO_SERVER_NAME, options, true);
            // 说明解析失败了
            System.exit(-1);
        }
        // 打印帮助文档
        if (commandLine.hasOption("h")) {
            hf.printHelp(ECHO_SERVER_NAME, options, true);
            System.exit(0);
        }
        return commandLine;
    }
}
