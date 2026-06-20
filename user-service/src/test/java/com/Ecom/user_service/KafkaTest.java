package com.Ecom.user_service;

import com.Ecom.user_service.Dto.UserCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "user-created" })
class KafkaTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    @BeforeEach
    void setup() {
        // Configure Kafka producer with JsonSerializer for value
        Map<String, Object> producerProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
    }

    @Test
    void testUserCreatedEvent() throws InterruptedException {
        // Prepare the event to send
        UserCreatedEvent user = new UserCreatedEvent();
        user.setUserId(1L);
        user.setEmail("huda@example.com");

        // Consumer setup
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Trust all packages for deserialization

        DefaultKafkaProducerFactory<String, UserCreatedEvent> producerFactory = new DefaultKafkaProducerFactory<>(Map.of());
        DefaultKafkaConsumerFactory<String, UserCreatedEvent> cf =
                new DefaultKafkaConsumerFactory<>(consumerProps,
                        new StringDeserializer(),
                        new JsonDeserializer<>(UserCreatedEvent.class, false));

        ContainerProperties containerProps = new ContainerProperties("user-created");

        BlockingQueue<ConsumerRecord<String, UserCreatedEvent>> records = new LinkedBlockingQueue<>();
        KafkaMessageListenerContainer<String, UserCreatedEvent> container =
                new KafkaMessageListenerContainer<>(cf, containerProps);

        container.setupMessageListener((MessageListener<String, UserCreatedEvent>) records::add);
        container.start();

        // Wait for container assignment
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // Send the event
        kafkaTemplate.send("user-created", user);

        // Receive the message
        ConsumerRecord<String, UserCreatedEvent> received = records.poll(5, TimeUnit.SECONDS);

        // Assertions
        assertThat(received).isNotNull();
        assertThat(received.value().getUserId()).isEqualTo(user.getUserId());
        assertThat(received.value().getEmail()).isEqualTo(user.getEmail());

        container.stop();
    }
}
