package com.zabeer.kafka.streams.scheduling;


import com.zabeer.kafka.streams.model.DebitCredit;
import com.zabeer.kafka.streams.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static com.zabeer.kafka.streams.constants.Constants.TXN_INPUT_TOPIC;

@Component
@Slf4j
@EnableScheduling
public class TransactionSimulator {

    private static final int MIN_ID = 100;
    private static final int MAX_ID = 500;
    KafkaProducer<String, Payment> kafkaProducer;

    public static long MIN_AMOUNT = 5;
    public static long MAX_AMOUNT = 100000;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        kafkaProducer = new KafkaProducer<>(props);
    }


    @Scheduled(fixedRate = 5000)
    public void simulatePayment() {
        try {

            Payment txnObj = getPaymentTxnObj();
            ProducerRecord<String, Payment> record = new ProducerRecord<>(TXN_INPUT_TOPIC, txnObj.getCustAccountNumber(), txnObj);

            kafkaProducer.send(record);

            System.out.printf("Produced txn message with key = %s and value = %s \n", txnObj.getCustAccountNumber(), txnObj.toString());
        }
        catch(Exception e) {
            log.error("exception producing msg", e);
        }

    }

    private Payment getPaymentTxnObj() {
        Random random = new Random();
        Long amount = random.longs(MIN_AMOUNT, (MAX_AMOUNT + 1)).findFirst().getAsLong();
        Integer custAcctNumber = random.ints(MIN_ID, (MAX_ID + 1)).findFirst().getAsInt();
        return Payment.builder().txnId(UUID.randomUUID().toString()).
                txnType(DebitCredit.CREDIT).txnDate(new Date()).
                txnAmount(BigDecimal.valueOf(amount)).custAccountNumber(custAcctNumber.toString()).build();

    }


    @PreDestroy
    public void destroy() {
        kafkaProducer.close();
    }


}
