package com.pe.feriocuadros.springboot.webflux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Value("${config.base.endpoint}")
	private String endpoint;

    @Bean
    @LoadBalanced
    WebClient registrarWebClient(final WebClient.Builder builder) {
		return builder.baseUrl(endpoint)
				.defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
				.build();
	}
}
