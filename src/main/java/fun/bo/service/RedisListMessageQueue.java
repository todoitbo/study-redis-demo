package fun.bo.service;

import io.lettuce.core.RedisCommandTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author todoitbo
 * @date 2023/12/19
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisListMessageQueue {

    private final RedisTemplate<String,String> redisTemplate;

    private final ExecutorService messageProcessorExecutor;

    @Value("classpath:/lua/list_queue.lua")
    private Resource luaResource;

    private final String sourceList = "messageQueue";
    private final String backupList = "backupQueue";


    /**
     * 推送消息到源List
     *
     * @param message 消息内容
     */
    public void pushMessage(String message) {
        redisTemplate.opsForList().rightPush(sourceList, message);
    }

    /**
     * 从源List弹出消息并推入备份List
     *
     * @return 弹出的消息
     */
    public String popMessage() {
        //
        String rightPopAndLeftPush  = "";
        try {
            rightPopAndLeftPush = redisTemplate.opsForList().rightPopAndLeftPush(sourceList, backupList, 3, TimeUnit.SECONDS);
        }catch (RedisCommandTimeoutException e) {
            // 处理超时异常，例如记录日志或执行其他逻辑
            System.err.println("Redis command timed out");
        }
        return rightPopAndLeftPush;
    }

    public void popMessagePersistence() {
        String rightPopAndLeftPush = null;
        try {
            rightPopAndLeftPush = redisTemplate.opsForList().leftPop(sourceList, 3, TimeUnit.SECONDS);
            if (rightPopAndLeftPush != null) {
                log.info("处理消息: {}" , rightPopAndLeftPush);
            }
        }catch (Exception e) {
            // 处理超时异常，进行持久化操作，消息没处理可进行后续处理
            if (rightPopAndLeftPush != null) {
                redisTemplate.opsForList().leftPush(backupList, rightPopAndLeftPush);
            }
            log.error(e.getMessage());
        }
    }

    /**
     * 处理消息的示例逻辑
     */
    public void processMessages() {
        while (true) {
            // 使用BRPOPLPUSH命令，原子性地从源List弹出消息并推入备份List
            String message = popMessage();

            // 处理消息（用实际的消息处理逻辑替换此部分）
            if (message != null) {
                log.info("处理消息: {}" , message);
                // 实际的消息处理逻辑在这里
            }

            // 休眠一段时间，避免忙等
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void startProcessingMessages() {
        messageProcessorExecutor.execute(() -> {
            while (true) {
                popMessagePersistence();
            }
        });
    }

    public void startProcessingMessagesUseScheduled() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::popMessagePersistenceLua, 0, 1, TimeUnit.SECONDS);
    }

    public static <T> RedisScript<T> loadRedisScript(Resource scriptResource, Class<T> elementClass) {
        String scriptContent = null;
        try {
            scriptContent = new String(Files.readAllBytes(scriptResource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException("读取Lua脚本文件失败");
        }
        return new DefaultRedisScript<>(scriptContent, elementClass);
    }

    public void popMessagePersistenceLua() {
        String rightPopAndLeftPush = null;
        try {
            RedisScript<String> stringRedisScript = loadRedisScript(luaResource, String.class);
            rightPopAndLeftPush = redisTemplate.execute(
                stringRedisScript,
                Collections.singletonList(sourceList)
            );
            log.info("处理消息: {}" , rightPopAndLeftPush);
        }catch (Exception e) {
            // 处理超时异常，进行持久化操作
            if (rightPopAndLeftPush != null) {
                redisTemplate.opsForList().leftPush(backupList, rightPopAndLeftPush);
            }
            log.error(e.getMessage());
        }
    }
}
