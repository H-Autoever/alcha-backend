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
    public ConcurrentKafkaListenerContainerFactory<String, RealtimeAppDataMessage> kafkaListenerContainerFactory() { // kafka는 kafkaListenerContainerFactory 라는 이름으로 Bean을 자동으로 등록하기 때문에 다른 이름을 쓰려면 @KafkaListener에 containerFactory = "orderKafkaListenerContainerFactory" 이렇게 명시해야 함
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
    public ConcurrentKafkaListenerContainerFactory<String, PeriodicAppDataMessage> periodicKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PeriodicAppDataMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(periodicAppDataConsumerFactory());
        return factory;
    }
}