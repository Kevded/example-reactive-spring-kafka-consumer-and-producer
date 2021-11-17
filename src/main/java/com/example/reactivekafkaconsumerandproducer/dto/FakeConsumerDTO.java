package com.example.reactivekafkaconsumerandproducer.dto;

public class FakeConsumerDTO {

    private String id;

    public FakeConsumerDTO() {}

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