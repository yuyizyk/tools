package cn.yuyizyk.tools.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author yuyi
 *
 */
public class NumberTools {
	private final static Logger log = LoggerFactory.getLogger(NumberTools.class);

	public static boolean is(String str) {
		return Regex.matches(str, Regex.V_NUMBER);
	}

}
