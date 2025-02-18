package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.utils.Define;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    private Integer id;
    private String number;
    private String password;
    private Long balance;
    private Integer userId;
    private Timestamp createdAt;

    // 출금 기능
    public void withdraw(Long amount) {
        // 방어적 코드 작성 예정
        this.balance -= amount;
    }

    // 입금 기능
    public void deposit(Long amount) {
        this.balance += amount;
    }

    // 패스워드 체크 기능
    public boolean checkPassword(String password) {
        boolean isOk = true;
        if (this.password.equals(password) == false) {
            isOk = false;
            throw new DataDeliveryException(Define.FAIL_ACCOUNT_PASSWROD, HttpStatus.BAD_REQUEST);
        }
        return isOk;
    }

    // 잔액 여부 확인 기능
    public void checkBalance(Long amount) {
        if (this.balance < amount) {
            throw new DataDeliveryException(Define.LACK_Of_BALANCE, HttpStatus.BAD_REQUEST);
        }
    }

    // 계좌 소유자 확인 기능
    public void checkOwner(Integer pricipalId) {
        if(this.userId != pricipalId) {
            throw new DataDeliveryException(Define.NOT_ACCOUNT_OWNER, HttpStatus.BAD_REQUEST);
        }
    }

    public String formatKoreanWon(Long balance) {
        return String.format("%,d원", balance);
    }

}