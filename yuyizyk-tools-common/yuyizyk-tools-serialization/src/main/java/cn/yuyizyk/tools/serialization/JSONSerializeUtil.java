package cn.yuyizyk.tools.serialization;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import cn.yuyizyk.tools.common.BeanUtil;
import cn.yuyizyk.tools.common.entity.DoubleEntity;
import cn.yuyizyk.tools.common.lang.Objs;

/**
 * 
 * javaBean 与json之间的序列化和反序列化
 * 
 *
 * @author yuyi
 */
public class JSONSerializeUtil extends AbstractSerializeUtil {
	private final static Logger log = LoggerFactory.getLogger(JSONSerializeUtil.class);

	private GsonSerialize gsonSerialize = new GsonSerialize();
	private JSONSerializeEngine engine = gsonSerialize;
	private JSONUtil util = gsonSerialize;

	@Override
	public void serialization(Object obj, OutputStream os) {
		engine.serialization(obj, os);
	}

	@Override
	public String serializeToStr(Object obj) {
		return engine.serializationToStr(obj);
	}

	@Override
	public <T> T deserialize(InputStream obj) {
		return engine.deserialize(obj);
	}

	@Override
	public <T> T deserializeByStr(String obj) {
		return engine.deserializeByStr(obj);
	}

	/**
	 * 将json字符串格式化
	 * 
	 * @param jsStr
	 * @param cls   格式化对象类型
	 */
	public <T> T toBeanByJson(String jsStr, Class<T> cls) throws JsonParseException {
		return util.toBeanByJson(jsStr, cls);
	}

	/**
	 * 将json字符串格式化 <br/>
	 * gson
	 * 
	 * @param jsStr
	 * @param type  可处理泛型
	 */
	public <T> T toBeanByJson(String jsStr, TypeToken<T> type) throws JsonParseException {
		return gsonSerialize.toBeanByJson(jsStr, type);
	}

	/**
	 * 
	 * gson
	 * 
	 * @param jsStr
	 * @return
	 */
	public JsonElement toJson(String jsStr) throws JsonParseException {
		return gsonSerialize.toJson(jsStr);
	}

	/**
	 * 
	 * @param jsStr
	 * @return
	 */
	public final boolean isJson(String jsStr) {
		return util.isJson(jsStr);
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
		if (cls.isAnonymousClass() || cls.isLocalClass()) {
			return JSONObject.toJSONString(bean);
		}
		return GsonSerialize.gson.toJson(bean);
	}

	public static interface JSONSerializeEngine {

		<T> T deserialize(InputStream obj);

		<T> T deserializeByStr(String obj);

		void serialization(Object obj, OutputStream os);

		public String serializationToStr(Object obj);

	}

	private static interface JSONUtil {

		<T> T toBeanByJson(String jsStr, Class<T> cls);

		boolean isJson(String jsStr);

		public <T> T deserialize(InputStream obj, Class<T> clz);
	}

