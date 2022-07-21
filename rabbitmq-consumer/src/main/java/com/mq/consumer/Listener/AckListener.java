package com.mq.consumer.Listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;



//@Component
public class AckListener implements ChannelAwareMessageListener {
//    @RabbitListener(queues = "queue_confirm")

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        Thread.sleep(2000);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //1
            System.out.println(new String(message.getBody()));
            //2
            System.out.println("处理业务逻辑...");

            //3.消息处理成功 签收
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            //出现异常 即消息处理失败 拒收 broker重新发送给consumer
            //requeue true消息重新回到对象 broker重新发送
            channel.basicNack(deliveryTag,true,true);
        }
    }
}
