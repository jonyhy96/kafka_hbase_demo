package cn.ghostcloud.demo.kafka;

import cn.ghostcloud.demo.model.Human;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Log4j2
@Service
public class Producer {
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public Producer(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ResponseEntity process(Human human){
        Gson gson = new Gson();
        try {
            kafkaTemplate.send("testtopic",gson.toJson(human)).get(3, TimeUnit.SECONDS);
            log.debug("Send to kafka success");
        } catch (ExecutionException e) {
            log.error("Connect to kafka server failed", e);
        } catch (InterruptedException e) {
            log.error("Operation has been interrupted", e);
        } catch (TimeoutException e) {
            log.error("Timeout to connect kafka server", e);
        }
        return ResponseEntity.ok("发送成功");
    }
}
