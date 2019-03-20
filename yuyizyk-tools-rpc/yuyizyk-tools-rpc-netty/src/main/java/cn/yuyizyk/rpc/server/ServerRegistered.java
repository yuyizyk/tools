package cn.yuyizyk.rpc.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import cn.yuyizyk.rpc.ConfigProperties;
import cn.yuyizyk.rpc.core.RSRegister;
import cn.yuyizyk.rpc.filter.RServerFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author yuyi
 */
@Slf4j
// @Component
// @ConditionalOnExpression("#{ erc.rpc.enable-server != false }")
public class ServerRegistered implements ApplicationRunner, DisposableBean {
	private ConfigProperties properties;
	private Map<String, RSRegister> registers;
	private SpringServiceFinder finder = new SpringServiceFinder();
	@Autowired
	private RpcServer rpcServer;
	private List<String> servernames = new ArrayList<>();
	private List<RServerFilter> rServerFilters = new ArrayList<>();
	private ConfigurableListableBeanFactory f;

	public void setConfigurableListableBeanFactory(ConfigurableListableBeanFactory f) {
		finder.setFindServerCallBack(servernames::add);
		finder.setFindFilterCallBack(rServerFilters::add);
		f.addBeanPostProcessor(finder);
		this.f = f;
	}

	public void setProperties(ConfigProperties properties2) {
		this.properties = properties2;
		this.rpcServer = new RpcServer(properties.getLocalhostPort(), finder::getObj);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			log.info("RPCServer start...");
			if (servernames.isEmpty()) {
				log.info("canel register RPCServer . THE  servernames size is 0.");
				return;
			}
			rpcServer.getInvokerHandler().addFilters(rServerFilters);
			rpcServer.run();
			registers = f.getBeansOfType(RSRegister.class);
			registers.values().forEach(RSRegister::register);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Override
	public void destroy() throws Exception {
		rpcServer.destroy();

	}

	public ServerRegistered() {
	}

}
