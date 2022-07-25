package com.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延迟队列实现
 * TTL+死信
 */
@Configuration
public class DelayConfig {
    public static final String QUEUE_NAME="order_queue";
    public static final String EXCHANGE_NAME="order_exchange";

    //死信队列交换机
    public static final String DLX_QUEUE_NAME="order_queue_dlx";
    public static final String DLX_EXCHANGE_NAME="order_exchange_dlx";

    @Bean("order_queue")
    public Queue queue(){
        return QueueBuilder.durable(QUEUE_NAME)
                //正常队列绑定死信交换机
                .deadLetterExchange(DLX_EXCHANGE_NAME)
                //订单取消队列routingkey
                .deadLetterRoutingKey("dlx.order.cancel")
                // 订单30分钟过期
                .ttl(10000)
                .build();
    }

    @Bean("order_exchange")
    public Exchange exchange(){
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Binding binding(@Qualifier("order_queue")Queue queue, @Qualifier("order_exchange")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange)
                .with("order.#").noargs();
    }



    @Bean("order_queue_dlx")
    public Queue queuedlx(){
        return QueueBuilder
                .durable(DLX_QUEUE_NAME)
                .build();
    }

    @Bean("order_exchange_dlx")
    public Exchange exchangedlx(){
        return ExchangeBuilder
                .topicExchange(DLX_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Binding dlxbinding(@Qualifier("order_queue_dlx")Queue queue, @Qualifier("order_exchange_dlx")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange)
                .with("dlx.order.#").noargs();
    }
}
