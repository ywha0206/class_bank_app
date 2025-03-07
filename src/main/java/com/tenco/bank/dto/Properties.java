package com.tenco.bank.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

// JSON 형식에 코딩 컨벤션이 스네이크 케이스를 카멜 노테이션으로 할당하라 !!
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class Properties {

    private String nickname;
    private String profileImage;
    private String thumbnailImage;
}