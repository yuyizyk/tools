package cn.yuyizyk.rpc.client;


import cn.yuyizyk.rpc.core.RpcRequest;
import cn.yuyizyk.rpc.core.RpcResponse;
import cn.yuyizyk.tools.common.entity.DoubleEntity;
import lombok.extern.slf4j.Slf4j;
import sun.security.krb5.internal.crypto.EType;

/**
 * 
 * 
 *
 * @author yuyi
 */
@Slf4j
public class RpcClient {
	private final RpcRequest request;
	private final ClientInokerHandler cihandler;

	private final GetterRouting getterRouting;

	public RpcClient(GetterRouting getterRouting, RpcRequest request, ClientInokerHandler cihandler) {
		this.getterRouting = getterRouting;
		this.request = request;
		this.cihandler = cihandler;
	}

	public RpcRequest getRequest() {
		return request;
	}

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

	private final Object obj = new Object();

	public void setRpcResponse(RpcResponse response) {
		this.response = response;
		synchronized (obj) {
			obj.notify();
		}
	}

	private RpcResponse response;
}
