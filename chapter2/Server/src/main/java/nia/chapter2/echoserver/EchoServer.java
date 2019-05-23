package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(65535).start();
    }

    public void start() throws Exception {
        // 创建NioEventLoopGroup对象来处理事件，如接受新连接、接收数据、写数据等等
        // 也可以使用OioServerSocketChannel
        EventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            // 创建ServerBootstrap实例来引导绑定和启动服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(nioEventLoopGroup)
                // 指定通道类型为NioServerSocketChannel
                .channel(NioServerSocketChannel.class)
                // 设置InetSocketAddress让服务器监听某个端口已等待客户端连接
                .localAddress(new InetSocketAddress(port))
                // 设置childHandler执行所有的连接请求
                // 指定连接后调用的ChannelHandler,入参为抽象的 ChannelInitializer
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new EchoServerHandler());
                    }
                });
            // 最后调用ServerBootstrap.bind() 方法来绑定服务器
            // 绑定服务器等待直到绑定完成，调用sync()方法会阻塞直到服务器完成绑定，然后服务器等待通道关闭
            // 因为使用sync()，所以关闭操作也会被阻塞
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            nioEventLoopGroup.shutdownGracefully().sync();
        }
    }
}
