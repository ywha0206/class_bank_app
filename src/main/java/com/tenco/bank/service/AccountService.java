package com.tenco.bank.service;

import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.utils.Define;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.AccountSaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final HistoryRepository historyRepository;

    public AccountService(AccountRepository accountRepository, HistoryRepository historyRepository) {
        this.accountRepository = accountRepository;
        this.historyRepository = historyRepository;
    }

    /**
     * 계좌 생성 기능
     *
     * @param dto
     * @param pricipalId
     */
    @Transactional
    public void createAccount(AccountSaveDTO dto, Integer pricipalId) {
        try {
            accountRepository.insert(dto.toAccount(pricipalId));
        } catch (DataAccessException e) {
            // DB연결 및 제약 사항 위한 및 쿼리 오류
            throw new DataDeliveryException("잘못된 처리 입니다", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // 예외 처리 - 에러 페이지로 이동
            throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * 복잡한 Select 쿼리문일 경우 트랜잭션 처리를 해주 것이 좋습니다.
     * 여기서는 단순한 Select 구문이라 바로 진행 합니다.
     *
     * @param principalId
     * @return
     */
    public List<Account> readAccountListByUserId(Integer principalId) {
        List<Account> accountListEntity = null;
        try {
            accountListEntity = accountRepository.findAllByUserId(principalId);
        } catch (DataAccessException e) {
            throw new DataDeliveryException("잘못된 처리 입니다", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // 예외 처리 - 에러 페이지로 이동
            throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return accountListEntity;
    }

    /**
     * 계좌 출금
     * 1. 트랜잭션 처리
     * 2. 계좌번호 존재 여부 확인
     * 3. 본인 계좌 여부 확인 -- 객체에서 확인
     * 4. 계좌에 비밀번호가 맞는지 확인 처리
     * 5. 잔액 여부 확인(출금 가능 금액)
     * 6. 출금처리 update
     * 7. 거래내역 등록 -- history
     *
     * @param dto
     * @param principalId
     */
    @Transactional
    public void updateAccountWithdraw(WithdrawalDTO dto, Integer principalId) {
        Account account = accountRepository.findByNumber(dto.getWAccountNumber());
        if (account == null) {
            throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
        }

        // 검사 로직
        account.checkOwner(principalId);
        account.checkPassword(dto.getWAccountPassword());
        account.checkBalance(dto.getAmount());

//        직접 작성이 아니라 클래스에 만들어둔 메서드 사용
//        account.setBalance(account.getBalance()-dto.getAmount());
        account.withdraw(dto.getAmount());
        // 저장
        accountRepository.updateById(account);

        History history = new History();
        history.setAmount(dto.getAmount());
        history.setWBalance(account.getBalance());
        history.setWAccountId(account.getId());
        history.setDAccountId(null);
        history.setDBalance(null);
//        history.setCreatedAt(Timestamp.valueOf(LocalDateTime.now())); 필요가 엄써용
        int rowResetCount= historyRepository.insert(history);
        if(rowResetCount != 1){
            throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
