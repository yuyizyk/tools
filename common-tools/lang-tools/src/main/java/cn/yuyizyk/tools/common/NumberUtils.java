package cn.yuyizyk.tools.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author yuyi
 *
 */
public class NumberUtils {
	private final static Logger log = LoggerFactory.getLogger(NumberUtils.class);

	public static boolean is(String str) {
		return Regex.matches(str, Regex.V_NUMBER);
	}

}
