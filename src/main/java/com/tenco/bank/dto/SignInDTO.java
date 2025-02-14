package com.tenco.bank.dto;

import com.tenco.bank.repository.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInDTO {

    private String username;
    private String password;

    public User toUser() {
        return User.builder()
                .username(this.username)
                .password(this.password)
                .build();
    }
}
