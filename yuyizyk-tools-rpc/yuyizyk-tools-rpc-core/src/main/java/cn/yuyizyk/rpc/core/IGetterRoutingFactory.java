package cn.yuyizyk.rpc.core;

import cn.yuyizyk.tools.common.entity.SimpleEntry;

/**
 * 
 * IGetterRouting 工厂
 * 
 *
 * @author yuyi
 */
@FunctionalInterface
public interface IGetterRoutingFactory {
	public IGetterRouting getObject(SimpleEntry<RPCServerName, Class<?>> simpleEntry) throws Exception;

	public static IGetterRoutingFactory get() {
		return null;
	};
}
