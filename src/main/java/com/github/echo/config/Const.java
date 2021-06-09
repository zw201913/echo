package com.github.echo.config;

/**
 * @author zouwei
 * @className ConfigConst
 * @date: 2021/6/7 下午10:35
 * @description:
 */
public class Const {
    /** 默认配置文件 */
    public static final String DEFAULT_CONFIG_FILE = "classpath:config/server.properties";

    /** 服务器端口配置 */
    public static final String SERVER_PORT_KEY = "server.port";
    /** 服务器默认端口 */
    public static final int DEFAULT_SERVER_PORT = 8080;
    /** 工作线程数量配置 */
    public static final String SERVER_WORK_THREAD_KEY = "server.workThread";
    /** 默认工作线程数量 */
    public static final int DEFAULT_SERVER_WORK_THREAD = 8;
    /** 链接（已经完成三次握手的连接）等待队列配置 */
    public static final String SERVER_SO_BACKLOG_KEY = "server.soBacklog";
    /** 默认等待队列数量 */
    public static final int DEFAULT_SERVER_SO_BACKLOG = 50;
}
