package cn.yuyizyk.rpc.client;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import cn.yuyizyk.rpc.core.RPCServerName;
import cn.yuyizyk.rpc.util.AnnotationGetter;
import cn.yuyizyk.tools.common.entity.SimpleEntry;

/**
 * 
 * 
 * @author yuyi
 * @param <T>
 */
public class RpcClientFactoryBean<T> implements FactoryBean<T> {
	private final Class<T> interfaceClz;
	private final ClientInokerHandler cihandler;

	public RpcClientFactoryBean(final Class<T> interfaceClz, ClientInokerHandler cihandler) {
		this.interfaceClz = interfaceClz;
		this.cihandler = cihandler;
	}

	@Override
	public T getObject() throws Exception {
		return RpcProxy.create(interfaceClz, cihandler);
	}

	public boolean isSingleton() {
		return true;
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClz;
	}

}
