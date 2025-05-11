package com.example.Project.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlSaveRequest {
    private String url;
    private String name;
    private String userId;
}
