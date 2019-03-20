package cn.yuyizyk.rpc.client;

import cn.yuyizyk.rpc.core.IGetterRouting;
import cn.yuyizyk.rpc.core.RPCServerName;

/**
 * 路由
 * 
 * 
 *
 * @author yuyi
 */
@FunctionalInterface
public interface CRoute {
	public IGetterRouting getServiceAddres(RPCServerName sname, Class<?> sclz);
}
