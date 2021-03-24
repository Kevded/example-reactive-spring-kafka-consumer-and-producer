![Java CI](https://github.com/Kevded/example-reactive-spring-kafka-consumer-and-producer/workflows/Java%20CI/badge.svg)

# Reactive Kafka consumer and producer Spring Boot

Sample project to show how to implement Reactive kafka consumer and producer in Spring Boot. With Spring Kafka.

Spring Boot version 2.3.9
## Config
- [ReactiveKafkaConsumerConfig](src/main/java/com/example/reactivekafkaconsumerandproducer/config/ReactiveKafkaConsumerConfig.java)
- [ReactiveKafkaProducerConfig](src/main/java/com/example/reactivekafkaconsumerandproducer/config/ReactiveKafkaProducerConfig.java)
- [application.properties](src/main/resources/application.properties)
- [pom.xml](pom.xml)

```java
package com.example.reactivekafkaconsumerandproducer.config;

import com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class ReactiveKafkaConsumerConfig {
    @Bean
    public ReceiverOptions<String, FakeConsumerDTO> kafkaReceiverOptions(@Value(value = "${FAKE_CONSUMER_DTO_TOPIC}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, FakeConsumerDTO> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO> reactiveKafkaConsumerTemplate(ReceiverOptions<String, FakeConsumerDTO> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO>(kafkaReceiverOptions);
    }
}
```

```java
package com.example.reactivekafkaconsumerandproducer.config;

import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class ReactiveKafkaProducerConfig {
    @Bean
    public ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate(
            KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<String, FakeProducerDTO>(SenderOptions.create(props));
    }
}
```

```properties
spring.kafka.bootstrap-servers=localhost:9200
# producer
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonDeserializer
# consumer
spring.kafka.consumer.group-id=reactivekafkaconsumerandproducer

spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
# json deserializer config
spring.kafka.properties.spring.json.trusted.packages=*
spring.kafka.consumer.properties.spring.json.use.type.headers=false
spring.kafka.consumer.properties.spring.json.value.default.type=com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO

# topic
FAKE_PRODUCER_DTO_TOPIC=fake_producer_dto_topic
FAKE_CONSUMER_DTO_TOPIC=fake_consumer_dto_topic
```

```xml
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>io.projectreactor.kafka</groupId>
            <artifactId>reactor-kafka</artifactId>
            <version>${reactor.kafka.version}</version>
        </dependency>
```

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