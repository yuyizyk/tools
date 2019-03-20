package cn.yuyizyk.rpc.server;

import java.util.function.Function;

import cn.yuyizyk.rpc.util.RpcDecoder;
import cn.yuyizyk.rpc.util.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * NETTY RPC SERVER
 * 
 * 
 *
 * @author yuyi
 */
@Slf4j
@Data
public class RpcServer {
	/** 注册端口 */
	private final int port;
	/** 处理器 */
	private final InvokerHandler invokerHandler;

	public RpcServer(int port, Function<String, Object> getterBeanByClz) {
		this.port = port;
		invokerHandler = new InvokerHandler(getterBeanByClz);
	}

	/** 同步启动 */
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			log.info(" START RPC SERVER .PORT:{} ", port);
			ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class).localAddress(port)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new RpcDecoder()).addLast(new RpcEncoder())
									// pipeline.addLast("encoder", new ObjectEncoder());
									// pipeline.addLast("decoder",
									// new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
									.addLast(invokerHandler);
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future = bootstrap.bind(port).sync();
			log.info(" 注册服务成功.PORT:{} ", port);
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("注册失败", e);
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public void run() throws Exception {
		new Thread(this::start).start();
	}

	public void destroy() throws Exception {
	}

}
