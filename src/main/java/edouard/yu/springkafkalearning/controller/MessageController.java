package edouard.yu.springkafkalearning.controller;

import edouard.yu.springkafkalearning.dto.UserDTO;
import edouard.yu.springkafkalearning.kafka.KafkaProducer;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("kafka")
@AllArgsConstructor
public class MessageController {
    private final KafkaProducer kafkaProducer;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/publish")
    public void publish(@RequestParam("message") String message){
        this.kafkaProducer.sendMessage(message);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/user/info")
    public void publish(@RequestBody UserDTO user){
        this.kafkaProducer.sendUserInformation(user);
    }
}
