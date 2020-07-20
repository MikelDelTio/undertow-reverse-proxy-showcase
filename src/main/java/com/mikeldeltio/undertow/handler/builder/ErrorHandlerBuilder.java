package com.mikeldeltio.undertow.handler.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mikeldeltio.undertow.handler.ErrorHandler;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.handlers.builder.HandlerBuilder;

public class ErrorHandlerBuilder implements HandlerBuilder {

	@Override
	public HandlerWrapper build(final Map<String, Object> config) {
		return handler -> new ErrorHandler(handler);
	}

	@Override
	public String name() {
		return "error-handler";
	}

	@Override
	public Map<String, Class<?>> parameters() {
		Map<String, Class<?>> params = new HashMap<>();
		return params;
	}

	@Override
	public Set<String> requiredParameters() {
		return parameters().keySet();
	}

	@Override
	public String defaultParameter() {
		return null;
	}
}
