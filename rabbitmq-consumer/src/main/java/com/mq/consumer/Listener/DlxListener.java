package com.mq.consumer.Listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 死信队列
 *  消费者拒收消息，不重发消息
 *
 */
@Component
public class DlxListener implements ChannelAwareMessageListener {
    @Override
    @RabbitListener(queues = "test_queue_dlx")
    public void onMessage(Message message, Channel channel) throws Exception {
        Thread.sleep(2000);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //1
            System.out.println(new String(message.getBody()));
            //2
            System.out.println("处理业务逻辑...");
            int i=3/0;
            //3.消息处理成功 签收
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            //出现异常 即消息处理失败 拒收 broker重新发送给consumer
            //requeue true消息重新回到对象 broker重新发送
            System.out.println("出现异常拒绝接收");
            channel.basicNack(deliveryTag,true,false); //不重回队列
        }
    }
}
