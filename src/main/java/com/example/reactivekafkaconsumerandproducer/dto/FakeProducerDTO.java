package com.example.reactivekafkaconsumerandproducer.dto;

public class FakeProducerDTO {

    private String id;

    public FakeProducerDTO(String id) {
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
        return "FakeProducerDTO{" +
                "id='" + id + '\'' +
                '}';
    }
}