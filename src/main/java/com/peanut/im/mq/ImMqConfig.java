package com.peanut.im.mq;

import org.springframework.amqp.core.*;
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
    public static final String QUEUE_SINGLE_PREFIX = "im.single.queue";
    public static final String SINGLE_ROUNTING_KEY_PREFIX = "im.single.";
    public static final String GROUP_EXCHANGE = "im.group.topic";
    public static final String GROUP_QUEUE_PREFIX = "im.queue.group"; // 群聊队列
    public static final String GROUP_ROUNTING_KEY_PREFIX = "im.group.";
    public static final int GROUP_QUEUE_COUNT = 5;
    public static final int SINGLE_QUEUE_COUNT = 5;

    @Bean
    public DirectExchange imExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Declarables singleQueuesAndBindings() {
        Declarable[] declarables = new Declarable[SINGLE_QUEUE_COUNT * 2];
        for (int i = 0; i < SINGLE_QUEUE_COUNT; i++) {
            String queueName = QUEUE_SINGLE_PREFIX + i;
            String routingKey = SINGLE_ROUNTING_KEY_PREFIX + i;

            Queue queue = new Queue(queueName, true);
            Binding binding = BindingBuilder.bind(queue)
                    .to(imExchange())
                    .with(routingKey);

            declarables[i] = queue;
            declarables[i + SINGLE_QUEUE_COUNT] = binding;
        }
        return new Declarables(declarables);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange groupExchange() {
        return new TopicExchange(ImMqConfig.GROUP_EXCHANGE, true, false);
    }

    // 2. 批量注册分片队列及Binding
    @Bean
    public Declarables groupQueuesAndBindings() {
        Declarable[] declarables = new Declarable[GROUP_QUEUE_COUNT * 2];
        for (int i = 0; i < GROUP_QUEUE_COUNT; i++) {
            String queueName = GROUP_QUEUE_PREFIX + i;
            String routingKey = GROUP_ROUNTING_KEY_PREFIX + i;

            Queue queue = new Queue(queueName, true);
            Binding binding = BindingBuilder.bind(queue)
                    .to(groupExchange())
                    .with(routingKey);

            declarables[i] = queue;
            declarables[i + GROUP_QUEUE_COUNT] = binding;
        }
        return new Declarables(declarables);
    }

    public static String getSingleChatRoutingKey(String userId) {
        int hash = userId.hashCode();
        int shard = (hash & 0x7fffffff) % SINGLE_QUEUE_COUNT;
        return SINGLE_ROUNTING_KEY_PREFIX + shard;
    }

    public static String getGroupChatRoutingKey(String groupId) {
        int hash = groupId.hashCode();
        int shard = (hash & 0x7fffffff) % SINGLE_QUEUE_COUNT;
        return GROUP_ROUNTING_KEY_PREFIX + shard;
    }

    public static String[] getAllSingleChatQueues() {
        String[] list = new String[ImMqConfig.SINGLE_QUEUE_COUNT];
        for (int i = 0; i < SINGLE_QUEUE_COUNT; i++) {
            list[i] = QUEUE_SINGLE_PREFIX + i;
        }
        return list;
    }

    public static String[] getAllGroupChatQueues() {
        String[] list = new String[ImMqConfig.GROUP_QUEUE_COUNT];
        for (int i = 0; i < GROUP_QUEUE_COUNT; i++) {
            list[i] = GROUP_QUEUE_PREFIX + i;
        }
        return list;
    }

}
