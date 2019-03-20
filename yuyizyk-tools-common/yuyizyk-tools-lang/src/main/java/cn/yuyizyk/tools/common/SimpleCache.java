package cn.yuyizyk.tools.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 简单缓存，无超时实现，使用{@link WeakHashMap}实现缓存自动清理
 * <p>
 * 参考：
 * <ul>
 * <li>https://github.com/looly/hutool/tree/v4-master/hutool-core/src/main/java/cn/hutool/core/util</li>
 * </ul>
 * </p>
 * *
 * 
 * @param <K>
 *            键类型
 * @param <V>
 *            值类型
 */
public class SimpleCache<K, V> implements Map<K, V>, Serializable {
	private static final long serialVersionUID = 8756337988757328743L;

	/** 池 */
	private final Map<K, V> cache = new WeakHashMap<>();

	private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
	private final ReadLock readLock = cacheLock.readLock();
	private final WriteLock writeLock = cacheLock.writeLock();

	/**
	 * 从缓存池中查找值
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	@Override
	public V get(Object key) {
		// 尝试读取缓存
		readLock.lock();
		V value;
		try {
			value = cache.get(key);
		} finally {
			readLock.unlock();
		}
		return value;
	}

	/**
	 * 放入缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 值
	 */
	@Override
	public V put(K key, V value) {
		writeLock.lock();
		try {
			cache.put(key, value);
		} finally {
			writeLock.unlock();
		}
		return value;
	}

	/**
	 * 移除缓存
	 * 
	 * @param key
	 *            键
	 * @return 移除的值
	 */
	@Override
	public V remove(Object key) {
		writeLock.lock();
		try {
			return cache.remove(key);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 清空缓存池
	 */
	@Override
	public void clear() {
		writeLock.lock();
		try {
			this.cache.clear();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int size() {
		readLock.lock();
		try {
			return this.cache.size();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		readLock.lock();
		try {
			return cache.isEmpty();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		readLock.lock();
		try {
			return cache.containsKey(key);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean containsValue(Object value) {
		readLock.lock();
		try {
			return cache.containsValue(value);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		writeLock.lock();
		try {
			this.cache.putAll(m);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Set<K> keySet() {
		readLock.lock();
		try {
			return cache.keySet();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Collection<V> values() {
		readLock.lock();
		try {
			return cache.values();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		readLock.lock();
		try {
			return cache.entrySet();
		} finally {
			readLock.unlock();
		}
	}
}
