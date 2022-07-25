package com.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class TTLConfig {
    public static final String QUEUE_NAME="queue_ttl";
    public static final String EXCHANGE_NAME="exchange_ttl";
    @Bean("queue_ttl")
    public Queue queue(){
        return QueueBuilder.durable(QUEUE_NAME)
                .ttl(100000)
                .build();
    }

    @Bean("exchange_ttl")
    public Exchange exchange(){
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Binding binding(@Qualifier("queue_ttl")Queue queue, @Qualifier("exchange_ttl")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange)
                .with("ttl.#").noargs();
    }
}
