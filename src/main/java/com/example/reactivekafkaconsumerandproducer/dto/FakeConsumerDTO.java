package com.example.reactivekafkaconsumerandproducer.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("FakeConsumer")
public class FakeConsumerDTO {
    @JsonProperty("id")
    private String id;

    public FakeConsumerDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FakeConsumerDTO{" +
                "id='" + id + '\'' +
                '}';
    }
}