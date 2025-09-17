package com.carpoor.alchabackend.config;

import com.carpoor.alchabackend.message.PeriodicAppDataMessage;
import com.carpoor.alchabackend.message.RealtimeAppDataMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Bean
    public ConsumerFactory<String, RealtimeAppDataMessage> realtimeAppDataConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "realtime-group");
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                new JsonDeserializer<>(RealtimeAppDataMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RealtimeAppDataMessage> realtimeAppDataListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RealtimeAppDataMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(realtimeAppDataConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PeriodicAppDataMessage> periodicAppDataConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "periodic-group");
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                new JsonDeserializer<>(PeriodicAppDataMessage.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PeriodicAppDataMessage> periodicAppDataListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PeriodicAppDataMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(periodicAppDataConsumerFactory());
        return factory;
    }
}