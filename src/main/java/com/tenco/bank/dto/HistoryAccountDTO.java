package com.tenco.bank.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

// Join 결과를 매핑할 모델은 dto로 설계하는 것이 일반적
@Data
public class HistoryAccountDTO {
    private Integer id;
    private Long amount;
    private Long balance;
    private String sender;
    private String receiver;
    private Timestamp createdAt;


    public String formatKoreanWon(Long balance) {
        return String.format("%,d원", balance);
    }
    public String timestampToString(Timestamp createdAt) {
        return new SimpleDateFormat("yyyy-MM-dd").format(createdAt);
    }
}

