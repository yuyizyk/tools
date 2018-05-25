package cn.yuyizyk.tools.common;

import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * url字符串 处理
 * 
 * @author yuyi
 *
 */
public class URLTools {
	private final static Logger log = LoggerFactory.getLogger(StringTools.class);

	public static boolean is(String url) {
		return Regex.matches(url, Regex.V_URL);
	}

	/**
	 * 获得url的domain  
	 */
	public static final String getDomain(String url) {
		List<String> li = Regex.findSubStr(url, Regex.V_DOMAIN);
		return li.isEmpty() ? null : li.get(0);
	}

	/**
	 * 获得url的getHost  
	 */
	public static final String getHost(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getHost();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 获得url的Protocol  
	 */
	public static final String getProtocol(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getProtocol();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 获得url的getUriPath  
	 */
	public static final String getUriPath(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getPath();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 获得url的getUrlQueryStr  
	 */
	public static final String getUrlQueryStr(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getQuery();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	public static final int getPort(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getPort();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return 0;
	}

}
