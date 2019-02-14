package cn.yuyizyk.rpc.core;

/**
 * 熔断 I interface 标记
 * 
 * 
 *
 * @author yuyi
 * @param <T>
 */
public interface RIFuse<T extends RPCIService & RIFuse<T>> {
}
