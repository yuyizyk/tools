package cn.yuyizyk.rpc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 配置中心
 * 
 * 
 *
 * @author yuyi
 */
@Component
@ConfigurationProperties
public class ConfigProperties {

	@Value("${erc.discovery:consul}")
	private String config_discovery = "consul";
	@Value("${spring.cloud.consul.host:localhost}")
	private String[] consulHost;
	@Value("${erc.host:localhost}")
	private String[] ercHost;
	@Value("${spring.cloud.consul.port:8500}")
	private Integer[] consulport;
	@Value("${erc.port:8500}")
	private Integer[] ercPort;

	@Value("${spring.cloud.consul.discovery.ip-address:localhost}")
	private String consulLocalhostAddress;

	@Value("${erc.rpc.localhost.address:localhost}")
	private String ercLocalhostAddress;

	public String[] PivotHostname() {
		if (StringUtils.equals(config_discovery, "consul")) {
			return consulHost;
		} else
			return ercHost;
	}

	public Integer[] PivotPort() {
		if (StringUtils.equals(config_discovery, "consul")) {
			return consulport;
		} else
			return ercPort;
	}

	public String LocalhostAddress() {
		if (StringUtils.equals(config_discovery, "consul")) {
			return consulLocalhostAddress;
		} else
			return ercLocalhostAddress;
	}

	@Value("${erc.rpc.localhost.port:9900}")
	private Integer localhostPort = 9900;
	@Value("${erc.rpc.close-server:false}")
	private boolean closeServer = true;
	@Value("${erc.rpc.close-client:false}")
	private boolean closeClient = true;

	public String[] getConsulHost() {
		return consulHost;
	}

	public void setConsulHost(String[] consulHost) {
		this.consulHost = consulHost;
	}

	public String[] getErcHost() {
		return ercHost;
	}

	public void setErcHost(String[] ercHost) {
		this.ercHost = ercHost;
	}

	public Integer[] getConsulport() {
		return consulport;
	}

	public void setConsulport(Integer[] consulport) {
		this.consulport = consulport;
	}

	public Integer[] getErcPort() {
		return ercPort;
	}

	public void setErcPort(Integer[] ercPort) {
		this.ercPort = ercPort;
	}

	public String getConsulLocalhostAddress() {
		return consulLocalhostAddress;
	}

	public void setConsulLocalhostAddress(String consulLocalhostAddress) {
		this.consulLocalhostAddress = consulLocalhostAddress;
	}

	public String getErcLocalhostAddress() {
		return ercLocalhostAddress;
	}

	public void setErcLocalhostAddress(String ercLocalhostAddress) {
		this.ercLocalhostAddress = ercLocalhostAddress;
	}

	public Integer getLocalhostPort() {
		return localhostPort;
	}

	public void setLocalhostPort(Integer localhostPort) {
		this.localhostPort = localhostPort;
	}

	public boolean isEnableServer() {
		return closeServer;
	}

	public void setCloseServer(boolean closeServer) {
		this.closeServer = closeServer;
	}

	public boolean isCloseClient() {
		return closeClient;
	}

	public void setCloseClient(boolean closeClient) {
		this.closeClient = closeClient;
	}

	public String getConfig_discovery() {
		return config_discovery;
	}

	public void setConfig_discovery(String config_discovery) {
		this.config_discovery = config_discovery;
	}

}
