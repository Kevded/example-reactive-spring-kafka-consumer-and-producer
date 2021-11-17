package com.example.reactivekafkaconsumerandproducer.web;


import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveConsumerService;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka")
public class DemoRestController {

    private final ReactiveProducerService reactiveProducerService;
    private final ReactiveConsumerService reactiveConsumerService;
    private final Logger log = LoggerFactory.getLogger(DemoRestController.class);

    public DemoRestController(ReactiveProducerService reactiveProducerService,
                              ReactiveConsumerService reactiveConsumerService) {
        this.reactiveProducerService = reactiveProducerService;
        this.reactiveConsumerService = reactiveConsumerService;
    }

    @RequestMapping("/consume")
    public String consumeDemoData() {

        reactiveConsumerService.consumeFakeConsumerDTO().subscribe();

        return  "Consuming...";
    }


    @RequestMapping("/produce")
    public String produceDemoData() {

        FakeProducerDTO dto1=new FakeProducerDTO("1");
        FakeProducerDTO dto2=new FakeProducerDTO("2");
        FakeProducerDTO dto3=new FakeProducerDTO("3");

        reactiveProducerService.send(dto1);
        reactiveProducerService.send(dto2);
        reactiveProducerService.send(dto3);

        return  "Produced 3 NEW messages.";
    }

}
