package com.barath.app;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class AppController {
	
	private static final Logger logger=LoggerFactory.getLogger(AppController.class);
	
	
	
	@ExceptionHandler(Exception.class)
	public String handleExeception(Exception ex){
		logger.error("Error ==> "+ex.getMessage());
		return ex.getMessage();
		
	}
	
	@GetMapping("/user")
	public Principal getUser(Principal principal){
		
		return principal;
	}

}
