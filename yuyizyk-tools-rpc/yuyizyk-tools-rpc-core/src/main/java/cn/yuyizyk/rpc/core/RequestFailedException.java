package cn.yuyizyk.rpc.core;

/** 降级 */
public class RequestFailedException extends RuntimeException {

	private final EType etype;

	public RequestFailedException(EType etype) {
		this.etype = etype;
	}

	public RequestFailedException(EType etype, Throwable t) {
		super(t);
		this.etype = etype;
	}

	public RequestFailedException(EType etype, String string) {
		super(string);
		this.etype = etype;
	}

	public static enum EType {

		Pivot, PivotAllError, PivotAllNotConnect, Demotion, MissService
	}

	public EType getEtype() {
		return etype;
	}

}
