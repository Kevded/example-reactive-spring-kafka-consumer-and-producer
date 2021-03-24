![Java CI](https://github.com/Kevded/example-reactive-spring-kafka-consumer-and-producer/workflows/Java%20CI/badge.svg)

# Reactive Kafka consumer and producer Spring Boot

Sample project to show how to implement Reactive kafka consumer and producer in Spring Boot. With Spring Kafka.

Spring Boot version 2.3.9

## Service
- [ReactiveConsumerService](src/main/java/com/example/reactivekafkaconsumerandproducer/service/ReactiveConsumerService.java)
- [ReactiveProducerService](src/main/java/com/example/reactivekafkaconsumerandproducer/service/ReactiveProducerService.java)

```java
package com.example.reactivekafkaconsumerandproducer.service;

import com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class ReactiveConsumerService implements CommandLineRunner {
    Logger log = LoggerFactory.getLogger(ReactiveConsumerService.class);

    private final ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO> reactiveKafkaConsumerTemplate;

    public ReactiveConsumerService(ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO> reactiveKafkaConsumerTemplate) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
    }

    private Flux<FakeConsumerDTO> consumeFakeConsumerDTO() {
        return reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                // .delayElements(Duration.ofSeconds(2L)) // BACKPRESSURE
                .doOnNext(consumerRecord -> log.info("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset())
                )
                .map(ConsumerRecord::value)
                .doOnNext(fakeConsumerDTO -> log.info("successfully consumed {}={}", FakeConsumerDTO.class.getSimpleName(), fakeConsumerDTO))
                .doOnError(throwable -> log.error("something bad happened while consuming : {}", throwable.getMessage()));
    }

    @Override
    public void run(String... args) {
        // we have to trigger consumption
        consumeFakeConsumerDTO().subscribe();
    }
}
```

```java
package com.example.reactivekafkaconsumerandproducer.service;

import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReactiveProducerService {

    private final Logger log = LoggerFactory.getLogger(ReactiveProducerService.class);
    private final ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate;

    @Value(value = "${FAKE_PRODUCER_DTO_TOPIC}")
    private String topic;

    public ReactiveProducerService(ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    public void send(FakeProducerDTO fakeProducerDTO) {
        log.info("send to topic={}, {}={},", topic, FakeProducerDTO.class.getSimpleName(), fakeProducerDTO);
        reactiveKafkaProducerTemplate.send(topic, fakeProducerDTO)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}", fakeProducerDTO, senderResult.recordMetadata().offset()))
                .subscribe();
    }
}
```

## Integration test
- [ReactiveConsumerServiceIntegrationTest](src/test/java/com/example/reactivekafkaconsumerandproducer/ReactiveConsumerServiceIntegrationTest.java)
- [ReactiveProducerServiceIntegrationTest](src/test/java/com/example/reactivekafkaconsumerandproducer/ReactiveProducerServiceIntegrationTest.java)


```java
```

```java
```
---

# Articles

- [gitbook.deddy.me/reactive-kafka-consumer-producer-spring-boot](https://gitbook.deddy.me/reactive-kafka-consumer-producer-spring-boot)