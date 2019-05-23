package nia.chapter2.echoclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import java.util.Date;

/**
 * 继承SimpleChannelInboundHandler的ChannelHandler来处理业务，
 * 使用SimpleChannelInboundHandler替代ChannelInboundHandlerAdapter的原因是：
 *      1、ChannelInboundHandlerAdapter在处理完消息后需要负责释放资源
 *      2、SimpleChannelInboundHandler会在完成channelRead0后调用ByteBuf.release()来释放资源
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
public class EchoClientHandler
        extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 客户端连接服务器后被调用
     */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",
                CharsetUtil.UTF_8));
    }

    /**
     * 从服务器接收到数据后调用，
     * 在该方法完成后将调用ByteBuf.release()来释放资源。
     * @param byteBuf 接收到的byte数据
     */
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        System.out.println(new Date() + ": Client received: " + byteBuf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 发生异常时被调用
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
