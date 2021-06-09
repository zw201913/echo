package com.github.echo.server;

import com.github.echo.config.Const;
import com.github.echo.util.CastUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

/**
 * @author zouwei
 * @className EchoServer
 * @date: 2021/5/27 上午10:46
 * @description:
 */
public class EchoServer {

    private Map<String, String> configMap;

    public EchoServer(Map<String, String> configMap) {
        this.configMap = configMap;
    }

    public void start() {
        // 启动服务器
        start0();
    }

    private void start0() {
        /** 获取配置文件端口，如果没有设置，那么默认就是8080 */
        int port =
                CastUtil.castInt(
                        this.configMap.get(Const.SERVER_PORT_KEY), Const.DEFAULT_SERVER_PORT);
        /** 获取工作线程数量，默认8个 */
        int workThreadNum =
                CastUtil.castInt(
                        this.configMap.get(Const.SERVER_WORK_THREAD_KEY),
                        Const.DEFAULT_SERVER_WORK_THREAD);
        /**
         * 已完成三次握手连接等待队列大小
         */
        int serverSoBacklog =
                CastUtil.castInt(
                        this.configMap.get(Const.SERVER_SO_BACKLOG_KEY),
                        Const.DEFAULT_SERVER_SO_BACKLOG);

        EventLoopGroup mainGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(workThreadNum);
        try {
            new ServerBootstrap()
                    .group(mainGroup, workGroup)
                    // 指定所用的NIO传输channel，其实就是指定IO模式
                    .channel(NioServerSocketChannel.class)
                    // 绑定端口
                    .localAddress(port)
                    // 当工作线程全部占满时，用于临时存放已经完成三次握手连接的队列大小
                    .option(ChannelOption.SO_BACKLOG, serverSoBacklog)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 添加处理器
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel sc) throws Exception {

                                }
                            })
                    // 异步绑定服务器，阻塞直至绑定成功
                    .bind()
                    .sync()

                    // 获取channel的CloseFuture对象，并且阻塞线程直至程序结束，否则会一直监听指定端口
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (Exception e) {
            mainGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
