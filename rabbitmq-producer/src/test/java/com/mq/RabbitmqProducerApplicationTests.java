package com.mq;

import com.mq.config.RabbitMQConfig;
import com.mq.config.TTLConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
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
            if (ack) System.out.println("接受消息成功" + ack);
                //做一些处理 保证能发送成功
            else System.out.println("接受消息成功失败" + ack);
        });
        //发送消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "confirm", "测试confirm");
    }

    /**
     * 回退模式
     * Exchange路由到Queue失败才会执行 ReturnCallBack
     */
    @Test
    void testReturn() {
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
            System.out.println("message:" + returnedMessage.getMessage());
            System.out.println("replyCode:" + returnedMessage.getReplyCode());
            System.out.println("replyText:" + returnedMessage.getReplyText());
            System.out.println("exchange:" + returnedMessage.getExchange());
            System.out.println("routingKey:" + returnedMessage.getRoutingKey());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"confirm","测试confirm");

        });
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "confirm", "测试confirm");
    }

    /**
     * 消费端限流
     *  prefetch
     */
    @Test
    void testSend() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "confirm", "测试prefetch");
        }
    }

    /**
     * TTL过期时间
     *  1.队列过期
     *  2.消息单独过期
     */
    @Test
    void testTTL() {
        //队列过期
//        for (int i = 0; i < 10; i++) {
//            rabbitTemplate.convertAndSend(TTLConfig.EXCHANGE_NAME, "ttl.hello", "测试message_ttl");
//        }
        //消息单独过期
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                rabbitTemplate.convertAndSend(TTLConfig.EXCHANGE_NAME, "ttl.hello", "测试message_ttl", new MessagePostProcessor() {
                    //消息后处理对象
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //1.设置消息过期时间
                        message.getMessageProperties().setExpiration("5000");
                        return message;
                    }
                });
            }else{
                rabbitTemplate.convertAndSend(TTLConfig.EXCHANGE_NAME, "ttl.hello", "测试message_ttl");
            }
        }
    }

    /**
     * 测试死信队列
     *  1.队列过期时间
     *  2.队列长度限制
     *  3.消息拒收
     */
    @Test
    void testDLX() {
        //1.
//        rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.hello","我是一条消息，我会死吗？");
        //2.测试队列长度限制
//        for (int i = 0; i < 20; i++) {
//            rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.hello","我是一条消息，我会死吗？");
//        }
        //3.测试拒收消息
        rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.hello","我是一条消息，我会死吗？");
    }

    /**
     * 延迟队列
     */
    @Test
    void testDelay() throws InterruptedException {
        //发送订单消息，订单系统下单成功后发送
        rabbitTemplate.convertAndSend("order_exchange","order.msg","订单信息：orderId=1,createTime='2022年7月22日11时20分15秒'");
        for (int i = 10; i >0 ; --i) {
            System.out.println(i+"...");
            Thread.sleep(1000);
        }
    }
}
