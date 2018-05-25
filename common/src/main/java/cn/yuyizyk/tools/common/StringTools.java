package cn.yuyizyk.tools.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Objects;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用字符串处理
 * 
 * @author yuyi
 *
 */
public class StringTools {
	private final static Logger log = LoggerFactory.getLogger(StringTools.class);

	/**
	 * 空字符
	 */
	public static final String BANKSTR = "";

	/**
	 * 转化为字符串
	 * 
	 * @param num
	 * @param n
	 *            整数位数
	 * @return
	 */
	static public String by(int num, int n) {
		return String.format("%0" + n + "d", num);
	}

	/**
	 * Clob To String
	 * 
	 * @param clob
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	static public String by(java.sql.Clob clob) {
		Reader isClob;
		String contentstr = "";
		StringBuffer sbResult = new StringBuffer();
		try {
			isClob = clob.getCharacterStream();
			BufferedReader bfClob = new BufferedReader(isClob);
			String strClob = bfClob.readLine();
			while (strClob != null) {
				sbResult.append(strClob);
				strClob = bfClob.readLine();
			}
			contentstr = sbResult.toString();
		} catch (Exception e) {
			log.error("", e);
		}
		return contentstr;
	}

	/**
	 * 根据字符的Ascii来获得具体的长度
	 * 
	 * @param str
	 * @return
	 */
	public static int getAsciiLength(String str) {
		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			int ascii = Character.codePointAt(str, i);
			if (ascii >= 0 && ascii <= 255)
				length++;
			else
				length += 2;
		}
		return length;
	}

	/**
	 * 获得指定Ascii长度的省略字符串
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String getAsciiOmitStr(String str, int length) {
		if (length >= str.length() * 2) {
			return str;
		}
		StringBuffer sb = new StringBuffer();
		int len = 0;
		for (int i = 0; i < str.length(); i++) {
			int ascii = Character.codePointAt(str, i);
			if (ascii >= 0 && ascii <= 255) {
				if (len >= length - 3) {
					break;
				}
				len++;
			} else {
				if (len >= length - 4) {
					break;
				}
				len += 2;
			}
			sb.append(str.charAt(i));
		}
		if (length >= len + 3) {
			sb.append("...");
		}
		return sb.toString();
	}

	/**
	 * 为空则真
	 * 
	 * @param strs
	 * @return
	 */
	public static boolean isBank(String strs) {
		return Objects.isNull(strs) || BANKSTR.equals(strs);
	}

	/**
	 * 任一为空则为真
	 * 
	 * @param strs
	 * @return
	 */
	public static boolean isAnyBank(String... strs) {
		for (String s : strs) {
			if (!isBank(s))
				return true;
		}
		return false;
	}

	/**
	 * 任一不为空则为真
	 * 
	 * @param strs
	 * @return
	 */
	public static boolean isNoneBank(String... strs) {
		for (String s : strs) {
			if (isBank(s))
				return true;
		}
		return false;
	}

	/**
	 * 获得字符串的编码
	 * <p>
	 * Title: guessEncoding
	 * <p>
	 * Description: 取得二进制的编码
	 * 
	 * @param bytes
	 * @return 编码格式 默认返回的是UTF-8
	 */
	public static String GuessEncoding(byte[] bytes) {
		String DEFAULT_ENCODING = "UTF-8";
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		}
		return encoding;
	}

	/**
	 * 获得字符串的编码
	 * 
	 * @param str
	 * @return
	 */
	public static String GuessEncoding(String str) {
		return GuessEncoding(str.getBytes());
	}

	/**
	 * 获得字符串的编码
	 * 
	 * @param str
	 * @return
	 */
	protected static String getEncoding(String str) {

		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GB2312
				String s = encode;
				return s; // 是的话，返回“GB2312“，以下代码同理
			}
		} catch (UnsupportedEncodingException exception) {
			log.error("", exception);
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是ISO-8859-1
				String s1 = encode;
				return s1;
			}
		} catch (UnsupportedEncodingException exception1) {
			log.error("", exception1);
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是UTF-8
				String s2 = encode;
				return s2;
			}
		} catch (UnsupportedEncodingException exception2) {
			log.error("", exception2);
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { // 判断是不是GBK
				String s3 = encode;
				return s3;
			}
		} catch (UnsupportedEncodingException exception3) {
			log.error("", exception3);
		}
		return ""; // 如果都不是，说明输入的内容不属于常见的编码格式。
	}
}
