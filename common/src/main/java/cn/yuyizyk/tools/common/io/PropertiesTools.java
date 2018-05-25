package cn.yuyizyk.tools.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.common.CopyTools;

/**
 * 
 * @author yuyi
 *
 */
public class PropertiesTools {
	private final static Logger log = LoggerFactory.getLogger(CopyTools.class);

	public static Properties read(String path) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		Properties p = new Properties();
		try {
			p.load(new BufferedReader(new InputStreamReader(inputStream)));
		} catch (IOException e) {
			log.error("异常:", e);
		}
		return p;
	}

}
