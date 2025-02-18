package com.tenco.bank.controller;

import com.tenco.bank.dto.AccountSaveDTO;
import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.HistoryAccountDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/account")
@Controller
public class AccountController {

    private final HttpSession session;
    private final AccountService accountService;

    // account/save.jsp
    @GetMapping("/save")
    public String savePage(Model model) {
        return "/account/save";
    }


    @PostMapping("/save")
    public String saveProc(AccountSaveDTO dto, @SessionAttribute(Define.PRINCIPAL) User principal) {
        // 유효성 검사보다 먼저 인증검사를 먼저 하는 것이 좋습니다.
        // 1. 인증검사 --> 이제 인터셉터로 인증검사 진행하기 때문에 검사 로직은 날렸다

        // 2. 유효성 검사
        if (dto.getNumber() == null || dto.getNumber().isEmpty()) {
            throw new DataDeliveryException("계좌번호를 입력하시오",
                    HttpStatus.BAD_REQUEST);
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new DataDeliveryException("계좌비밀번호를 입력하시오",
                    HttpStatus.BAD_REQUEST);
        }

        if (dto.getBalance() == null || dto.getBalance() <= 0) {
            throw new DataDeliveryException("잘못된 입력 입니다",
                    HttpStatus.BAD_REQUEST);
        }
        accountService.createAccount(dto, principal.getId());

        // TODO 추후 account/list 페이지가 만들어 지면 수정 할 예정 입니다.
        return "redirect:/account/save";
    }


    /**
     * 계좌 목록 페이지
     *
     * @param model - accountList
     * @return list.jsp
     */
    @GetMapping({"/list", "/"})
    public String listPage(Model model, @SessionAttribute(Define.PRINCIPAL) User principal) {

        // 경우의 수 -> 유, 무
        List<Account> accountList = accountService.readAccountListByUserId(principal.getId());

        if (accountList.isEmpty()) {
            model.addAttribute("accountList", null);
        } else {
            model.addAttribute("accountList", accountList);
        }

        return "/account/list";
    }


    @GetMapping("/withdrawal")
    public String withdrawalPage(Model model) {
        return "/account/withdrawal";
    }

    @PostMapping("/withdrawal")
    public String withdrawalPage(WithdrawalDTO dto, @SessionAttribute(Define.PRINCIPAL) User principal) {

        // 유효성 검사
        if (dto.getAmount() == null) {
            throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
        }
        if (dto.getAmount().longValue() <= 0) {
            throw new DataDeliveryException(Define.W_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
        }
        if (dto.getWAccountNumber() == null) {
            throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
        }
        if (dto.getWAccountPassword() == null || dto.getWAccountPassword().isEmpty()) {
            throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        accountService.updateAccountWithdraw(dto, principal.getId());
        return "redirect:/account/list";
    }


    @GetMapping("/deposit")
    public String depositPage() {
        return "/account/deposit";
    }


    /**
     * 입금 기능 처리
     * @param
     * @return 계좌 목록 페이지
     */
    @PostMapping("/deposit")
    public String depositProc(DepositDTO dto, @SessionAttribute(Define.PRINCIPAL) User principal) {

        // 2. 유효성 검사
        if (dto.getAmount() == null) {
            throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
        }
        if (dto.getAmount().longValue() <= 0) {
            throw new DataDeliveryException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
        }
        if (dto.getDAccountNumber() == null) {
            throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
        }
        // 서비스 호출
        accountService.updateAccountDeposit(dto, principal.getId());

        return "redirect:/account/list";

    }


    @GetMapping("/transfer")
    public String transferPage() {
        return "/account/transfer";
    }

    /**
     * 계좌 상세보기 화면
     * 주소 설계 : http://localhost:8080/account/detail/1
     * 타입 설계 : http://localhost:8080/account/detail/1?type=all,deposit, withdraw
     *
     * @return
     */
    @GetMapping("/detail/{accountId}")
    public String detailPage(@PathVariable(name = "accountId") Integer accountId,
                             @RequestParam(required = false, name = "type") String type,
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "size", defaultValue = "2") int size,
                             Model model) {
        // 유효성 검사
        List<String> validTypes = Arrays.asList("all", "deposit", "withdrawal");
        if (!validTypes.contains(type)) {
            throw new DataDeliveryException(Define.INVALID_ACCESS, HttpStatus.BAD_REQUEST);
        }

        // 페이지 처리를 하기 위한 데이터
        // 전체 레코드 수가 필요하다.  히스토리 이력이 10
        // 한 페이지당 보여줄 갯수는 3라고 가정 한다면
        // 10개 페이지가 생성된다. --> 5 페이지   3 3 3 1
        // 전체 레코드 수를 가져와야 하고
        // 토탈 페이지 수를 계산 해야 한다.
        int totalRecords = accountService.countHistoryByAccountAndType(type, accountId);
        int totalPages = (int)Math.ceil((double) totalRecords / size);

        // 화면을 구성하기 위해 필요한 데이터
        // 소유자 이름 -- account_tb
        // 해당 계좌번호 -- account_tb
        // 거래내역 -- history_tb
        Account account = accountService.readAccountId(accountId);
        List<HistoryAccountDTO> historyList = accountService.readHistoryByAccountId(type, accountId, page, size);

        // 뷰리졸브 --> jsp 데이터를 내려줄 때
        // Model
        model.addAttribute("account", account);
        model.addAttribute("historyList", historyList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("type", type);
        model.addAttribute("size", size);

        return "/account/detail";
    }
}