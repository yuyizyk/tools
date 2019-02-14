package cn.yuyizyk.rpc.core.filter;

import java.lang.reflect.Method;

import cn.yuyizyk.rpc.core.RPCIService;

/**
 * 服务端- 服务结束后执行
 * 
 * 
 *
 * @author yuyi
 */
@FunctionalInterface
public interface RServiceAfterFilter<T extends RPCIService> extends RPCServiceFilter<T> {

	public void after(Class<T> clz, Method method);

	/**
	 * 服务结束后执行 <br/>
	 * filter 先于 after
	 * 
	 * @param result
	 * @return
	 */
	public default Object afterFilter(T service, Method method, Object result, Object... args) {
		return result;
	}
}
