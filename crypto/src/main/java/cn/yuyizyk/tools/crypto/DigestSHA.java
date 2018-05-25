package cn.yuyizyk.tools.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;



import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 摘要的SHA系列加密 SHA1 SHA224 SHA256 SHA384 SHA512 和SHA1文件加密
 * @author PICOHOOD1
 * @version 
 *
 */
public class DigestSHA {

	private static final Logger logger = LoggerFactory.getLogger(DigestSHA.class);
	public static String SHA1(String data)
	{
		if (data==null) 
		{
			return null;
		}
//	
		return DigestUtils.sha1Hex(data);
		
	}
	public static String SHA1(File file)
	{
		
		if (file==null) 
		{
			return null;
		}
		String r =null;
		try {
			FileInputStream input = new FileInputStream(file);
			r= DigestUtils.sha1Hex(input);
			input.close();
		} catch (IOException e) {
			logger.error("{}文件SHA1加密错误", file.getName());
			logger.error("异常:{}",e);
		}
		
		
		return r;
	}
	
	
	public static String SHA224(String data)
	{
		if (data==null) 
		{
			return null;
		}
		Security.addProvider(new BouncyCastleProvider());
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-224");
			byte[] b = md.digest(data.getBytes());	
			return new String(Hex.encode(b));
			
		} catch (NoSuchAlgorithmException e) {
			logger.error("{}SHA-224加密错误", data);
			logger.error("异常:{}",e);
		} 
		return data;
		
	}
	public static String SHA256(String data)
	{
		if (data==null) 
		{
			return null;
		}
		
		return DigestUtils.sha256Hex(data);
		
	}
	public static String SHA384(String data)
	{
		if (data==null) 
		{
			return null;
		}
//		byte[] b = DigestUtils.sha384(data);
		return DigestUtils.sha384Hex(data);
		
	}
	public static String SHA512(String data)
	{
		if (data==null) 
		{
			return null;
		}
//		byte[] b = DigestUtils.sha512(data);
		return DigestUtils.sha512Hex(data);
		
	}
	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stu
		String s="81977100";
		System.out.println(SHA1(s));
		System.out.println(SHA224(s).length());
		System.out.println(SHA256(s).length());
		System.out.println(SHA384(s));
		System.out.println(SHA512(s));
		//System.out.println(SHA1(new File("G://软件备份/eclipse-jee-juno-win32.zip")));
		String val="123";
		// 用来将字节转换成 16 进制表示的字符
	     char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		 MessageDigest md = MessageDigest.getInstance("SHA1"); 
		 md.update(val.getBytes()); // 通过使用 update 方法处理数据,使指定的 byte数组更新摘要
	      byte[] encryptStr = md.digest(); // 获得密文完成哈希计算,产生128 位的长整数
	      char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符
	      int k = 0; // 表示转换结果中对应的字符位置
	      for (int i = 0; i < 16; i++) { // 从第一个字节开始，对每一个字节,转换成 16 进制字符的转换
	        byte byte0 = encryptStr[i]; // 取第 i 个字节
	        str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
	        str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
	      }
	        System.err.println( new String(str));; // 换后的结果转换为字符串
	}

}
