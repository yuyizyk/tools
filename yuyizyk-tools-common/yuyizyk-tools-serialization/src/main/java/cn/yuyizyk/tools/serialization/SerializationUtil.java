package cn.yuyizyk.tools.serialization;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;

import cn.yuyizyk.tools.common.lang.Objs;

/**
 * 序列化工具
 *
 */
public class SerializationUtil implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient final static Logger log = LoggerFactory.getLogger(SerializationUtil.class);
	private transient final static Gson gson = getGsonBuilder().create();

	public static final GsonBuilder getGsonBuilder() {
		return JSONSerializeUtil.getGsonBuilder();
	}

	/**
	 * 将json字符串格式化
	 * 
	 * @param jsStr
	 * @param cls   格式化对象类型
	 */
	public static final <T> T toBeanByJson(String jsStr, Class<T> cls) throws JsonParseException {
		return newJSONSerializeUtil().toBeanByJson(jsStr, cls);
	}

	/**
	 * 将xml字符串格式化
	 * 
	 * @param xml
	 * @param cls
	 * @return
	 */
	public static final <T> T toBeanByXml(String xml, Class<T> cls) {
		return cls.cast(newXMLSerializeUtil().deserializeByStr(xml));
	}

	/**
	 * 将json字符串格式化
	 * 
	 * @param jsStr
	 * @param type  可处理泛型
	 */
	public static final <T> T toBeanByJson(String jsStr, TypeToken<T> type) throws JsonParseException {
		return newJSONSerializeUtil().toBeanByJson(jsStr, type);
	}

	/**
	 * 
	 * @param jsStr
	 * @return
	 */
	public static final JsonElement toJson(String jsStr) throws JsonParseException {
		return newJSONSerializeUtil().toJson(jsStr);
	}

	/**
	 * 
	 * @param jsStr
	 * @return
	 */
	public static final boolean isJson(String jsStr) {
		return newJSONSerializeUtil().isJson(jsStr);
	}

	public static void main(String[] args) {

		System.out.println(serializeToStr("123"));
		System.out.println(unserializeByStr("123"));

		// getGsonBuilder().create();

		// getGsonBuilder().registerTypeHierarchyAdapter(POJO.class, new
		// TypeAdapter<POJO>() {
		//
		// @Override
		// public POJO read(JsonReader in) throws IOException {
		// return null;
		// }
		//
		// @Override
		// public void write(JsonWriter out, POJO value) throws IOException {
		// }
		//
		// }).create().fromJson(json, classOfT);
		// getGsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {
		// @Override
		// public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		// return null;
		// }
		// });

		/*
		 * u = gson.fromJson(toJson("{\"userid\":\"123\"}"), new TypeToken<UserInfo>() {
		 * }.getType());
		 */
		// u = toBean("{\"userid\":\"123\"}", UserInfo.class);
		// System.out.println(u.toJsonStr());
		//
		// List<Map<Integer, UserInfo>> li = new ArrayList<Map<Integer, UserInfo>>();
		// Map<Integer, UserInfo> m = new HashMap<>();
		// m.put(123, u);
		// System.out.println(toJsonStr(m));
		// li.add(m);
		// System.out.println(toJsonStr(li));
		// System.out.println(12312);
		//
		// toBean("[{\"123\":{\"userid\":\"123\"}}]", new TypeToken<List<Map<Integer,
		// UserInfo>>>());
		// System.out.println(li.size());
		// System.out.println(li);
		// u = li.get(0).entrySet().iterator().next().getValue();
		// System.out.println(u.toJsonStr());
		// System.out.println(toJsonStr(li));

	}

	/**
	 * 序列化为json字符串 <br/>
	 * 默认使用gson，内部类gson不能解析，使用fastjson代替
	 * 
	 * @param bean
	 * @return
	 */
	public static final String toJsonStr(Object bean) {
		Class<?> cls = bean.getClass();
		if (Objects.isNull(cls.getCanonicalName())) {
			/**
			 * 目标返回基础类的规范名称 <br/>
			 * 由Java语言规范定义。 如果返回null: 基础类没有规范的名称<br/>
			 * （即，它是一个本地或匿名类或其组件的数组类型没有规范名称）
			 */
			return JSONObject.toJSONString(bean);
		}
		return gson.toJson(bean);
	}

	/**
	 * 序列化
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> String serializeToStr(T obj) {
		return newDefualtSerializeUtil().serializeToStr(obj);
	}

	/**
	 * 将对象序列化到一个流中
	 */
	public static <T> void serialize(T obj, OutputStream os) {
		newDefualtSerializeUtil().serialization(obj, os);
	}

	/**
	 * 流中读取
	 * 
	 * @param clz
	 * @param bais
	 * @return
	 */
	public Object deserialization(InputStream bais) {
		return deserialization(Object.class, bais);
	}

	/**
	 * 流中读取
	 * 
	 * @param clz
	 * @param bais
	 * @return
	 */
	public <T> T deserialization(Class<T> clz, InputStream bais) {
		Object obj = newDefualtSerializeUtil().deserialize(bais);
		if (obj != null) {
			if (clz.isInstance(obj))
				return clz.cast(obj);
			log.error(" deserialize[{}] not is  Class {} ", bais, clz);
		}
		return null;
	}

	/**
	 * 反序列化
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T deserializeByStr(String s, Class<T> clz) {
		Object obj = newDefualtSerializeUtil().deserializeByStr(s);
		if (obj != null) {
			if (clz.isInstance(obj))
				return clz.cast(obj);
			log.error(" deserialize[{}] not is  Class {} ", s, clz);
		}
		return null;
	}

	/**
	 * 反序列化
	 * 
	 * @param obj
	 * @return
	 */
	public static Serializable deserializeByStr(String s) {
		return deserializeByStr(s, Serializable.class);
	}

	/**
	 * 反序列化
	 * 
	 * @param obj
	 * @return
	 */
	public static Serializable unserializeByStr(String s) {
		return deserializeByStr(s, Serializable.class);
	}

	/**
	 * 序列化
	 * 
	 * @param     <T> 对象类型
	 * @param obj 要被序列化的对象
	 * @return 序列化后的字节码
	 */
	public static <T> byte[] serialize(T obj) {
		return newDefualtSerializeUtil().serialize(obj);
	}

	/**
	 * 反序列化
	 * 
	 * @param       <T> 对象类型
	 * @param bytes 反序列化的字节码
	 * @return 反序列化后的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unserialize(byte[] bytes) {
		return (T) newDefualtSerializeUtil().unserialize(bytes, Serializable.class);
	}

	/**
	 * 将对象xml化
	 * 
	 * @param obj
	 * @return
	 */
	public static final String serializeToXmlStr(Object obj) {
		if (Objs.isEmpty(obj)) {
			return null;
		}
		XStream x = new XStream();
		return x.toXML(obj);
	}

	protected static final AbstractSerializeUtil SERIALIZE_UTIL = new ProtostuffSerializeUtil();
	// newJavaSerializeUtil();

	public static final AbstractSerializeUtil newDefualtSerializeUtil() {
		return SERIALIZE_UTIL;
	}

	public static final JSerializeUtil newJavaSerializeUtil() {
		return new SerializeUtilBuilde().create(JSerializeUtil.class);
	}

	public static final XMLSerializeUtil newXMLSerializeUtil() {
		return new SerializeUtilBuilde().create(XMLSerializeUtil.class);
	}

	public static final JSONSerializeUtil newJSONSerializeUtil() {
		return new SerializeUtilBuilde().create(JSONSerializeUtil.class);
	}

	public static class SerializeUtilBuilde {

		public <T extends AbstractSerializeUtil> T create(Class<T> clz) {
			try {
				return clz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("异常", e);
				return null;
			}
		}
	}

}
