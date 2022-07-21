package com.mq.consumer.Listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AckListener {
    @RabbitListener(queues = "queue_confirm")
    public void listener(Message message){
        System.out.println(message.getBody());
    }

}
