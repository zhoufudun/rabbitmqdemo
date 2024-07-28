package com.hmily.rabbitmq.consumer.conusmer;

import com.hmily.rabbitmq.product.domain.Order;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RabbitReceiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "hello.queue1",
                    durable="true"),
            exchange = @Exchange(value = "exchange-1",
                    durable="true",
                    type= "fanout",
                    ignoreDeclarationExceptions = "true"),
            key = "key1"
    )
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        log.info("--------------------------------------");
        log.info("消费端Payload: " + message.getPayload()+", header=[send_time="+message.getHeaders().get("send_time")+", number="+message.getHeaders().get("number")+"]");
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }

    /**
     *
     * 读取自定义配置项
     *
     * 	spring.rabbitmq.listener.order.queue.name=queue-2
     spring.rabbitmq.listener.order.queue.durable=true
     spring.rabbitmq.listener.order.exchange.name=exchange-1
     spring.rabbitmq.listener.order.exchange.durable=true
     spring.rabbitmq.listener.order.exchange.type=topic
     spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions=true
     spring.rabbitmq.listener.order.key=springboot.*
     * @param order
     * @param channel
     * @param headers
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable="${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable="${spring.rabbitmq.listener.order.exchange.durable}",
                    type= "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${spring.rabbitmq.listener.order.key}"
    )
    )
    @RabbitHandler
    public void onOrderMessage(@Payload Order order,
                               Channel channel,
                               @Headers Map<String, Object> headers) throws Exception {
        log.info("--------------------------------------");
        log.info("消费端order: " + order.getId());
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }
}
