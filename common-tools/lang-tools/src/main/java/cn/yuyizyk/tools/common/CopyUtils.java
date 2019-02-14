package cn.yuyizyk.tools.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 深拷贝操作
 * 
 * @author yuyi
 *
 */
public class CopyUtils {
	private final static Logger log = LoggerFactory.getLogger(CopyUtils.class);

	/**
	 * 深拷贝
	 * 
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T deepCopy(T src) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
			out.writeObject(src);

			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			@SuppressWarnings("unchecked")
			T dest = (T) in.readObject();
			return dest;
		} catch (IOException | ClassNotFoundException e) {
			log.error("", e);
		}
		return null;
	}
}
