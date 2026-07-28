package com.tashin.physicsai.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class Value {

    private String messaging_product;

    private Metadata metadata;

    private List<Contact> contacts;

    private List<Message> messages;

}
