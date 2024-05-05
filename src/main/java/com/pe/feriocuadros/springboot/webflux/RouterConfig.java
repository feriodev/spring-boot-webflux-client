package com.pe.feriocuadros.springboot.webflux;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.pe.feriocuadros.springboot.webflux.handler.ProductHandler;

@Configuration
public class RouterConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(ProductHandler handler){
		return route(GET("/api/client").or(GET("/api/client/listar")), handler::listar)
				.andRoute(GET("/api/client/{id}"), handler::detalle)
				.andRoute(POST("/api/client"), handler::crear)
				.andRoute(PUT("/api/client/{id}"), handler::editar)
				.andRoute(DELETE("/api/client/{id}"), handler::eliminar)
				.andRoute(POST("/api/client/upload/{id}"), handler::upload)
				/*.andRoute(POST("/api/productos/crear"), handler::crearConFoto)*/;
	}
}
