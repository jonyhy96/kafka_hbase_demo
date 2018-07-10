package cn.ghostcloud.demo.kafka;

import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@EnableKafka
public class Consumer {
    @KafkaListener(topics = "rangeEvent")
    public void rangeEvent(Object payload) {
        log.info("Get message from rangeEvent : {}", payload.toString());
    }

    @KafkaListener(topics = "driverTiredEvent")
    public void driverTiredEvent(Object payload) {
        log.info("Get message from driverTiredEvent : {}", payload.toString());
    }
    @KafkaListener(topics = "testtopic")
    public void unknownEvent(Object payload) {
        log.info("Get message from testtopic : {}", payload.toString());
    }
}
