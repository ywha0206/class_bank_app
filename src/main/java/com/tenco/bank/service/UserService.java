package com.tenco.bank.service;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    //    @Autowired
    private final UserRepository userRepository;
    // final -> 불변 객체로 만들어줌 -> 한번 생성되면 변경 X
    // 불변 객체는 autowired 안 됨

//     생성자 의존 주입 - DI
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    // 회원 가입 처리
    // 예외 처리
    // DB 에서 연결이나 쿼리 실행, 제약 사항 위한 같은
    // 예외는 RuntimeException 으로 예외를 잡을 수 없습니다.
    @Transactional // 트랜 잭션 처리 습관
    public void createUser(SignUpDTO dto) {
        // Http 응답으로 클라이언트에게 전달할 오류 메시지는 최소한으로 유지하고,
        // 보안 및 사용자 경험 측면에서 민감한 정보를 노출하지 않도록 합니다.
        int result = 0;
        try {
            result = userRepository.insert(dto.toUser());
            // 여기서 예외 처리를 하면 상위 catch 블록에서 예외를 잡는다.
        } catch (DataAccessException e) {
            // DataAccessException는 Spring의 데이터 액세스 예외 클래스로,
            // 데이터베이스 연결이나 쿼리 실행과 관련된 문제를 처리합니다.
            throw new DataDeliveryException("잘못된 처리 입니다", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // 그 외 예외 처리 - 페이지 이동 처리
            throw new RedirectException("알 수 없는 오류" , HttpStatus.SERVICE_UNAVAILABLE);
        }
        // 예외 클래스가 발생이 안되지만 프로세스 입장에서 예외 상황으로 바라 봄
        if (result != 1) {
            // 삽입된 행의 수가 1이 아닌 경우 예외 발생
            throw new DataDeliveryException("회원 가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그인 서비스 처리
     * @param // SignInDTO
     * @return userEntity or null
     */
    public User readUser(SignInDTO dto) {
        // 유효성 검사는 Controller 에서 먼저 하자.
        User userEntity = null;
        try {
            userEntity = userRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
        } catch (DataAccessException e) {
            throw new DataDeliveryException("잘못된 처리 입니다",HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // 예외 처리 - 에러 페이지 호출
            throw new RedirectException("알 수 없는 오류" , HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 예외 클래스 발생 안됨. 프로세스 입장에서 예외로 처리 throw 처리 함
        if(userEntity == null) {
            throw new DataDeliveryException("아이디 혹은 비번이 틀렸습니다",
                    HttpStatus.BAD_REQUEST);
        }

        return userEntity;
    }
}