	public static final GsonBuilder getGsonBuilder() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")// 时间转化为特定格式
				.enableComplexMapKeySerialization()// 支持Map的key为复杂对象的形式
				// .setLenient()// json宽松
				// .setPrettyPrinting()// 对json结果格式化.
				// .excludeFieldsWithModifiers(java.lang.reflect.Modifier.PRIVATE)//
				//////////// 配置Gson以排除所有具有指定修饰符的类字段(序列化和反序列化都排除) 默认TRANSIENT STATIC
				// .registerTypeAdapter(Type type, Object typeAdapter)//配置Gson以进行自定义序列化或反序列化
				// .registerTypeAdapterFactory(TypeAdapterFactory factory//)为类型适配器注册一个工厂。
				// .registerTypeHierarchyAdapter(baseType,typeAdapter)//将Gson配置为继承类型层次结构的自定义序列化或反序列化。
				.setExclusionStrategies()// 配置Gson在序列化和反序列化过程中应用一组排除策略。
				// .disableInnerClassSerialization()// 配置Gson在序列化过程中排除内部类。
				// .addSerializationExclusionStrategy(strategy)//配置Gson在序列化过程中应用传入的排除策略
				// .addDeserializationExclusionStrategy(strategy)// 在反序列化过程中应用的排除策略
				// .disableHtmlEscaping()//默认情况下，Gson转义HTML字符，例如<>等。使用此选项将Gson配置为按原样传递HTML字符。
				.serializeNulls()// 配置Gson以序列化空字段。
		;
	}

	public static class GsonSerialize implements JSONSerializeEngine, JSONUtil {
		private final static Gson gson = getGsonBuilder().create();

		@Override
		public void serialization(Object obj, OutputStream os) {
			if (Objs.isEmpty(obj))
				return;
			gson.toJson(DoubleEntity.builder(obj.getClass(), obj), new OutputStreamWriter(os));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T deserialize(InputStream obj) {
			DoubleEntity<String, Map<String, Object>> entity = gson.fromJson(new InputStreamReader(obj),
					TypeToken.get(new DoubleEntity<Class<?>, Map<String, Object>>().getClass()).getType());
			if (entity == null)
				return null;
			if (StringUtils.isEmpty(entity.getEntity1()) || Objs.isEmpty(entity.getEntity2()))
				return null;

			try {
				Class<?> clz = Class.forName(entity.getEntity1());
				T t = (T) clz.newInstance();
				return BeanUtil.beanCopier(t, entity.getEntity2());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				log.error("ERROR  :{} , ", entity.toString(), e);
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T deserializeByStr(String jsStr) {
			DoubleEntity<String, Map<String, Object>> entity = gson.fromJson(toJson(jsStr),
					TypeToken.get(new DoubleEntity<Class<?>, Map<String, Object>>().getClass()).getType());
			if (entity == null)
				return null;
			if (StringUtils.isEmpty(entity.getEntity1()) || Objs.isEmpty(entity.getEntity2()))
				return null;

			try {
				Class<?> clz = Class.forName(entity.getEntity1());
				T t = (T) clz.newInstance();
				return BeanUtil.beanCopier(t, entity.getEntity2());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				log.error("ERROR  :{} , ", entity.toString(), e);
			}
			return null;
		}

		@Override
		public String serializationToStr(Object obj) {
			Class<?> cls = obj.getClass();
			if (cls.isAnonymousClass() || cls.isLocalClass()) {
				return JSONObject.toJSONString(DoubleEntity.builder(obj.getClass(), obj));
			}
			return GsonSerialize.gson.toJson(DoubleEntity.builder(obj.getClass(), obj));
		}

		/**
		 * gson
		 * 
		 * @param jsStr
		 * @return
		 */
		@Override
		public final boolean isJson(String jsStr) {
			if (StringUtils.isBlank(jsStr)) {
				return false;
			}
			try {
				JsonElement je = toJson(jsStr);
				return je.isJsonNull();
			} catch (JsonParseException e) {
				log.info("该json解析异常:[{}]", jsStr, e);
				return false;
			}
		}

		/**
		 * 将json字符串格式化
		 * 
		 * @param jsStr
		 * @param cls   格式化对象类型
		 */
		@Override
		public <T> T toBeanByJson(String jsStr, Class<T> cls) throws JsonParseException {
			return toBeanByJson(jsStr, TypeToken.get(cls));
		}

		/**
		 * 将json字符串格式化
		 * 
		 * @param jsStr
		 * @param type  可处理泛型
		 */
		public final <T> T toBeanByJson(String jsStr, TypeToken<T> type) throws JsonParseException {
			return gson.fromJson(toJson(jsStr), type.getType());
		}

		/**
		 * 
		 * 
		 * 
		 * @param jsStr
		 * @return
		 */
		public final JsonElement toJson(String jsStr) throws JsonParseException {
			try {
				return new JsonParser().parse(jsStr);
			} catch (JsonParseException e) {
				log.error("json解析异常:jsonStr:[{}]", jsStr, e);
				e.printStackTrace();
				throw e;
			}
		}

		@Override
		public <T> T deserialize(InputStream obj, Class<T> clz) {
			return gson.fromJson(new InputStreamReader(obj), clz);
		}

	}

	public <T> T deserializeByStr(String obj, Class<T> clz) {
		return toBeanByJson(obj, clz);
	}

	public <T> T deserializeByStr(InputStream obj, Class<T> clz) {
		return util.deserialize(obj, clz);
	}

}
