package nia.chapter2.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import java.util.Date;

/**
 * channel handler必须继承ChannelInboundHandlerAdapter并且重写channelRead方法。
 * 不使用SimpleChannelInboundHandler的原因是：
 *   1、channelRead要返回相同的消息给客户端，在服务器执行完成写操作之前不能释放调用读取到的消息，
 *   2、因为写操作是异步的，一旦写操作完成后，Netty中会自动释放消息
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 在任何时候都会被调用来接收数据，此方法是必须要重写的
     * @param channelHandlerContext 通道上下文
     * @param msg 接收的数据消息
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ": Server received: " + byteBuf.toString(CharsetUtil.UTF_8));
        // 向客户端写入相同的内容
        channelHandlerContext.write(byteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 捕获服务器的异常.
     * 比如:客户端连接服务器后强制关闭，服务器会抛出"客户端主机强制关闭错误"
     * @param channelHandlerContext
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        // 发生异常后关闭ChannelHandlerContext
        channelHandlerContext.close();
    }
}
