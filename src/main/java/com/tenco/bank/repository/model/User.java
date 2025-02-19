package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String fullname;
    private Timestamp createdAt;

    private String uploadFileName;
    private String originFileName;

    public String setUpUserImage() {
        return uploadFileName == null
                ? "https://picsum.photos/id/237/200/200"
                : "/images/uploads/" + uploadFileName;
    }
}