package com.barath.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@EnableResourceServer
public class AppController {
	
	private static final Logger logger=LoggerFactory.getLogger(AppController.class);
	
	
	
	@ExceptionHandler(Exception.class)
	public String handleExeception(Exception ex){
		logger.error("Error ==> "+ex.getMessage());
		return ex.getMessage();
		
	}
	
	@GetMapping("/")
	public String oauthClientHome(){
		return "welcome to oauth client";
	}

}
