package com.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class DLXConfig {
    public static final String QUEUE_NAME="test_queue_dlx";
    public static final String EXCHANGE_NAME="test_exchange_dlx";

    //死信队列交换机
    public static final String DLX_QUEUE_NAME="queue_dlx";
    public static final String DLX_EXCHANGE_NAME="exchange_dlx";

    @Bean("test_queue_dlx")
    public Queue queue(){
        return QueueBuilder.durable(QUEUE_NAME)
                //正常队列绑定死信交换机
                .deadLetterExchange(DLX_EXCHANGE_NAME)
                .deadLetterRoutingKey("dlx.hehe")
                //实现死信队列
                // 1.队列过期时间
                .ttl(10000)
                // 2.队列长度限制 max-length
                .maxLength(10)
                // 3.
                .build();
    }

    @Bean("test_exchange_dlx")
    public Exchange exchange(){
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Binding binding(@Qualifier("test_queue_dlx")Queue queue, @Qualifier("test_exchange_dlx")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange)
                .with("test.dlx.#").noargs();
    }



    @Bean("queue_dlx")
    public Queue queuedlx(){
        return QueueBuilder.durable(DLX_QUEUE_NAME)
                .build();
    }

    @Bean("exchange_dlx")
    public Exchange exchangedlx(){
        return ExchangeBuilder
                .topicExchange(DLX_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Binding dlxbinding(@Qualifier("queue_dlx")Queue queue, @Qualifier("exchange_dlx")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange)
                .with("dlx.#").noargs();
    }
}
