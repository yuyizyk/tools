package cn.yuyizyk.tools.serialization;

import java.io.InputStream;

public interface Deserialization {

	public <T> T deserialize(InputStream obj);

	public <T> T deserializeByStr(String obj);
}
