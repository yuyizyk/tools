package cn.yuyizyk.rpc.client;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import cn.yuyizyk.rpc.core.RpcResponse;
import cn.yuyizyk.rpc.util.RpcDecoder;
import cn.yuyizyk.rpc.util.RpcEncoder;
import cn.yuyizyk.tools.common.entity.DoubleEntity;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * NETTY 通信底层采用连接池进行复用.
 * 
 * 
 *
 * @author yuyi
 */
@Slf4j
public class ClientInokerHandler implements InitializingBean, DisposableBean {
	private final static BlockingQueue<RpcResponse> publicBoxQueue = new LinkedBlockingQueue<>();
	private final static Map<String, RpcClient> mapper = new ConcurrentHashMap<>();
	private boolean runflag = true;
	private final EventLoopGroup group = new NioEventLoopGroup();
	private final Thread thread = new Thread(() -> {
		RpcResponse response;
		while (runflag) {
			try {
				response = publicBoxQueue.take();
				RpcClient rc = mapper.get(response.getId());
				if (rc == null) {
					continue;
				}
				rc.setRpcResponse(response);
				mapper.remove(response.getId());
			} catch (Exception e) {
				log.error("", e);
			}
		}
	});

	public void afterPropertiesSet() {
		thread.start();
	}

	public void destroy() {
		runflag = false;
		group.shutdownGracefully();
	}

	/**
	 * 同步发送请求
	 * 
	 * @param hostAndPort 目前路径
	 * @param rpcClient   请求实体
	 * @throws Exception
	 */
	public void send(DoubleEntity<String, Integer> hostAndPort, RpcClient rpcClient) throws Exception {
		mapper.put(rpcClient.getRequest().getId(), rpcClient);
		ChannelFuture future = null;
		try {
			future = pool.borrowObject(hostAndPort);
			future.channel().writeAndFlush(rpcClient.getRequest()).sync();
		} finally {
			if (future != null)
				pool.returnObject(hostAndPort, future);
		}

	}

	private final GenericKeyedObjectPool<DoubleEntity<String, Integer>, ChannelFuture> pool = new GenericKeyedObjectPool<>(
			new ClientKeyedPooledObjectFactory(), new GenericKeyedObjectPoolConfig<ChannelFuture>() {
				{
					setMaxWaitMillis(3000);// 空闲获取超时时间
					setSoftMinEvictableIdleTimeMillis(1000 * 60 * 30L);// 对象最小的空间时间 在超出最小空闲数量(minIdle)时进行移除超时连接
					setTestOnReturn(false);
					// setMinIdle(1);
					setMinIdlePerKey(1);// 子池最小空闲
					setLifo(true);
					setMaxIdlePerKey(2);// 子池最大
					/** 支持jmx管理扩展 */
					setJmxEnabled(true);
					setJmxNamePrefix("ClientPoolProtocol");
					/** 保证获取有效的池对象 */
					setTestOnBorrow(true);
					setTestOnReturn(true);
				}
			});

	private final class ClientKeyedPooledObjectFactory
			extends BaseKeyedPooledObjectFactory<DoubleEntity<String, Integer>, ChannelFuture> {
		@Override
		public ChannelFuture create(DoubleEntity<String, Integer> key) throws Exception {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast(new RpcEncoder()) // 将 RPC 请求进行编码（为了发送请求）
							.addLast(new RpcDecoder()) // 将 RPC 响应进行解码（为了处理响应）
							.addLast(new CChannelInokerHandler()); // 发送 RPC 请求
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future = bootstrap.connect(key.getEntity1(), key.getEntity2()).sync();
			return future;
		}

		@Override
		public PooledObject<ChannelFuture> wrap(ChannelFuture value) {
			return new DefaultPooledObject<ChannelFuture>(value);
		}

	}

	private final class CChannelInokerHandler extends SimpleChannelInboundHandler<RpcResponse> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
			publicBoxQueue.put(response);

		}
	}

}
