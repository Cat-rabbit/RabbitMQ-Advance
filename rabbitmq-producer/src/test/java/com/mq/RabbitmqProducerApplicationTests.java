package com.mq;

import com.mq.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitmqProducerApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testConfirm() {
        //定义回调函数
        /**
         * correlationData 相关配置信息
         * ack echange交换机是否收到消息 true or false
         * cause 失败原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("confirm方法被执行了.....");
            if (ack) System.out.println("接受消息成功"+ack);
            //做一些处理 保证能发送成功
            else System.out.println("接受消息成功失败"+ack);
        });
        //发送消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"confirm","测试confirm");
    }

    /**
     * 回退模式
     *  Exchange路由到Queue失败才会执行 ReturnCallBack
     */
    @Test
    void testReturn(){
        //设置交换机处理失败消息的模式
        rabbitTemplate.setMandatory(true);
        /**
         * returnedMessage:
         * message 消息对象
         * replyCode 错误码
         * replyText 错误信息
         * exchange
         * routingKey
         */
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
                System.out.println("return方法被执行了.....");
            System.out.println("message:"+ returnedMessage.getMessage());
            System.out.println("replyCode:"+ returnedMessage.getReplyCode());
            System.out.println("replyText:"+ returnedMessage.getReplyText());
            System.out.println("exchange:"+ returnedMessage.getExchange());
            System.out.println("routingKey:"+ returnedMessage.getRoutingKey());

        });
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"confirm","测试confirm");
    }
}
