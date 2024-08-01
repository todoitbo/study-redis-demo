package fun.bo.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Component;

/**
 * @author todoitbo
 * @date 2023/12/20
 */
@RequiredArgsConstructor
@Component
public class InitUtil {

    private final RedisTemplate<String,String> redisTemplate;

    public void initializeStream() {

        StreamOperations<String, Object, Object> streamOperations = redisTemplate.opsForStream();

        // 创建一个流
        try {
            streamOperations.createGroup("your-stream-name", ReadOffset.from("0"), "your-consumer-group");
        } catch (Exception e) {
            // 流可能已存在，忽略异常
        }
    }

    public void initializeConsumerGroup(String streamName, String consumerGroupName) {

        StreamOperations<String, Object, Object> streamOperations = redisTemplate.opsForStream();

        // 创建消费者组
        try {
            streamOperations.createGroup(streamName, ReadOffset.from("0"), consumerGroupName);
        } catch (Exception e) {
            // 消费者组可能已存在，忽略异常
        }
    }
}
