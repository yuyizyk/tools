package cn.yuyizyk.rpc.filter;

/**
 * 服务端 - 服务结束后执行
 * 
 * 
 *
 * @author yuyi
 */
@FunctionalInterface
public interface RServerAfterFilter extends RServerFilter {
	public void after();
}
