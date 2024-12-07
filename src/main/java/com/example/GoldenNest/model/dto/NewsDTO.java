package com.example.GoldenNest.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewsDTO {

    private String id;
    private String title;
    private String content;
    private String source;
    private List<String> mediasId;

}
