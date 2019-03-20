package cn.yuyizyk.rpc.core;

/**
 * SERVER 注册模块
 * 
 * 
 *
 * @author yuyi
 */
public interface RSRegister {

	/**
	 * 注册
	 * 
	 * @return
	 */
	public RSRegister register();

	/**
	 * 销毁|注销
	 * 
	 * @return
	 */
	public RSRegister deregister();

}
