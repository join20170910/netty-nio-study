package com.ws.st.tcp.server;

import com.ws.st.codec.MessageDecoder;
import com.ws.st.codec.config.BootstrapConfig;
import com.ws.st.tcp.handler.HearBeatHandler;
import com.ws.st.tcp.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JohnServer {
    private final static Logger logger = LoggerFactory.getLogger(JohnServer.class);
    private BootstrapConfig.TcpConfig tcpConfig;
    private ServerBootstrap serverBootstrap;

    public JohnServer(BootstrapConfig.TcpConfig tcpConfig) throws InterruptedException {
        this.tcpConfig = tcpConfig;
        EventLoopGroup mainGroup = new NioEventLoopGroup(tcpConfig.getBossThreadSize());
        NioEventLoopGroup subGroup = new NioEventLoopGroup(tcpConfig.getWorkThreadSize());
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 10240) // 服务端可连接队列大小
                .option(ChannelOption.SO_REUSEADDR, true) // 参数表示允许重复使用本地地址和端口
                .childOption(ChannelOption.TCP_NODELAY, true) // 是否禁用Nagle算法 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 保活开关2h没有数据服务端会发送心跳包
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageDecoder())
                                .addLast(new IdleStateHandler(0,0,10))
                                .addLast(new HearBeatHandler(tcpConfig.getHeardBeatTime()))
                        .addLast(new NettyServerHandler());

                    }
                });



    }
    public void start() throws InterruptedException {
        ChannelFuture sync = serverBootstrap.bind(tcpConfig.getTcpPort()).sync();
        sync.addListener(future -> {
            if (future.isSuccess()){
                logger.info("start server");
            }
        });
    }
}
