package com.example.deliveryService.domain;

import org.springframework.hateoas.Link;

import java.util.List;

public class ResponseMessage {

    private String message;
    private List<Link> links;

    public ResponseMessage(String message, Link... links) {
        this.message = message;
        this.links = List.of(links);
    }

    public String getMessage() {
        return message;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
