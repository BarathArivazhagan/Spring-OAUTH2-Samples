package com.barath.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

@SpringBootApplication
@EnableDiscoveryClient
public class OauthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthServerApplication.class, args);
	}
	
	@Configuration
	@Order(-20)
	protected static class LoginConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			
			http
				.formLogin().loginPage("/login").permitAll()
			.and()
				.requestMatchers().antMatchers("/login", 
							       "/oauth/authorize", 
							       "/oauth/confirm_access","/oauth/token")
			.and()
				.authorizeRequests().anyRequest().authenticated();
			
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.parentAuthenticationManager(authenticationManager);
		}
	}
	
	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

	  @Autowired
	  private AuthenticationManager authenticationManager;
	  
	  @Override
	  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	    endpoints.authenticationManager(authenticationManager);
	  }

	@Override
	  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
	    clients.inMemory()
	        .withClient("clientapp")
	        .secret("clientsecret")
	        .authorizedGrantTypes("authorization_code", "refresh_token",
	            "password","implicit").scopes("openid");
	  }
	}
	
	
}
