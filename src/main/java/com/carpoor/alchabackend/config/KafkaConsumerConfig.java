package com.carpoor.alchabackend.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.util.backoff.FixedBackOff;


@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    // yml의 spring.kafka.*를 모두 흡수
    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties props) {
        return new DefaultKafkaConsumerFactory<>(props.buildConsumerProperties());
    }

    // ★ 단 하나의 기본 컨테이너 팩토리
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> factory) {

        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        containerFactory.setConsumerFactory(factory);

        // 문자열(JSON) → 메서드 파라미터 타입으로 변환
        containerFactory.setRecordMessageConverter(new StringJsonMessageConverter());

        // 정책(행동)은 코드에서: 재시도/백오프/에러 핸들러/동시성 등
//        containerFactory.setConcurrency(3);
        containerFactory.setCommonErrorHandler(noRetrySkipLogger());
        // 필요 시: f.getContainerProperties().setAckMode(AckMode.MANUAL);
        // 필요 시: f.setBatchListener(true);

        return containerFactory;
    }

    // 1) 에러핸들러: 재시도 없음(0회), 로그만 남기고 스킵
    @Bean
    DefaultErrorHandler noRetrySkipLogger() {
        Logger log = LoggerFactory.getLogger("kafka-consumer");
        return new DefaultErrorHandler(
                (rec, ex) -> log.warn(
                        "KAFKA-SKIP | topic={} | partition={} | offset={} | key={} | error={} | message={}",
                        rec.topic(),
                        rec.partition(),
                        rec.offset(),
                        rec.key(),
                        ex.getClass().getSimpleName(),
                        ex.getMessage() == null ? "-" : ex.getMessage()
                ),
                new FixedBackOff(0L, 0L) // 재시도 0회, 즉시 스킵
        );
    }
}