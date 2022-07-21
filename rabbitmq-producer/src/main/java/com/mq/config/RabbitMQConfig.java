package com.mq.config;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME="queue_confirm";
    public static final String EXCHANGE_NAME="exchange_confirm";
    @Bean("queue_confirm")
    public Queue queue(){
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean("exchange_confirm")
    public Exchange exchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Binding binding(@Qualifier("queue_confirm")Queue queue,@Qualifier("exchange_confirm")Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("confirm").noargs();
    }
}
