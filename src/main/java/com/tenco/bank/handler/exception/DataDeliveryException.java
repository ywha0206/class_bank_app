package com.tenco.bank.handler.exception;
// 사용자 정의 예외 클래스 만들기

import org.springframework.http.HttpStatus;

public class DataDeliveryException extends RuntimeException {

    private HttpStatus status;

    // 예외 발생시 Http 상태코드 알려준다
    // 메세지 (어떤 예외인지)
    public DataDeliveryException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}