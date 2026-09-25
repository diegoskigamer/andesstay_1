package com.andesstay.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EMAIL_QUEUE = "q.cmd.email";
    public static final String HOUSEKEEPING_QUEUE = "q.cmd.housekeeping";
    public static final String VOUCHER_QUEUE = "q.cmd.voucher";

    // Las colas y exchanges reales se declaran en ms-andesstay-reservations
    // (el productor). Este servicio solo necesita el converter para poder
    // deserializar los mensajes JSON a ReservationEvent.
    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
