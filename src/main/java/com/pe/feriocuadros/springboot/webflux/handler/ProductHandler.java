package com.pe.feriocuadros.springboot.webflux.handler;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.pe.feriocuadros.springboot.webflux.models.Producto;
import com.pe.feriocuadros.springboot.webflux.service.ProductService;

import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

	@Autowired
	private ProductService service;
	
	public Mono<ServerResponse> listar(ServerRequest request){
		return ServerResponse.ok()
				.contentType(APPLICATION_JSON)			
				.body(service.findAll(), Producto.class)
				.onErrorResume(error -> {
					System.out.println(error.getMessage());
					return Mono.error(error);
				});
	}
	
	public Mono<ServerResponse> detalle(ServerRequest request){
		String id = request.pathVariable("id");
		return errorHandler(
				service.findById(id)
				.flatMap(p -> {
					return ServerResponse.ok()
							.contentType(APPLICATION_JSON)
							.body(fromValue(p));							 
				}));
	}
	
	public Mono<ServerResponse> crear(ServerRequest request){
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		return producto.flatMap(p -> {
			if (p.getCreateAt() == null) {
				p.setCreateAt(new Date());
			}
			
			return service.save(p);
		}).flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
				.contentType(APPLICATION_JSON)
				.body(fromValue(p)))
				.onErrorResume(error -> {
					WebClientResponseException errorResponse = (WebClientResponseException) error;
					if (errorResponse.getStatusCode() == BAD_REQUEST) {
						return ServerResponse.badRequest()
								.contentType(APPLICATION_JSON)
								.body(fromValue(errorResponse.getResponseBodyAsString()));
					}
					else {
						return Mono.error(errorResponse);
					}
				});
	}
	
	public Mono<ServerResponse> editar(ServerRequest request){
		String id = request.pathVariable("id");
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		return errorHandler(
				producto
				.flatMap(p -> service.update(p, id))
				.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
						.contentType(APPLICATION_JSON)
						.body(fromValue(p)))
				);
				
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest request){
		String id = request.pathVariable("id");		
		return errorHandler(service.delete(id).then(ServerResponse.noContent().build()));
	}
	
	public Mono<ServerResponse> upload(ServerRequest request){
		String id = request.pathVariable("id");	
		return errorHandler(
				request.multipartData()
				.map(multipart -> multipart.toSingleValueMap().get("file"))
				.cast(FilePart.class)
				.flatMap(file -> service.upload(file, id))
				.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(id)))
						.contentType(APPLICATION_JSON)
						.body(fromValue(p)))
				);
	}
	
	private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response){
		return response.onErrorResume(error -> {
			WebClientResponseException errorResponse = (WebClientResponseException) error;
			if (errorResponse.getStatusCode() == NOT_FOUND) {
				Map<String, Object> body = new HashMap<>();
				body.put("message", "No existe el producto ".concat(errorResponse.getMessage()));
				body.put("date", LocalTime.now().toString());
				body.put("status", errorResponse.getStatusCode().value());
				return ServerResponse.status(NOT_FOUND).body(fromValue(body));
			}
			else {
				return Mono.error(errorResponse);
			}
		});
	}
}
