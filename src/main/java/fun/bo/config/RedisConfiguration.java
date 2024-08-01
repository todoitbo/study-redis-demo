package fun.bo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xiaobo
 */
@Configuration
@Slf4j
public class RedisConfiguration {


    @Bean
    public RedisTemplate<String, byte[]> byteRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("创建针对二进制数据的redis模板对象");
        RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        configureRedisTemplate(redisTemplate, redisConnectionFactory, RedisSerializer.byteArray());
        return redisTemplate;
    }

    @Bean
    public <K, V> RedisTemplate<K, V> genericRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("创建通用的redis模板对象");
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        configureRedisTemplate(redisTemplate, redisConnectionFactory, new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    private <K, V> void configureRedisTemplate(RedisTemplate<K, V> redisTemplate,
                                               RedisConnectionFactory redisConnectionFactory,
                                               RedisSerializer<?> valueSerializer) {
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
    }

    @Bean
    public ExecutorService messageProcessorExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}