package com.example.Project.domain;

import lombok.Data;
import java.util.List;

@Data
public class DeleteRequest {
    private List<Long> urlIds;
    private String userId;
}