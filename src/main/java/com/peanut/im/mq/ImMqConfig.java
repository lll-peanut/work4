package com.peanut.im.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */
@Configuration
public class ImMqConfig {
    public static final String EXCHANGE = "im.direct";
    public static final String QUEUE_SINGLE = "im.chat.single.queue";
    public static final String ROUTING_SINGLE = "im.chat.single";

    @Bean
    public DirectExchange imExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue singleChatQueue() {
        return new Queue(QUEUE_SINGLE, true);
    }

    @Bean
    public Binding singleChatBinding(DirectExchange imExchange, Queue singleChatQueue) {
        return BindingBuilder.bind(singleChatQueue).to(imExchange).with(ROUTING_SINGLE);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
