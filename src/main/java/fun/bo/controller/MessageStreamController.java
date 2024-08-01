package fun.bo.controller;

import fun.bo.dto.MessageDto;
import fun.bo.produce.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaobo
 */
@RestController
@RequestMapping("/stream")
@RequiredArgsConstructor
public class MessageStreamController {

    private final MessageProducer messageProducer;

    @PostMapping("/send")
    public String sendMessage(@RequestBody MessageDto messageDto) {
        messageProducer.sendMessage("your-stream-name", messageDto.getKey(), messageDto.getMessage());
        return "Message sent successfully!";
    }
}
