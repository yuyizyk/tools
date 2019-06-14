package cn.yuyizyk.tools.common;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.common.lang.Strs;


/**
 * ID 生成器
 * 
 * <pre>
 * 	时间戳保证全局ID有序 ：13882816373
 * 	3位毫秒级ID生成索引：001
 * 	2位服务ID号：02
 * </pre>
 *
 * @author yuyi
 */
public class IDGenerator {
	private final static Logger log = LoggerFactory.getLogger(Strs.class);

	private AtomicInteger index = new AtomicInteger(0);
	private AtomicLong lastTimeMillis = new AtomicLong();

	public IDGenerator(int workerId) {
		toFixedLStr(workerId, workIdlen);
		this.workerIdIndex = workerId;
	}

	/** 32 位 进制表 */ // acii
	private static final char[] digits = { //
			'0', '1', '2', '3', '4', '5', '6', '7', //
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 'w', 'x', 'y', 'z'
	};

	/** by Integer */
	static int formatUnsignedLong(long val, int shift, char[] buf, int offset, int len) {
		int charPos = len;
		int radix = 1 << shift;
		int mask = radix - 1;
		do {
			buf[offset + --charPos] = digits[((int) val) & mask];
			val >>>= shift;
		} while (val != 0 && charPos > 0);

		return charPos;
	}

	// 定长32位字符
	private static final String toFixedLStr(long val, int len) {
		assert val >= 0;
		assert val <= (digits.length ^ len);
		String str = toStr(val);// 转化为32位字符
		if (str.length() == len)
			return str;
		StringBuilder sb = new StringBuilder(len);
		for (int i = len - str.length(); i > 0; i--) {
			sb.append("0");
		}
		return sb.append(str).toString();
	}

	// 转化为32位字符
	private static final String toStr(long val) {
		return toStr(val, 5);//
	}

	private static String toStr(long val, int shift) {
		int mag = Long.SIZE - Long.numberOfLeadingZeros(val);
		int chars = Math.max(((mag + (shift - 1)) / shift), 1);
		char[] buf = new char[chars];
		formatUnsignedLong(val, shift, buf, 0, chars);
		return new String(buf);
	}

	// IDG ID
	private final int workerIdIndex;
	private static final int workIdlen = 2;

	private static final int seedlen = 3;
	// 1毫秒内能生成的ID数
	private static final int seed = (int) Math.pow(digits.length, seedlen);

	/**
	 * 时间以 2019-01-01 0：0：0 作为起始。而不是从 1970-1-1 减少时间代表的长度。
	 */
	private static final long startime = 1546272000000l;

	/**
	 * 获取当前相对 2019-05-01 的时间 毫秒数
	 * 
	 * @return
	 */
	private static final long getRelativeNowTime() {
		return System.currentTimeMillis() - startime;
	}

	public static final NumId toNumId(String idStr) {
		return NumId.byIdStr(idStr);
	}

	public static final class NumId {
		private long relativeTime = 0l;
		private int seed = 0;
		private int workId = 0;

		public long getRelativeTime() {
			return relativeTime;
		}

		public void setRelativeTime(long relativeTime) {
			this.relativeTime = relativeTime;
		}

		public int getSeed() {
			return seed;
		}

		public void setSeed(int seed) {
			this.seed = seed;
		}

		public int getWorkId() {
			return workId;
		}

		public void setWorkId(int workId) {
			this.workId = workId;
		}

		@Override
		public String toString() {
			return String.format("%d%03d%02d", relativeTime, seed, workId);
		}

		public final String toIdStr() {
			return new StringBuilder(16).append(toStr(relativeTime)).append(toFixedLStr(seed, seedlen))
					.append(toFixedLStr(workId, 2)).toString();
		}

		public static NumId byIdStr(String idStr) {
			return new NumId(idStr);
		}

		public NumId(String idStr) {
			char[] cs = idStr.toCharArray();
			int i, temp = 0, len = cs.length - (seedlen + workIdlen);
			char c = 0;
			while (temp < len) {
				i = (c = cs[temp]) - 'a';
				if (i < 0) {
					i = c - '0';
				} else
					i += 10;
				relativeTime = relativeTime * 32 + i;
				temp++;
			}
			len = cs.length - workIdlen;
			while (temp < len) {
				i = (c = cs[temp]) - 'a';
				if (i < 0) {
					i = c - '0';
				} else
					i += 10;
				seed = seed * 32 + i;
				temp++;
			}
			len = cs.length;
			while (temp < cs.length) {
				i = (c = cs[temp]) - 'a';
				if (i < 0) {
					i = c - '0';
				} else
					i += 10;
				workId = workId * 32 + i;
				temp++;
			}
		}

		public NumId() {
		}
	}

	/** 获得ID */
	public final NumId nextNumId() {
		NumId id = new NumId();
		id.setWorkId(workerIdIndex);
		long t = getRelativeNowTime(), t2;
		for (; t > (t2 = lastTimeMillis.get());) {
			if (lastTimeMillis.compareAndSet(t2, t)) {
				index.set(0);
				break;
			}
		}
		if (t < t2)
			log.error("时钟回拨");// 该情况出现在系统时钟回拨。此情况出现，则代表ID极可能重复。
		index.compareAndSet(seed, 0);
		id.setRelativeTime(t);
		id.setSeed(index.incrementAndGet());
		return id;
	}

	/** 获得ID */
	public final String nextId() {
		return nextNumId().toIdStr();
	}

	public static void main(String[] args) {
		IDGenerator g = new IDGenerator(2);
		String str = g.nextId();
		System.out.println(str);
		NumId i;
		System.out.println(i = toNumId(str));
		System.out.println(i.toIdStr());
	}
}
