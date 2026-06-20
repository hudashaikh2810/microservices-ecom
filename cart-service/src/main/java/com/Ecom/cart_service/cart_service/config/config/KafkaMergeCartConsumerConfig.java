package com.Ecom.cart_service.cart_service.config.config;

import com.Ecom.cart_service.cart_service.DTO.MergeCartDto;
import com.Ecom.cart_service.cart_service.DTO.UserCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaMergeCartConsumerConfig {
    @Autowired
    private DefaultErrorHandler errorHandler;


    @Bean
    public ConsumerFactory<String, MergeCartDto> consumerFactoryMergeCart() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "cart-merge-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(MergeCartDto.class).ignoreTypeHeaders().trustedPackages("*"));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MergeCartDto> kafkaListenerContainerFactoryMergeCart(
            ConsumerFactory<String, MergeCartDto> consumerFactoryMergeCart,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, MergeCartDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryMergeCart);
        factory.setCommonErrorHandler(errorHandler); // ✅ proper way
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // safer unless you want manual
        //message gets commited only if method doesn't return any error if it does then offset is not commmited
        return factory;
    }

    @Bean
    public NewTopic mergeCart() {
        return TopicBuilder.name("cart")   // MAIN topic
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic mergeCartDlt() {
        return TopicBuilder.name("cart.DLT") // DLT topic
                .partitions(3)
                .replicas(1)
                .build();
    }
}
