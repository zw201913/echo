# Echo Server

a web server for java

### 2021年06月09号

今天开始提交第一份echo server代码，目前已经写了一个简单的读取、加载配置的功能了，并且把监听服务运行起来了。

做这个项目的初衷就是为了能利用这个项目实战一下netty框架，同时锻炼一下自己的设计能力。第一份代码已经写了几天了，一开始写了一个netty的服务端和客户端通信的模版代码，但却无法让我更进一步了解netty。所以就决定自己撸一个类似tomcat的web server来玩玩。

先大概总结一下这次提交的代码的思路吧：

结合看过的几个开源项目的源码，基本上启动初期就是获取配置，通过相关的配置来驱动程序的运行，tomcat也基本类似。

1.配置文件可以通过启动命令配置，类似：

```shell
java -jar -c 配置文件
```

如果没有在启动命令中配置，那么我们应该提供一个默认的配置文件。

2.目前针对于.properties格式的配置文件处理起来好上手，所以配置文件就先用这个格式。其他格式的配置文件后面再逐渐支持，毕竟我目前比较关注netty的使用。

一开始代码基本全部写在EchoServer这个类中，后面整理了一下，为了让EchoServer看起来更面向对象，目前就只给EchoServer留了一个配置对象，通过配置来创建EchoServer对象。那么就把读取、加载配置文件的工作放到StartUp类中，这样的话StartUp这个类就做了两件事情：

1.处理配置文件

2.通过配置创建EchoServer对象，并且启动EchoServer服务。

在第一步处理配置文件的时候，其实可以有三个入口去获取配置文件。第一个是jvm配置参数，第二个是命令行参数，第三个是环境变量。当然还有最后的默认配置文件，这个可以放在资源文件中。在目前提交的代码中，支持了命令行参数和默认配置文件。

1.从jvm参数中获取数据可以使用：

```
System.getProperty(String key);
```

2.解析命令行参数可以使用new DefaultParser()

```java
public static CommandLine parse(Options options, String[] args) {
  			// 创建解析器
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
          	// 解析args。里面会检查args是否符合options的要求
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            return null;
        }
        return commandLine;
    }
```

所以我们要提前准备好Options对象，也就是EchoServer所有可接受的输入命令，如下：

```java
// 定义命令行参数
Options options = CommandLineUtil.buildCommandLineOptions(
                        new Option("h", "help", false, "print help"),
                        new Option("c", true, "echo config file."));
```

目前暂时先定义两个，后续迭代会逐渐增加其他功能。

在使用DefaultParser处理命令行参数时，同时还get到了HelpFormatter的用法，这是一个打印帮助文档的工具。

```java
HelpFormatter hf = new HelpFormatter();
hf.printHelp(ECHO_SERVER_NAME, options, true);
```

通过两行代码就可以打印出所有的命令行参数使用方法

```shell
usage: EchoServer [-c <arg>] [-h]
 -c <arg>    echo config file.
 -h,--help   print help
```

3.从环境变量中获取参数需要使用：

```java
System.getenv(String name);
```

最终，我选择了从命令行中获取配置文件的路径，如果获取不到配置文件，那么直接从资源目录中找默认的配置文件。

在第一步处理配置文件的过程中，我们先处理命令行参数，通过DefaultParser把命令行参数解析成CommandLine。

我们需要从CommandLine中获取-c参数的值，如果拿到了-c参数的值的话，我们默认为这个值是一个绝对路径（目前还没有支持网络文件）。

如果取不到-c参数的值，那么我们就从classpath中取默认配置文件server.properties

```java
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
            // classpath:config/server.properties
            configFile = Const.DEFAULT_CONFIG_FILE;
        }
        return configFile;
    }
```

最终，通过拿到的配置文件路径，解析成map对象

```java
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
        return configMap;
    }
```

这里面需要注意的是，因为解析出来的配置文件可能是绝对路径，也可能是“classpath:”作为前缀的默认配置，将来还有其他来源的配置，所以我们这里需要通过不同的策略来处理。

```java
				// 读取配置文件
        try {
            return readConfig(configFile, new DefaultResourceLoader());
        } catch (IOException e) {
            // 配置文件读取失败
            System.exit(-1);
        }
```

通过一个统一的DefaultResourceLoader作为入口，在真正实现getResource()函数的时候，根据参数的特征分别调用具体的实现

```java
public class DefaultResourceLoader implements ResourceLoader {

    @Override
    public InputStream getResource(String path) throws IOException {
      	//如果以“classpath:”开头，那么就使用ClassPathResourceLoader实现
        if (StringUtils.startsWith(path, ClassPathResourceLoader.CLASSPATH_URL_PREFIX)) {
            return new ClassPathResourceLoader(this.getClass().getClassLoader()).getResource(path);
        }
      	// 否则，就直接用FileSystemResourceLoader实现
        return new FileSystemResourceLoader().getResource(path);
    }
}

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
```

至此，我们已经把配置文件读取到内存了，此时我们就可以根据配置来创建EchoServer对象

```java
		/**
     * 启动函数
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
```

EchoServer.java

```java
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
                                    // todo
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
```

这是目前提交的第一份代码，下一步准备给项目补充上log，这样才能更好地开发后续的功能。