package cn.yuyizyk.tools.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cn.yuyizyk.tools.common.lang.Objs;
import net.sf.cglib.beans.BeanCopier;

/**
 * Bean Map转换
 * 
 * @author tngou@tngou.net
 *
 */
public class BeanUtil {
	private final static Logger log = LoggerFactory.getLogger(BeanUtil.class);

	/**
	 * toMapByGetter <br/>
	 * 通过对象的getter 获得
	 * 
	 * @param obj
	 * @return
	 */
	public static final HashMap<String, Object> toMapByGetter(Object obj) {
		return toMapByGetter(obj, a -> {
			return true;
		}, a -> {
			return true;
		});
	}

	/**
	 * toMapByGetter <br/>
	 * 通过对象的getter 获得
	 * 
	 * @param obj
	 * @return
	 */
	public static final HashMap<String, Object> toMapByGetter(Object obj,
			final Function<PropertyDescriptor, Boolean> checkPropertyDescriptor,
			final Function<Object, Boolean> checkValue) {
		if (obj == null)
			return null;
		HashMap<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			String name;
			Object val;
			for (int i = 0; i < descriptors.length; i++) {
				name = descriptors[i].getName();
				if (!"class".equals(name) && checkPropertyDescriptor.apply(descriptors[i])) {
					val = propertyUtilsBean.getNestedProperty(obj, name);
					if (checkValue.apply(val))
						params.put(name, val);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error("toMap异常:obj:[{}]", obj, e);
			e.printStackTrace();
		}
		return params;
	}

	private static final SimpleCache<String, BeanCopier> cacheBeanCopier = new SimpleCache<>();

	/**
	 * 快速拷贝
	 * 
	 * @param tag
	 * @param src
	 */
	public static <T> T beanCopier(T tag, Object src) {
		if (Objs.anyEmpty(tag, src))
			return null;
		String str = new StringBuilder().append(src.getClass().toString()).append(" TO ")
				.append(tag.getClass().toString()).toString();
		BeanCopier bc;
		if ((bc = cacheBeanCopier.get(str)) == null) {
			bc = BeanCopier.create(src.getClass(), tag.getClass(), false);
			cacheBeanCopier.put(str, bc);
		}
		bc.copy(src, tag, null);
		return tag;
	}

	/**
	 * 
	 * @param tag
	 * @param src
	 */
	public static <T> T beanCopier(T tag, Map<String, ? extends Object> properties) {
		try {
			return populate(tag, properties);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error(e.getMessage(), e);
		}
		return tag;
	}

	/**
	 * 转换
	 * 
	 * @param bean
	 * @param properties
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T> T populate(T bean, Map<String, ? extends Object> properties)
			throws IllegalAccessException, InvocationTargetException {
		ConvertUtils.register(new BeanUtil().new MyTimestamp(), Timestamp.class); // 时间处理
		ConvertUtils.register(new BeanUtil().new MyDate(), Date.class);
		BeanUtilsBean.getInstance().populate(bean, properties);
		return bean;
	}

	/**
	 * 时间
	 * 
	 * @author tngou@tngou.net
	 *
	 */
	class MyTimestamp implements Converter {

		@SuppressWarnings({ "unchecked", "hiding" })
		@Override
		public <Timestamp> Timestamp convert(Class<Timestamp> paramClass, Object paramObject) {
			if (paramObject == null)
				return null;
			LocalDateTime localDateTime = DateUtil.parse(paramObject.toString());
			if (localDateTime == null)
				return null;
			return (Timestamp) DateUtil.toTimestamp(localDateTime);
		}

	}

	class MyDate implements Converter {

		@SuppressWarnings({ "unchecked", "hiding" })
		@Override
		public <Date> Date convert(Class<Date> paramClass, Object paramObject) {
			if (paramObject == null)
				return null;
			LocalDateTime localDateTime = DateUtil.parse(paramObject.toString());
			if (localDateTime == null)
				return null;
			return (Date) DateUtil.toSqlDate(localDateTime);
		}

	}

}
