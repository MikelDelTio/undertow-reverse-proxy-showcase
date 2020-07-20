package com.mikeldeltio.undertow.util;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class HandlerUtils {

	public static void dispatchRequest(HttpHandler next, HttpServerExchange exchange) {
		exchange.dispatch(handler -> {
			next.handleRequest(exchange);
		});
	}
}
