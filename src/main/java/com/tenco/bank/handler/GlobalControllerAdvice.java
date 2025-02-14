package com.tenco.bank.handler;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

// 중앙에서 관리할 예외처리 핸들러
@ControllerAdvice // IoC 대상(싱글톤) - html 렌더링시 예외에서 많이 사용
// @RestControllerAdvice  RESET API 활용 많이 됨
public class GlobalControllerAdvice {

    // 모든 예외 클래스를 알 수 없기 때문에 로깅으로 확인할 수 있도록 적용
    @ExceptionHandler({Exception.class})
    public void exception(Exception e) {
        System.out.println("---------------------------------");
        System.out.println(e.getClass().getName());
        System.out.println(e.getMessage());
        System.out.println("---------------------------------");
    }

    //  클라이언트에게 예외를 데이터로 전달
    @ResponseBody // 뷰 리졸버 안 탐
    @ExceptionHandler({DataDeliveryException.class})
    public String dataDeleveryException(DataDeliveryException e) {
        // String buffer 사용 이유 : 그냥 스트링은 객체를 많이 생성하게 되어서
        StringBuffer sb = new StringBuffer();
        sb.append(" <script>");
        sb.append(" alert(' " + e.getMessage() + "' );");
        sb.append(" window.history.back(); ");
        sb.append(" <.script>");
        return sb.toString();
    }


    //  클라이언트에게 예외를 데이터로 전달
    @ResponseBody // 뷰 리졸버 안 탐
    @ExceptionHandler({UnAuthorizedException.class})
    public String unAuthorizedException(UnAuthorizedException e) {
        StringBuffer sb = new StringBuffer();
        sb.append(" <script>");
        sb.append(" alert(' " + e.getMessage() + "' );");
        sb.append(" window.history.back(); ");
        sb.append(" <.script>");
        return sb.toString();
    }


    /**
     * 에러 페이지로 이동 처리하기
     * JSP로 이동시 데이터를 담아서 보내는 방법
     * 클라이언트에게 예외를 데이터로 전달하지 않고, 뷰 리졸브 사용
     */
    @ExceptionHandler({RedirectException.class})
    public ModelAndView redirectException(RedirectException e) {
        ModelAndView mav = new ModelAndView("/errorPage");
        mav.addObject("statusCode", e.getStatus().value());
        mav.addObject("message", e.getMessage());
        return mav;
    }
}