package cn.yuyizyk.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import cn.yuyizyk.rpc.client.ClientDiscovery;
import cn.yuyizyk.rpc.server.ServerRegistered;

@PropertySource("classpath:application.yml")
@Configuration
@ConditionalOnClass({ ServerRegistered.class, ClientDiscovery.class })
@EnableConfigurationProperties(ConfigProperties.class)
// @AutoConfigureAfter()	
@ConditionalOnProperty(prefix = "erc.rpc", value = "enabled", matchIfMissing = true)
public class RpcAutoConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Autowired
	private ConfigProperties properties;
	private static ClientDiscovery clientDiscovery = new ClientDiscovery();
	private static ConfigurableListableBeanFactory f;
	private static ServerRegistered serverRegistered = new ServerRegistered();

	@Bean
	@ConditionalOnExpression("#{ '${erc.rpc.close-client:false}' == 'false' }")
	public ClientDiscovery clientDiscovery() {
		clientDiscovery.setProperties(properties);
		//ClientConsulRoute.init(properties);
		return clientDiscovery;
	}

	@Bean
	@ConditionalOnExpression("#{ '${erc.rpc.close-server:false}' == 'false' }")
	public ServerRegistered serverRegistered() {
		serverRegistered.setProperties(properties);
		return serverRegistered;
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		f = applicationContext.getBeanFactory();
		clientDiscovery.setConfigurableListableBeanFactory(f);
		serverRegistered.setConfigurableListableBeanFactory(f);
	}
}
