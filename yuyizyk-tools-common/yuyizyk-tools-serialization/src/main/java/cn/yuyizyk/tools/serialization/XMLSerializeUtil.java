package cn.yuyizyk.tools.serialization;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.yytaliance.entity.Box;
import com.yytaliance.util.Objs;

/**
 * javaBean与xml之间的序列化和反序列化
 * 
 * 
 *
 * @author yuyi
 */
public class XMLSerializeUtil extends AbstractSerializeUtil {

	public static void main(String[] args) {
		Box<Map<String, Object>> b = new Box<>();
		Map<String, Object> map = new HashMap<>();
		b.setObj(map);
		XStream x = new XStream(new DomDriver());
		x.processAnnotations(b.getClass());// 开启对b.getClass的注解解析
		// 类别名
		// x.alias(b.getClass().getSimpleName(), b.getClass());// 将class box 节名 输出为
		// b.getClass().getSimpleName()
		// 类成员别名
		x.aliasField("value", Box.class, "obj");
		x.aliasAttribute(Box.class, "obj", "value");

		// x.useAttributeFor(Box.class, "obj");
		// registerConverter(Converter converter) ，注册一个转换器。
		String str;
		System.out.println(str = x.toXML(b));
		System.out.println(str = new XMLSerializeUtil().serializeToStr(b));

		System.out.println(b = new XMLSerializeUtil().deserializeByStr(str));
	}

	/**
	 * 将对象xml化
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T deserializeByXmlStr(String xml, Class<T> clz) {
		if (Objs.isEmpty(xml) || clz == null) {
			return null;
		}
		XStream x = new XStream(new DomDriver());
		x.alias(clz.getSimpleName(), clz);
		return (T) x.fromXML(xml);
	}

	/**
	 * 将对象xml化
	 * 
	 * @param obj
	 * @return
	 */
	public String serializeToXmlStr(Object obj) {
		if (Objs.isEmpty(obj)) {
			return null;
		}
		XStream x = new XStream(new DomDriver());
		// x.processAnnotations(obj.getClass());
		// x.alias(obj.getClass().getSimpleName(), obj.getClass());
		return x.toXML(obj);
	}

	@Override
	public void serialization(Object obj, OutputStream os) {
		if (Objs.isEmpty(obj)) {
			return;
		}
		XStream x = new XStream(new DomDriver());
		// x.processAnnotations(obj.getClass());
		// x.alias(obj.getClass().getSimpleName(), obj.getClass());
		x.toXML(obj, os);
	}

	@Override
	public String serializeToStr(Object obj) {
		return serializeToXmlStr(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserializeByStr(String obj) {
		if (Objs.isEmpty(obj)) {
			return null;
		}
		XStream x = new XStream(new DomDriver());
		return (T) x.fromXML(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream obj) {
		if (Objs.isEmpty(obj)) {
			return null;
		}
		XStream x = new XStream(new DomDriver());
		return (T) x.fromXML(obj);
	}

	@SuppressWarnings("unchecked")
	public <T> T deserializeByStr(InputStream obj, Class<T> clz) {
		if (Objs.isEmpty(obj) || clz == null) {
			return null;
		}
		XStream x = new XStream(new DomDriver());
		x.alias(clz.getSimpleName(), clz);
		return (T) x.fromXML(obj);
	}

}
