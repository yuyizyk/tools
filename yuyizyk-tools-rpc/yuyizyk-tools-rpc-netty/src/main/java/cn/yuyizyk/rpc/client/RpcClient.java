package cn.yuyizyk.rpc.client;

import cn.yuyizyk.rpc.core.IGetterRouting;
import cn.yuyizyk.rpc.core.RequestFailedException;
import cn.yuyizyk.rpc.core.RpcRequest;
import cn.yuyizyk.rpc.core.RpcResponse;
import cn.yuyizyk.rpc.core.RequestFailedException.EType;
import cn.yuyizyk.tools.common.entity.DoubleEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC CLIENT 完成RPC客户端对资源的请求(底层)
 * 
 *
 * @author yuyi
 */
@Slf4j
public class RpcClient {

	/** 本次RPC会话的请求信息 */
	private RpcRequest request;
	/** 本次RPC会话的响应信息 */
	private RpcResponse response;
	/** 锁信号 */
	private final Object obj = new Object();
	/** 会话通信底层 */
	private final ClientInokerHandler cihandler;
	/** 会话路由信息 */
	private final IGetterRouting getterRouting;

	public RpcClient(IGetterRouting getterRouting, RpcRequest request, ClientInokerHandler cihandler) {
		this.getterRouting = getterRouting;
		this.request = request;
		this.cihandler = cihandler;
	}

	/** ClientInokerHandler 回调设置 */
	public void setRpcResponse(RpcResponse response) {
		this.response = response;
		synchronized (obj) {
			obj.notify();
		}
	}

	public RpcRequest getRequest() {
		return request;
	}

	/**
	 * 发送请求(同步请求)
	 * 
	 * @return
	 * @throws Exception
	 */
	public RpcResponse send() throws Exception {
		DoubleEntity<String, Integer> hostAndPort;
		int i = 0;
		do {
			hostAndPort = getterRouting.get();
			try {
				log.debug("REQ REMOTE :[ H:{},P:{} ],BODY:  {} ", hostAndPort.getEntity1(), hostAndPort.getEntity2(),
						request);
				cihandler.send(hostAndPort, this);
				synchronized (obj) {
					obj.wait(4000);
				}
			} catch (Exception e) {
				log.error("{}:{} ", e.getClass(), e.getMessage(), e);
				getterRouting.tagCurrentError();
			}
			// TODO
			i++;
		} while (response == null && i < 3);

		if (response == null) {
			log.error("超时熔断。");
			throw new RequestFailedException(EType.Demotion);
		}
		log.debug("RESP REMOTE :[ H:{},P:{} ],BODY:  {} ", hostAndPort.getEntity1(), hostAndPort.getEntity2(),
				response);
		return response;
	}

}
