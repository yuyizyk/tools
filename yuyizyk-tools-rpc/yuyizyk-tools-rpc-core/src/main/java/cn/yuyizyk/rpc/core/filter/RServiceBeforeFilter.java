package cn.yuyizyk.rpc.core.filter;

import java.lang.reflect.Method;

import cn.yuyizyk.rpc.core.RPCIService;

/**
 * 
 * 服务端-服务开始前执行
 * 
 *
 * @author yuyi
 * @param <T>
 */
@FunctionalInterface
public interface RServiceBeforeFilter<T extends RPCIService> extends RPCServiceFilter<T> {
	public void before(Class<T> clz, Method method);

	/**
	 * 服务开始前执行
	 * 
	 * @param result
	 * @return
	 */
	public default Object beforeFilter(T service, Method method, Object... args) {
		return null;
	}
}
