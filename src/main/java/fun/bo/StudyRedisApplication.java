package fun.bo;

import fun.bo.service.RedisListMessageQueue;
import fun.bo.utils.InitUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author todoitbo
 * @date 2023/12/19
 */
@SpringBootApplication
public class StudyRedisApplication {

    @Resource
    private RedisListMessageQueue redisListMessageQueue;

    @Resource
    private InitUtil initUtil;
    public static void main(String[] args) {
        SpringApplication.run(StudyRedisApplication.class, args);
    }

    @PostConstruct
    public void init() {
        initUtil.initializeStream();
        initUtil.initializeConsumerGroup("your-stream-name", "your-consumer-group");
        // redisListMessageQueue.startProcessingMessages();
        // redisListMessageQueue.startProcessingMessagesUseScheduled();
    }
}