package com.example.reactivekafkaconsumerandproducer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(topics = {"${FAKE_PRODUCER_DTO_TOPIC}", "${FAKE_CONSUMER_DTO_TOPIC}"})
class ReactivekafkaconsumerandproducerApplicationTests {

	@Test
	void main_contextLoads_DoesNotThrow() {
		assertDoesNotThrow(() -> ReactivekafkaconsumerandproducerApplication.main(new String[]{"--server.port=0"}));
	}

}