package com.mq.consumer.Listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 延迟队列
 *
 */
@Component
public class OrderListener implements ChannelAwareMessageListener {
    @Override
    //监听死信队列！！！
    @RabbitListener(queues = "order_queue_dlx")
    public void onMessage(Message message, Channel channel) throws Exception {
        Thread.sleep(2000);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //1
            System.out.println(new String(message.getBody()));
            //2
            System.out.println("处理业务逻辑...");
            System.out.println("根据订单信息查询订单状态...");
            System.out.println("判断状态是否为支付成功...");
            System.out.println("取消订单，回滚库存...");
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
