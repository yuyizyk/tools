package cn.yuyizyk.rpc.client;

import cn.yuyizyk.rpc.core.IGetterRouting;
import cn.yuyizyk.rpc.core.IGetterRoutingFactory;
import cn.yuyizyk.rpc.core.RPCServerName;
import cn.yuyizyk.tools.common.entity.DoubleEntity;
import cn.yuyizyk.tools.common.entity.SimpleEntry;

public class LocalGetterRoutingFactory implements IGetterRoutingFactory {

	@Override
	public IGetterRouting getObject(SimpleEntry<RPCServerName, Class<?>> simpleEntry) throws Exception {
		return () -> DoubleEntity.builder("127.0.0.1", 8080);
	}

}
