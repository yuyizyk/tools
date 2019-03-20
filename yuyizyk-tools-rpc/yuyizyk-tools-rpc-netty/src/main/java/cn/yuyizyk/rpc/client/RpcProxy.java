package cn.yuyizyk.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cn.yuyizyk.rpc.core.IGetterRouting;
import cn.yuyizyk.rpc.core.IGetterRoutingFactory;
import cn.yuyizyk.rpc.core.RIFuse;
import cn.yuyizyk.rpc.core.RPCServerName;
import cn.yuyizyk.rpc.core.RequestFailedException;
import cn.yuyizyk.rpc.core.RpcRequest;
import cn.yuyizyk.rpc.core.RpcResponse;
import cn.yuyizyk.rpc.core.RequestFailedException.EType;
import cn.yuyizyk.rpc.util.AnnotationGetter;
import cn.yuyizyk.tools.common.entity.Box;
import cn.yuyizyk.tools.common.entity.SimpleEntry;
import lombok.extern.slf4j.Slf4j;

/**
 * 生成客户端代理对象(将每一次对象调用方法都转化为RPC调用,透明RPC调用)
 * 
 * 
 *
 * @author yuyi
 */
@Slf4j
public class RpcProxy {

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> interfaceClass, ClientInokerHandler handler) throws Exception {
		SimpleEntry<RPCServerName, Class<?>> s = AnnotationGetter.getRPCAnnInfo(interfaceClass);
		Assert.notNull(s, interfaceClass + " Getter RPC Annon ERROR. ");
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {
					/** 熔断 */
					private Box<T> fuse;
					/** 路由器 */
					private IGetterRouting getterRouting = IGetterRoutingFactory.get().getObject(s);

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						RpcRequest request = new RpcRequest(); // 创建并初始化 RPC 请求
						request.setId(UUID.randomUUID().toString());
						request.setClassName(interfaceClass.getName());
						request.setMethodName(method.getName());
						request.setParameterTypes(method.getParameterTypes());
						request.setParameters(args);
						Object obj;
						RpcClient client = new RpcClient(getterRouting, request, handler); // 初始化 RPC 客户端
						try {
							do {
								RpcResponse response = client.send(); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
								if (StringUtils.isEmpty(response.getError())) {
									return response.getResult();
								} else {
									obj = error(interfaceClass, request, response, fuse);
									if (obj != null)
										return obj;
								}
							} while (true);
						} catch (RequestFailedException e) {
							obj = error(interfaceClass, request, null, fuse);
							if (obj != null)
								return obj;
							throw e;
						}

					}
				});
	}

	public static <T> Object error(Class<T> interfaceClass, RpcRequest request, RpcResponse response, Box<T> fuse) {
		SET_FUSE: synchronized (interfaceClass) {
			if (fuse == null) {
				fuse = new Box<>();
				Map<String, T> map = ClientDiscovery.getBeanFactory().getBeansOfType(interfaceClass);
				if (map.size() >= 2) {
					Optional<T> opt = (Optional<T>) map.values().stream()
							.filter(obj -> RIFuse.class.isAssignableFrom(obj.getClass())).findAny();
					if (opt.isPresent()) {
						fuse.setObj(opt.get());
						break SET_FUSE;
					}
				}
			}
		}
		if (fuse.getObj() != null) {
			try {
				log.info(" {}  ", fuse.getObj());
				Method m = fuse.getObj().getClass().getMethod(request.getMethodName(), request.getParameterTypes());
				return m.invoke(fuse.getObj(), request.getParameters());
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				log.error("", e);
			}
		}
		if (response != null && StringUtils.isNoneBlank(response.getErrorClz())) {
			log.error("clz:{} msg:{}", response.getErrorClz(), response.getError());
			try {
				Class<?> clz = Class.forName(response.getErrorClz());
				if (Throwable.class.isAssignableFrom(clz)) {
					Throwable t = (Throwable) clz.newInstance();
					throw new RequestFailedException(EType.Demotion, t);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				log.error("", e);
			}
			throw new RequestFailedException(EType.Demotion);
		}
		return null;
	}
}
