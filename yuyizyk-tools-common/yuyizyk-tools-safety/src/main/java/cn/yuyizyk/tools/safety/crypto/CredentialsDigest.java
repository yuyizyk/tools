package cn.yuyizyk.tools.safety.crypto;

/**
 * 证书加密
 * 
 * @author liufang
 * 
 */
public interface CredentialsDigest {
	public String digest(String plainCredentials, byte[] salt);

	public boolean matches(String credentials, String plainCredentials,
			byte[] salt);
}
