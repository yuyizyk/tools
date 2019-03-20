package cn.yuyizyk.rpc.client;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import cn.yuyizyk.rpc.ConfigProperties;
import cn.yuyizyk.rpc.core.RSRegister;
import cn.yuyizyk.rpc.core.RService;
import cn.yuyizyk.tools.common.cls.Clzs;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 客户端
 * 
 * 
 *
 * @author yuyi
 */
@Slf4j
public class ClientDiscovery implements InstantiationAwareBeanPostProcessor, InitializingBean, DisposableBean {
	private final ClientInokerHandler clientInokerHandler = new ClientInokerHandler();
	private ConfigProperties properties;
	private static ConfigurableListableBeanFactory beanFactory;

//	/**
//	 * 只设置带有RService 的远程接口
//	 */
//	@Override
//	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
//		if (beanClass.isAnnotationPresent(RService.class)) {
//		}
//		return null;
//	}

	// @Override
	// public Object postProcessBeforeInitialization(Object bean, String beanName)
	// throws BeansException {
	// if (bean instanceof RPCClientFactoryBean)
	// try {
	// return ((RPCClientFactoryBean) bean).getObject();
	// } catch (Exception e) {
	// log.error("",e);
	// }
	// return bean;
	// }

	public static ConfigurableListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setConfigurableListableBeanFactory(ConfigurableListableBeanFactory f) {
		f.addBeanPostProcessor(this);
		DefaultListableBeanFactory bf = (DefaultListableBeanFactory) (beanFactory = f);
		Clzs.getClzFromPkg("com.yytaliance.ercloud.api").forEach(c -> {
			if (c.isAnnotationPresent(RService.class)) {
				GenericBeanDefinition definition = new GenericBeanDefinition();
				ConstructorArgumentValues constr = new ConstructorArgumentValues();
				constr.addGenericArgumentValue(c);
				constr.addGenericArgumentValue(clientInokerHandler);
				definition.setConstructorArgumentValues(constr);
				definition.setBeanClass(RpcClientFactoryBean.class);
				// definition.setBeanClass(RpcProxy.create(c, () -> new
				// SimpleEntry<>("localhost", 8500)).getClass()); // 设置类
				definition.setScope("singleton"); // 设置scope
				definition.setLazyInit(true); // 设置是否懒加载
				definition.setAutowireCandidate(true); // 设置是否可以被其他对象自动注入
				bf.registerBeanDefinition(new StringBuilder().append(Character.toLowerCase(c.getSimpleName().charAt(0)))
						.append(c.getSimpleName().substring(1)).toString(), definition);
			}
		});

	}

	public ClientDiscovery setProperties(ConfigProperties properties2) {
		this.properties = properties2;
		return this;
	}

	public ClientDiscovery() {
	}

	public ClientDiscovery discovery() {
		return this;
	}

	@Override
	public void destroy() throws Exception {
		clientInokerHandler.destroy();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		clientInokerHandler.afterPropertiesSet();
	}

}
