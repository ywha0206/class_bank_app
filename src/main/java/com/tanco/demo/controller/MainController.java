package com.tanco.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller  // IoC 대상 (싱글톤 패턴으로 관리 된다) --> 제어의 역전 
public class MainController {

	// 주소설계 
	// http://localhost:8080/main-page
	// 자원 요청 METHOD - GET 방식 
	@GetMapping({"/main-page", "/index"})
	public String mainPage() {
		// Controller --> 리턴값을 문자열로 처리 
		// 뷰 리졸브 --> 
		// prefix: /WEB-INF/view
		//       mainPage
	    //  suffix: .jsp 
		
		// /WEB-INF/view/mainPage.jsp  
		return "/mainPage";
	}
}
