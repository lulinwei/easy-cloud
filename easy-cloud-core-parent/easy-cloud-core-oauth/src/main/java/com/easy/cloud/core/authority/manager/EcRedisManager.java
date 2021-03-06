package com.easy.cloud.core.authority.manager;

import java.util.Set;

import org.crazycake.shiro.RedisManager;

import com.easy.cloud.core.common.string.utils.EcStringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * <p>
 * 集成redisManage
 * </p>
 *
 * <pre>
 *  说明：使用自定义的redisManager
 *  约定：
 *  命名规范：
 *  使用示例：
 * </pre>
 *
 * @author daiqi
 * @创建时间 2018年5月25日 下午4:52:13
 */
@SuppressWarnings("deprecation")
public class EcRedisManager extends RedisManager {

	private static JedisPool jedisPool;
	private JedisPoolConfig jedisPoolConfig;

	public EcRedisManager() {
		this(new JedisPoolConfig());
	}

	public EcRedisManager(JedisPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}

	/**
	 * 重写init方法 不做任何操作
	 */
	public void init() {
		initJedispool();
	}

	private void initJedispool() {
		if (jedisPool == null) {
			if (EcStringUtils.isNotEmpty(getPassword())) {
				jedisPool = new JedisPool(jedisPoolConfig, getHost(), getPort(), getTimeout(), getPassword());
			} else if (getTimeout() != 0) {
				jedisPool = new JedisPool(jedisPoolConfig, getHost(), getPort(), getTimeout());
			} else {
				jedisPool = new JedisPool(jedisPoolConfig, getHost(), getPort());
			}
		}
	}

	/**
	 * get value from redis
	 * 
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key) {
		byte[] value = null;
		Jedis jedis = jedisPool.getResource();
		try {
			value = jedis.get(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * set
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] set(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key, value);
			if (getExpire() != 0) {
				jedis.expire(key, this.getExpire());
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * set
	 * 
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public byte[] set(byte[] key, byte[] value, int expire) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key, value);
			if (expire != 0) {
				jedis.expire(key, expire);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * del
	 * 
	 * @param key
	 */
	public void del(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * flush
	 */
	public void flushDB() {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.flushDB();
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * size
	 */
	public Long dbSize() {
		Long dbSize = 0L;
		Jedis jedis = jedisPool.getResource();
		try {
			dbSize = jedis.dbSize();
		} finally {
			jedisPool.returnResource(jedis);
		}
		return dbSize;
	}

	/**
	 * keys
	 * 
	 * @param regex
	 * @return
	 */
	public Set<byte[]> keys(String pattern) {
		Set<byte[]> keys = null;
		Jedis jedis = jedisPool.getResource();
		try {
			keys = jedis.keys(pattern.getBytes());
		} finally {
			jedisPool.returnResource(jedis);
		}
		return keys;
	}

	public JedisPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}
}
