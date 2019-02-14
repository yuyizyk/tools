package cn.yuyizyk.tools.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用字符串处理
 * 
 * @author yuyi
 *
 */
public class Strings {
	private final static Logger log = LoggerFactory.getLogger(Strings.class);

	/**
	 * 空字符
	 */
	public static final String BANKSTR = "";

	/**
	 * 随机字符串生成
	 * 
	 * @return
	 */
	public static String getRandmStr(int length) {
		char[] tempCs = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o',
				'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R',
				'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N',
				'M' };
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int temp = random.nextInt();
			if (0 != temp) {
				sb.append(tempCs[Math.abs(temp) % tempCs.length]);
			} else {
				sb.append(tempCs[Integer.MIN_VALUE % tempCs.length]);
			}
		}
		return sb.toString();
	}

	/**
	 * 字符串转为16进制
	 * 
	 * @param s
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String convertStringTo16(String s) {
		byte[] b = s.getBytes();
		String str = "";
		try {
			for (int i = 0; i < b.length; i++) {
				Integer I = new Integer(b[i]);
				String strTmp = I.toHexString(b[i]);
				if (strTmp.length() > 2)
					strTmp = strTmp.substring(strTmp.length() - 2);
				str = str + strTmp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toUpperCase();
	}

	/**
	 * 16进制转为字符串
	 * 
	 * @param s
	 * @return
	 */
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

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
	 * 获得指定Ascii长度的省略字符串 <br/>
	 */
	public static String subOmitStr(String text, int length) {
		return subOmitStr(text, length, "...");

	}

	/**
	 * 过滤Pattern匹配的字符串，并保留group数据。
	 * 
	 * @param input
	 * @param p
	 * @return
	 */
	public static String filter(String input, Pattern p) {
		Matcher m = p.matcher(input);
		int start = 0, end;
		StringBuilder sb = new StringBuilder();
		while (m.find()) {
			end = m.start();
			sb.append(input.subSequence(start, end));
			for (int i = 1, len = m.groupCount(); i <= len; i++) {
				sb.append(input.subSequence(m.start(i), m.end(i)));
			}
			start = m.end();
		}
		end = input.length();
		sb.append(input.subSequence(start, end));
		return sb.toString();
	}

	/**
	 * 将字符串按行风格，支持windows(\r\n)、linux(\n)和(\r)格式换行。
	 * 
	 * @param s
	 * @return
	 */
	public static String[] splitLines(String s) {
		return StringUtils.split(s, "\r\n");
	}

	/**
	 * 将换行符替换成\n
	 * 
	 * @return
	 */
	public static String replaceNewline(String s) {
		s = StringUtils.replace(s, "\r\n", "\n");
		s = StringUtils.replaceChars(s, '\r', '\n');
		return s;
	}

	public static void replace(StringBuilder sb, String searchString, String replacement) {
		int start = 0;
		int end = sb.indexOf(searchString, start);
		if (end == -1) {
			return;
		}
		int searchLength = searchString.length();
		int replaceLength = replacement.length();
		while (end != -1) {
			sb.replace(end, end + searchLength, replacement);
			start = end + replaceLength;
			end = sb.indexOf(searchString, start);
		}
	}

	public static String urlEncode(String s) {
		if (StringUtils.isBlank(s)) {
			return s;
		}
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never
			throw new RuntimeException(e);
		}

	}

	public static String urlDecode(String s) {
		if (StringUtils.isBlank(s)) {
			return s;
		}
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获得指定Ascii长度的省略字符串 <br/>
	 * 字符串截断。编码大于127的字符作为占两个位置，否则占一个位置。
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String subOmitStr(String text, int length, String append) {
		if (StringUtils.isBlank(text) || text.length() < length) {
			return text;
		}
		int num = 0, i = 0, len = text.length();
		StringBuilder sb = new StringBuilder();
		for (; i < len; i++) {
			char c = text.charAt(i);
			if (c > 127) {
				num += 2;
			} else {
				num++;
			}
			if (num <= length * 2) {
				sb.append(c);
			}
			if (num >= length * 2) {
				break;
			}
		}
		if (i + 1 < len && StringUtils.isNotBlank(append)) {
			if (text.charAt(i) > 127) {
				sb.setLength(sb.length() - 1);
			} else {
				sb.setLength(sb.length() - 2);
			}
			sb.append(append);
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
