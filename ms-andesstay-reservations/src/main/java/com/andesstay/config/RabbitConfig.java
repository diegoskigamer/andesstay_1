package com.andesstay.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología de RabbitMQ para comandos asíncronos (email, housekeeping,
 * voucher), tal como describe el caso: exchanges cmd.direct / cmd.topic,
 * con sus colas y una Dead Letter Queue (DLQ) por si el consumidor falla.
 */
@Configuration
public class RabbitConfig {

    public static final String CMD_DIRECT_EXCHANGE = "cmd.direct";
    public static final String CMD_TOPIC_EXCHANGE = "cmd.topic";

    public static final String EMAIL_QUEUE = "q.cmd.email";
    public static final String HOUSEKEEPING_QUEUE = "q.cmd.housekeeping";
    public static final String VOUCHER_QUEUE = "q.cmd.voucher";
    public static final String DLQ = "q.cmd.dlq";

    @Bean
    DirectExchange cmdDirectExchange() {
        return new DirectExchange(CMD_DIRECT_EXCHANGE);
    }

    @Bean
    TopicExchange cmdTopicExchange() {
        return new TopicExchange(CMD_TOPIC_EXCHANGE);
    }

    @Bean
    Queue dlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    private Queue withDlq(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    Queue emailQueue() {
        return withDlq(EMAIL_QUEUE);
    }

    @Bean
    Queue housekeepingQueue() {
        return withDlq(HOUSEKEEPING_QUEUE);
    }

    @Bean
    Queue voucherQueue() {
        return withDlq(VOUCHER_QUEUE);
    }

    @Bean
    Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(cmdDirectExchange()).with("email.send");
    }

    @Bean
    Binding housekeepingBinding() {
        return BindingBuilder.bind(housekeepingQueue()).to(cmdTopicExchange()).with("housekeeping.*");
    }

    @Bean
    Binding voucherBinding() {
        return BindingBuilder.bind(voucherQueue()).to(cmdDirectExchange()).with("voucher.gen");
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
