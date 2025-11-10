package kr.co.syrup.adreport.framework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping
public class HtmlViewController {

	@RequestMapping(value = "/static/**/*.html")
	public void htmlView(HttpServletRequest request) {
	    log.debug("request.getRequestURI() : {}", request.getRequestURI());
	}

}
