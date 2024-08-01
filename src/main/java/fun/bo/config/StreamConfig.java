package fun.bo.config;

import fun.bo.consumer.MessageConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

/**
 * @author xiaobo
 */
@Configuration
public class StreamConfig {

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory, MessageConsumer messageConsumer) {

        // 用于配置消息监听容器的选项。在这个方法中，通过设置不同的选项，如轮询超时时间和消息的目标类型，可以对消息监听容器进行个性化的配置。
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        // 设置了轮询超时的时间为100毫秒。这意味着当没有新的消息时，容器将每隔100毫秒进行一次轮询。
                        .pollTimeout(Duration.ofMillis(100))
                        // 指定了消息的目标类型为 String。这意味着容器会将接收到的消息转换为 String 类型，以便在后续的处理中使用。
                        .targetType(String.class)
                        .build();

        // 创建一个可用于监听Redis流的消息监听容器。
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer =
                StreamMessageListenerContainer.create(connectionFactory, options);

        // 方法配置了容器来接收来自特定消费者组和消费者名称的消息。它还指定了要读取消息的起始偏移量，以确定从哪里开始读取消息。
        listenerContainer.receive(
                Consumer.from("your-consumer-group", "your-consumer-name"),
                StreamOffset.create("your-stream-name", ReadOffset.lastConsumed()), messageConsumer);

        // 方法启动了消息监听容器，使其开始监听消息。一旦容器被启动，它将开始接收并处理来自Redis流的消息。
        listenerContainer.start();

        return listenerContainer;
    }
}
