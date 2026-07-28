package com.tashin.physicsai.dto.request;

import lombok.Data;

@Data
public class Message {

    private Image image;

    private Document document;

    private Audio audio;

    private String from;

    private String id;

    private String timestamp;

    private String type;

    private Text text;

}
