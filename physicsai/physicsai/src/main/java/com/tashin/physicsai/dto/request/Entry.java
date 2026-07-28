package com.tashin.physicsai.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class Entry {

    private String id;

    private List<Change> changes;

}