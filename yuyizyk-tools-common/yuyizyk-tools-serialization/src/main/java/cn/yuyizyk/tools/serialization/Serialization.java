package cn.yuyizyk.tools.serialization;

import java.io.OutputStream;

public interface Serialization {
	public void serialization(Object obj, OutputStream os);

	public String serializeToStr(Object obj);
}
