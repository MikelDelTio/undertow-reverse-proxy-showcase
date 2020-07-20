package com.mikeldeltio.undertow.handler;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikeldeltio.undertow.model.error.ErrorMessage;

import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class ErrorHandler implements HttpHandler {

	private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class);

	private final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private final static String APPLICATION_JSON = "application/json";

	private final HttpHandler next;

	public ErrorHandler(HttpHandler next) {
		this.next = next;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		exchange.addDefaultResponseListener(new DefaultResponseListener() {
			@Override
			public boolean handleDefaultResponse(final HttpServerExchange exchange) {
				if (!exchange.isResponseChannelAvailable()) {
					return false;
				}

				Throwable exception = exchange.getAttachment(DefaultResponseListener.EXCEPTION);
				ErrorMessage errorMessage = new ErrorMessage();
				errorMessage.setTimestamp(LocalDateTime.now().format(ofPattern(DATE_TIME_FORMAT)).toString());
				errorMessage.setDescription(StatusCodes.INTERNAL_SERVER_ERROR_STRING);
				errorMessage.setMessage(exception != null ? exception.getMessage() : "");

				String errorMessageJson = null;
				try {
					errorMessageJson = new ObjectMapper().writeValueAsString(errorMessage);
				} catch (IOException e) {
					LOGGER.error("Error while serializing Error Message {}", e);
					errorMessageJson = "{}";
				}

				exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, APPLICATION_JSON);
				exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, errorMessageJson.length());
				exchange.setStatusCode(500);
				exchange.getResponseSender().send(errorMessageJson);

				return true;

			}
		});
		next.handleRequest(exchange);
	}
}
