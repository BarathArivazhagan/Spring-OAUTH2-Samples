package com.barath.app;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;




@RestController
public class AppController {
	
	private static final Logger logger=LoggerFactory.getLogger(AppController.class);
	private static final String OAUTH_TOKEN_URL="http://localhost:8082/server/oauth/token?grant_type=authorization_code&client_id=clientapp&code={code}&redirect_uri={redirecturi}&state={state}";
	private static final String RESOURCE_URL="http://localhost:8083/client/";
	private ObjectMapper mapper=new ObjectMapper();
	
	@Autowired
	private OAuth2ClientContext clientContext;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@ExceptionHandler(Exception.class)
	public String handleExeception(Exception ex){
		logger.error("Error ==> "+ex.getMessage());
		return ex.getMessage();
		
	}
	
	@GetMapping("/")
	public String oauthZuulHome(){
		return "welcome to oauth zuul home";
	}
	
	@GetMapping("/redirect")
	public Object redirectedUrl(@RequestParam("code") String code,@RequestParam("state") String state,@RequestHeader Map<String,String> headers) throws Exception{
		System.out.println("Redirect is called ");
		headers.entrySet().stream().forEach(System.out::println);
		System.out.println(" CODE "+code+" STATE "+state);
		Object response=getOutput(code,state,headers);
		return response;
	}
	
	protected OAuth2AccessToken getAccessToken(String code,String state,Map<String,String> responseheaders) throws IOException{
		
//		System.out.println("client context details "+mapper.writeValueAsString(clientContext));
		HttpHeaders headers =new HttpHeaders();
		responseheaders.entrySet().stream().forEach((entry)->{
			System.out.println("Key "+entry.getKey()+" value "+entry.getValue());
			//headers.c
			headers.add(entry.getKey(), entry.getValue());
		});
		headers.add(" Authorization", "Basic YTph");
		//Uri
		HttpEntity<String> requestEntity=new HttpEntity<>(headers);
		Map<String,String> params=new HashMap<String,String>();
		params.put("code", code);
		params.put("redirecturi", "http://localhost:8083/redirect");
		params.put("state", state);
		OAuth2AccessToken accessToken=null;
		ResponseEntity<String> response=restTemplate.exchange(OAUTH_TOKEN_URL,HttpMethod.GET,requestEntity,String.class,params);
		if(response.hasBody()){
			String accessTokenString=response.getBody();
			System.out.println("access token string "+accessTokenString);
			accessToken= mapper.readValue(accessTokenString, OAuth2AccessToken.class);
			System.out.println("token details "+mapper.writeValueAsString(accessToken));
		}else{
			System.out.println("Response is null"+response.getStatusCode().getReasonPhrase());
		}
		
		return accessToken;
	}
	
	
	protected Object getOutput(String code,String state,Map<String,String> responseheaders) throws Exception{
		OAuth2AccessToken accessToken=getAccessToken(code,state,responseheaders);
		Object response=null;
		if(accessToken !=null){
			System.out.println("Access token is not null "+accessToken.getValue());
			System.out.println("token details "+mapper.writeValueAsString(accessToken));
			HttpHeaders headers =new HttpHeaders();
			headers.add(" Authorization", "Bearer "+accessToken.getValue());
			HttpEntity<String> requestEntity=new HttpEntity<>(headers);
		
			ResponseEntity<String> responseEntity=restTemplate.exchange(RESOURCE_URL,HttpMethod.GET,requestEntity,String.class);
			if(responseEntity.hasBody()){
				System.out.println("Resource obtained "+responseEntity.getBody());
				response=responseEntity.getBody();
			}else{
				System.out.println("Error in repsonse "+responseEntity.getStatusCode().getReasonPhrase());
			}
		}
		
		
		
		return response;
		
	}

}
