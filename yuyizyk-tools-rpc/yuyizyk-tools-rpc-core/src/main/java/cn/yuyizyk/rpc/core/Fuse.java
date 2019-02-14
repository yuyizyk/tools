package cn.yuyizyk.rpc.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Fuse {
	Class<? extends RIFuse<?>> value();

	/** 方法名称 */
	FusePoint[] point() default {};
}
