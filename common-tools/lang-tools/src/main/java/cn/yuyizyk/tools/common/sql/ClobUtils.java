package cn.yuyizyk.tools.common.sql;

import java.sql.SQLException;

import javax.sql.rowset.serial.SerialException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.common.CopyUtils;

/**
 * clob
 * 
 * @author yuyi
 *
 */
public class ClobUtils {
	private final static Logger log = LoggerFactory.getLogger(CopyUtils.class);

	/**
	 * 
	 * @return
	 * @throws SerialException
	 * @throws SQLException
	 */
	public java.sql.Clob by(String str) {
		try {
			return new javax.sql.rowset.serial.SerialClob(str.toCharArray());
		} catch (SQLException e) {
			log.error("", e);
			return null;
		}
	}

}
