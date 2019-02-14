package cn.yuyizyk.tools.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数组操作
 * 
 * @author zyk
 *
 */
public class ArrayUtil {
	/**
	 * 并集 A∪B<br/>
	 * 
	 * @param list
	 *            a 集
	 * @param lists
	 *            B 集
	 * 
	 * @return
	 */
	@SafeVarargs
	public static <T extends Object> List<T> union(Collection<T> list1, Collection<T>... lists) {
		Stream.of(lists).forEach(list1::addAll);
		return list1.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 差集
	 * 
	 * @param list
	 *            a 集
	 * @param lists
	 *            被减
	 * @return 差
	 */
	@SafeVarargs
	public static <T extends Object> Collection<T> difference(Collection<T> list, Collection<T>... lists) {
		for (Collection<T> l : lists) {
			if (list.isEmpty())
				return list;
			Collection<T> teml = list;
			list = l.stream().filter(t -> !teml.contains(t)).collect(Collectors.toList());
		}
		return list;
	}

	/**
	 * 交集 A∩B<br/>
	 * 
	 * @param list
	 *            a 集
	 * @param lists
	 *            B 集
	 * 
	 * @return
	 */
	public static <T extends Object> Collection<T> intersection(Collection<T> list1,
			@SuppressWarnings("unchecked") Collection<T>... lists) {
		for (Collection<T> l : lists) {
			if (list1.isEmpty())
				return list1;
			Collection<T> teml = list1;
			list1 = l.stream().filter(t -> teml.contains(t)).collect(Collectors.toList());
		}
		return list1;
	}

	/**
	 * 数组深拷贝
	 * 
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T deepCopy(T src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		T dest = (T) in.readObject();
		return dest;
	}
}
