package com.example.beyond.ordersystem.ordering.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StockDecreaseEventHandler {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(){
        rabbitTemplate.convertAndSend(aaaa, xxx);
    }

    @Transactional
    @RabbitListener(queues = )
    public void listen(){

    }
}
