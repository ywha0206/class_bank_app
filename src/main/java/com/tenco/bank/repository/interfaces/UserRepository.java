package com.tenco.bank.repository.interfaces;

import com.tenco.bank.repository.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 인터페이스 만들고 + xml 파일 정의한다

@Mapper // 필수 어노테이션
public interface UserRepository {
    public int insert(User user);
    public int updateById(User user);
    public int deleteById(Integer id);
    public int findById(Integer id);
    public List<User> findAll();

    // 코드 추가 1단계 - 조회 : username, password
    // 주의!! - 파미리터가 2개 이상일 경우 @Param 어노테니션을 반드시 선언
    public User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    public User findByUsername(String username);
}