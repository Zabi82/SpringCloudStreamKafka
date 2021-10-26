package com.zabeer.kafka.streams;

import com.zabeer.kafka.streams.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;


@SpringBootApplication
@EnableScheduling
@Slf4j
public class KafkaStreamApplication {

    public static final BigDecimal THRESHOLD_AMT = BigDecimal.valueOf(25000);


    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamApplication.class, args);
    }



    @Bean
    public Consumer<KStream<String, Payment>> peekTxn() {
        return input -> {
            input.peek(((key, value) -> log.info("key for txn input topic message is {} and value {} ", key, value.toString())));
        };
    }

    @Bean
    public Function<KStream<String, Payment>, KStream<String, Payment>> filterHighValueTxn(){
       return input -> input.filter((k, v) -> THRESHOLD_AMT.compareTo(v.getTxnAmount()) < 0);
    }

    @Bean
    public Consumer<KStream<String, Payment>> peekHighValueTxn() {
        return input -> {
            input.peek(((key, value) -> log.info("key for high value txn topic message is {} and value {} ", key, value.toString())));
        };
    }





}
