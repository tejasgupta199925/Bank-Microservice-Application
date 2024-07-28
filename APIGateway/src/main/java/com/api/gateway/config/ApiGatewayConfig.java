package com.api.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {
	
	@Value("${service.customer.name}")
	private String customerServiceName;
	
	@Value("${service.account.name}")
	private String accountServiceName;
	
	@Value("${service.customer.path}")
	private String customerServicePath;
	
	@Value("${service.account.path}")
	private String accountServicePath;
	
	@Value("${service.customer.uri}")
	private String customerServiceUri;
	
	@Value("${service.account.uri}")
	private String accountServiceUri;

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route(customerServiceName, r -> r.path(customerServicePath).uri(customerServiceUri))
			.route(accountServiceName, r -> r.path(accountServicePath).uri(accountServiceUri))
			.build();
	}
	
}
