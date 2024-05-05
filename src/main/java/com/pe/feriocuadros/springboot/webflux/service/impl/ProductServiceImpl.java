package com.pe.feriocuadros.springboot.webflux.service.impl;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pe.feriocuadros.springboot.webflux.models.Producto;
import com.pe.feriocuadros.springboot.webflux.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private WebClient client;
	
	@Override
	public Flux<Producto> findAll() {		
		return client.get()
				.accept(APPLICATION_JSON)
				.exchangeToFlux(response -> response.bodyToFlux(Producto.class));
	}

	@Override
	public Mono<Producto> findById(String id) {		
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		return client.get().uri("/{id}", params)
				.accept(APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Producto.class);
				//.exchangeToMono(response -> response.bodyToMono(Producto.class));
	}

	@Override
	public Mono<Producto> save(Producto producto) {
		
		return client.post()
				.accept(APPLICATION_JSON)
				.contentType(APPLICATION_JSON)
				.body(fromValue(producto))
				.retrieve()
				.bodyToMono(Producto.class);
	}

	@Override
	public Mono<Producto> update(Producto producto, String id) {
		return client.put().uri("/{id}", Collections.singletonMap("id", id))
				.accept(APPLICATION_JSON)
				.contentType(APPLICATION_JSON)
				.body(fromValue(producto))
				.retrieve()
				.bodyToMono(Producto.class);
	}

	@Override
	public Mono<Void> delete(String id) {
		return client.delete().uri("/{id}", Collections.singletonMap("id", id))
				.accept(APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Void.class);
	}
	
	@Override
	public Mono<Producto> upload(FilePart file, String id){
		MultipartBodyBuilder parts = new MultipartBodyBuilder();
		parts.asyncPart("file", file.content(), DataBuffer.class).headers(h -> {
			h.setContentDispositionFormData("file", file.filename());
		});
		return client.post()
				.uri("/upload/{id}", Collections.singletonMap("id", id))
				.contentType(MULTIPART_FORM_DATA)
				.body(fromValue(parts))
				.retrieve()
				.bodyToMono(Producto.class);
		
	}

}
