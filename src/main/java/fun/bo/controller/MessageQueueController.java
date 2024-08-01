package fun.bo.controller;

import fun.bo.service.RedisListMessageQueue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author todoitbo
 * @date 2023/12/19
 */
@RestController
@RequestMapping("queue")
public class MessageQueueController {

    @Resource
    private RedisListMessageQueue redisListMessageQueue;

    @RequestMapping("list/{message}")
    public String messageQueue(@PathVariable String message) {
        redisListMessageQueue.pushMessage(message);
        return "success";
    }
}
