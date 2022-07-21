package com.mq.consumer.Listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 限流机制
 *
 */
@Component
public class QosListener implements ChannelAwareMessageListener {
    @Override
    @RabbitListener(queues = "queue_confirm")
    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println(new String(message.getBody()));

//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
    }
}
