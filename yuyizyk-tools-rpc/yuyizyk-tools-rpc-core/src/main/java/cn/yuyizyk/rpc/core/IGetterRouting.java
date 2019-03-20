package cn.yuyizyk.rpc.core;

import cn.yuyizyk.tools.common.entity.DoubleEntity;

@FunctionalInterface	
public interface IGetterRouting {
	/**  */
	DoubleEntity<String, Integer> get();

	default public IGetterRouting tagCurrentError() {
		return this;
	}
}
