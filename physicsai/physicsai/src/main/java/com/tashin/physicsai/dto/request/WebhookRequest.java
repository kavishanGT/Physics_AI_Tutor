package com.tashin.physicsai.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class WebhookRequest {

    private String object;

    private List<Entry> entry;

}
