package com.Ecom.user_service.config;

import com.Ecom.user_service.Dto.UserCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
/*
* Purpose: Handles errors when consuming messages from Kafka.

If a consumer fails to process a message (exception is thrown), this bean decides what to do next.

In your code:

It uses a DeadLetterPublishingRecoverer, which sends failed messages to a Dead Letter Topic (DLT):
<original-topic>.DLT
This way, you don’t lose messages — they are saved for later inspection or reprocessing.

ExponentialBackOffWithMaxRetries is used:

Tries 3 times before giving up.

Wait time between retries grows exponentially: 1s → 2s → 4s → 8s… (up to 10s max).

✅ Summary: This bean ensures resilience in your Kafka consumer — retries failed messages and eventually sends unprocessable messages to DLT instead of crashing the consumer
* */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        // This recoverer automatically sends to <original-topic>.DLT
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new org.apache.kafka.common.TopicPartition(record.topic() + ".DLT", record.partition())
        );

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10000L);

        return new DefaultErrorHandler(recoverer, backOff);
    }
    /*
    * This bean tells how to create a kafka consumer */
    @Bean
    public ConsumerFactory<String, UserCreatedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-created-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(UserCreatedEvent.class).ignoreTypeHeaders().trustedPackages("*"));
    }
/*
ells Spring Kafka how to run listeners.

Uses the ConsumerFactory to create consumers.

Attaches the DefaultErrorHandler (your retry + DLT logic).

Configures concurrency (factory.setConcurrency(3)):

Creates 3 threads to process messages in parallel.

Sets acknowledgment mode:

AckMode.MANUAL: Message offsets are committed only if no error occurs, preventing message loss or duplicate processing.
* */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, UserCreatedEvent> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler); // ✅ proper way
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // safer unless you want manual
        //message gets commited only if method doesn't return any error if it does then offset is not commmited
        return factory;
    }
    @Bean
    public NewTopic userCreated() {
        return TopicBuilder.name("user-created")   // MAIN topic
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userCreatedDlt() {
        return TopicBuilder.name("user-created.DLT") // DLT topic
                .partitions(3)
                .replicas(1)
                .build();
    }





}

