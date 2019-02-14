package cn.yuyizyk.tools.common.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
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
public class Strs {
	private final static Logger log = LoggerFactory.getLogger(Strs.class);

	/**
	 * 日期型正则文本格式（YYYY-MM-DD）
	 */
	public static final String DATE_FORMAT_REGULARITY = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";

	/**
	 * 时间型正则文本格式（YYYY-MM-DD HH:MM:SS）
	 */
	public static final String TIME_FORMAT_REGULARITY = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8])))))) ((0[0-9])|(1[0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9]{1}";

	/**
	 * 
	 * @Description: 替换空格 ，半角 、全角
	 */
	public static String ReplaceBlank(String str) {
		if (str == null)
			return str;
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		str = m.replaceAll("");
		str = StringUtils.remove(str, "　");
		str = StringUtils.remove(str, " ");
		return str;
	}

	public static final String find(String str, String regx) {
		if (str == null)
			return str;
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(str);
		return m.find() ? m.group() : null;
	}

	/**
	 * 判断是否为日期型数据
	 * 
	 * @Description: 判断是否为日期型数据(YYYY-MM-DD)
	 */
	public static boolean isDate(String date) {
		/**
		 * 判断日期格式和范围
		 */
		Pattern pat = Pattern.compile(DATE_FORMAT_REGULARITY);

		Matcher mat = pat.matcher(date);

		boolean dateType = mat.matches();

		return dateType;
	}

	/**
	 * 判断是否为时间型数据
	 * 
	 * @Description: 判断是否为时间型数据(YYYY-MM-DD HH:MM:SS)
	 */
	public static boolean isTime(String dateString) {
		return dateString.matches(TIME_FORMAT_REGULARITY);
	}

	public static void main(String[] args) {
		// String date = "2017-11-15";
		// System.out.println(isDate(date));
		// String time = "2017-4-31 23:23:23";
		// System.out.println(isTime(time));
		// System.out.println(Charset.defaultCharset());
		// String s = "asdf中国你a好";
		// System.out.println(getOmitStr(s, 16));
		// System.out.println(getAsciiLength(getOmitStr(s, 16)));
	}

	/**
	 * 获得指定长度的省略字符串
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String getOmitStr(String str, int length) {
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

	public static String getEncodingString(String str, String defaEncod) {
		String enc = getEncoding(str);
		if (StringUtils.isBlank(enc)) {
			enc = defaEncod;
		}
		if (StringUtils.isBlank(enc)) {
			enc = "utf-8";
		}
		try {
			return new String(str.getBytes(enc), "utf-8");
		} catch (Exception e) {
			return str;
		}
	}

	public static String clobToString(Clob clob) throws SQLException, IOException {
		String reString = "";
		Reader is = clob.getCharacterStream();// 得到流
		BufferedReader br = new BufferedReader(is);
		String s = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
			sb.append(s);
			s = br.readLine();
		}
		reString = sb.toString();
		if (br != null) {
			br.close();
		}
		if (is != null) {
			is.close();
		}
		return reString;
	}

	/** UTF-8 */
	public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;

	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj 对象
	 * @return 字符串
	 */
	public static String toString(Object obj) {
		return toString(obj, CHARSET_UTF_8);
	}

	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj         对象
	 * @param charsetName 字符集
	 * @return 字符串
	 */
	public static String toString(Object obj, String charsetName) {
		return toString(obj, Charset.forName(charsetName));
	}

	/**
	 * 对象是否为数组对象
	 * 
	 * @param obj 对象
	 * @return 是否为数组对象，如果为{@code null} 返回false
	 */
	public static boolean isArray(Object obj) {
		if (null == obj) {
			return false;
		}
		return obj.getClass().isArray();
	}

	/**
	 * 数组或集合转String
	 * 
	 * @param obj 集合或数组对象
	 * @return 数组字符串，与集合转字符串格式相同
	 */
	public static String arr2String(Object obj) {
		if (null == obj) {
			return null;
		}
		if (isArray(obj)) {
			try {
				return Arrays.deepToString((Object[]) obj);
			} catch (Exception e) {
				final String className = obj.getClass().getComponentType().getName();
				switch (className) {
				case "long":
					return Arrays.toString((long[]) obj);
				case "int":
					return Arrays.toString((int[]) obj);
				case "short":
					return Arrays.toString((short[]) obj);
				case "char":
					return Arrays.toString((char[]) obj);
				case "byte":
					return Arrays.toString((byte[]) obj);
				case "boolean":
					return Arrays.toString((boolean[]) obj);
				case "float":
					return Arrays.toString((float[]) obj);
				case "double":
					return Arrays.toString((double[]) obj);
				default:
					throw e;
				}
			}
		}
		return obj.toString();
	}

	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj     对象
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String toString(Object obj, Charset charset) {
		if (isEmpty(obj)) {
			return null;
		}

		if (obj instanceof String) {
			return (String) obj;
		} else if (obj instanceof byte[]) {
			return toString((byte[]) obj, charset);
		} else if (obj instanceof Byte[]) {
			return toString((Byte[]) obj, charset);
		} else if (obj instanceof ByteBuffer) {
			return toString((ByteBuffer) obj, charset);
		} else if (isArray(obj)) {
			return arr2String(obj);
		}

		return obj.toString();
	}

	/**
	 * 将byte数组转为字符串
	 * 
	 * @param bytes   byte数组
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, String charset) {
		return toString(bytes, StringUtils.isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 解码字节码
	 * 
	 * @param data    字符串
	 * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String toString(byte[] data, Charset charset) {
		if (isEmpty(data)) {
			return null;
		}

		if (isEmpty(charset)) {
			return new String(data);
		}
		return new String(data, charset);
	}

	/**
	 * 将Byte数组转为字符串
	 * 
	 * @param bytes   byte数组
	 * @param charset 字符集
	 * @return 字符串
	 */
	public static String toString(Byte[] bytes, String charset) {
		return toString(bytes, StringUtils.isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 解码字节码
	 * 
	 * @param data    字符串
	 * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String toString(Byte[] data, Charset charset) {
		if (isEmpty(data)) {
			return null;
		}

		byte[] bytes = new byte[data.length];
		Byte dataByte;
		for (int i = 0; i < data.length; i++) {
			dataByte = data[i];
			bytes[i] = (isEmpty(dataByte)) ? -1 : dataByte.byteValue();
		}

		return toString(bytes, charset);
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty([])        = true
	 * isEmpty([null])       = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final Object[] args) {
		return args == null || args.length == 0;
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty([])        = true
	 * isEmpty([null])       = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final Collection<?> args) {
		return args == null || args.isEmpty();
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty({})        = true
	 * isEmpty({null=null})       = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final Map<?, ?> args) {
		return args == null || args.isEmpty();
	}

	/**
	 * 
	 * @param args
	 * @return args == null
	 */
	public static final boolean isEmpty(final Object args) {
		return Objects.isNull(args);
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty("")        = true
	 * isEmpty(" ")       = false
	 * isEmpty("bob")     = false
	 * isEmpty("  bob  ") = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final CharSequence cs) {
		return StringUtils.isEmpty(cs);
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final long... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final int... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final short... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final char... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final byte... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final double... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final float... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final boolean... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 将编码的byteBuffer数据转换为字符串
	 * 
	 * @param data    数据
	 * @param charset 字符集，如果为空使用当前系统字符集
	 * @return 字符串
	 */
	public static String toString(ByteBuffer data, String charset) {
		if (isEmpty(data)) {
			return null;
		}

		return toString(data, Charset.forName(charset));
	}

	/**
	 * 将编码的byteBuffer数据转换为字符串
	 * 
	 * @param data    数据
	 * @param charset 字符集，如果为空使用当前系统字符集
	 * @return 字符串
	 */
	public static String toString(ByteBuffer data, Charset charset) {
		if (isEmpty(charset)) {
			charset = Charset.defaultCharset();
		}
		return charset.decode(data).toString();
	}

	/**
	 * {@link CharSequence} 转为字符串，null安全
	 * 
	 * @param cs {@link CharSequence}
	 * @return 字符串
	 */
	public static String toString(CharSequence cs) {
		return isEmpty(cs) ? null : cs.toString();
	}

	public static Charset charset(String charsetName) {
		return StringUtils.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
	}

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
	 * @param n   整数位数
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
