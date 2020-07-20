package com.mikeldeltio.undertow.handler;

import static com.mikeldeltio.undertow.util.HandlerUtils.dispatchRequest;
import static io.undertow.UndertowMessages.MESSAGES;
import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.SameThreadExecutor.INSTANCE;
import static io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR;
import static io.undertow.util.StatusCodes.OK;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mikeldeltio.undertow.model.health.Health;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class HealthCheckHandler implements HttpHandler {

	private static final Logger LOGGER = Logger.getLogger(HealthCheckHandler.class);

	public static final String APPLICATION_JSON = "application/json";

	private final HttpHandler next;

	private static final ObjectWriter mapper = new ObjectMapper().writerFor(Health.class);

	public HealthCheckHandler(HttpHandler next) {
		if (next == null) {
			throw MESSAGES.argumentCannotBeNull("next");
		}
		this.next = next;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		if (exchange.isInIoThread()) {
			exchange.dispatch(this);
			return;
		}
		exchange.dispatch(INSTANCE, () -> doHealthCheck(exchange).orTimeout(30, SECONDS)
				.whenComplete((health, ex) -> dispatchRequest(next, exchange)));
	}

	private CompletableFuture<Void> doHealthCheck(HttpServerExchange exchange) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				String health = null;
				exchange.getResponseHeaders().put(CONTENT_TYPE, APPLICATION_JSON);
				try {
					health = mapper.writeValueAsString(Health.up().build());
					exchange.setStatusCode(OK);
					exchange.getResponseSender().send(health);
				} catch (IOException exception) {
					LOGGER.error(exception);
					try {
						health = mapper.writeValueAsString(Health.down().build());
						exchange.setStatusCode(INTERNAL_SERVER_ERROR);
						exchange.getResponseSender().send(health);
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		});
		return future;
	}

}
