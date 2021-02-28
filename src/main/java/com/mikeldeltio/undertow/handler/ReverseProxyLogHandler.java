package com.mikeldeltio.undertow.handler;

import static com.mikeldeltio.undertow.atributte.HttpLogMessageAtributte.getHttpLogMessage;
import static com.mikeldeltio.undertow.atributte.HttpLogMessageAtributte.setHttpLogMessage;
import static com.mikeldeltio.undertow.util.HandlerUtils.dispatchRequest;
import static com.mikeldeltio.undertow.util.HttpUtils.readRequestBody;
import static io.undertow.UndertowMessages.MESSAGES;
import static io.undertow.util.SameThreadExecutor.INSTANCE;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.xnio.conduits.StreamSinkConduit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mikeldeltio.undertow.conduit.ReverseProxyResponseStreamSinkConduit;
import com.mikeldeltio.undertow.model.log.HttpLogMessage;

import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;

public class ReverseProxyLogHandler implements HttpHandler {

	private static final Logger LOGGER = Logger.getLogger(ReverseProxyLogHandler.class);

	private static final ObjectWriter mapper = new ObjectMapper().writerFor(HttpLogMessage.class);

	private final HttpHandler next;

	public ReverseProxyLogHandler(HttpHandler next) {
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
		exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
			@Override
			public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
				return new ReverseProxyResponseStreamSinkConduit(factory.create(), exchange);
			}
		});
		exchange.dispatch(INSTANCE, () -> doLogRequest(exchange).orTimeout(30, SECONDS)
				.whenComplete((health, ex) -> dispatchRequest(next, exchange)));
	}

	private CompletableFuture<Void> doLogRequest(HttpServerExchange exchange) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				HttpLogMessage httpLogMessage = new HttpLogMessage();
				httpLogMessage.setTimestamp(LocalDateTime.now().toString());
				httpLogMessage.setMethod(exchange.getRequestMethod().toString());
				httpLogMessage.setUrl(exchange.getRequestURL()
						+ (!exchange.getQueryString().isBlank() ? "?" + exchange.getQueryString() : ""));
				httpLogMessage.setRequestHeaders(exchange.getRequestHeaders().toString());
				httpLogMessage.setRequestBody(readRequestBody(exchange));
				setHttpLogMessage(exchange, httpLogMessage);
			}
		});
		return future;
	}

	public static void doLogResponse(HttpServerExchange exchange, byte[] bufferedResponse) {
		try {
			HttpLogMessage httpLogMessage = getHttpLogMessage(exchange);
			httpLogMessage.setStatusCode(exchange.getStatusCode());
			httpLogMessage.setResponseHeaders(exchange.getResponseHeaders().toString());
			httpLogMessage.setResponseBody(new String(bufferedResponse, exchange.getResponseCharset()));
			LOGGER.info(mapper.writeValueAsString(httpLogMessage));
		} catch (IOException exception) {
			LOGGER.error(exception);
		}
	}

}
