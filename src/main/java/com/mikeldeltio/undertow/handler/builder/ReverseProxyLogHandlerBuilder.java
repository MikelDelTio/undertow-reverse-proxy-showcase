package com.mikeldeltio.undertow.handler.builder;

import java.util.Map;
import java.util.Set;

import com.mikeldeltio.undertow.handler.ReverseProxyLogHandler;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.handlers.builder.HandlerBuilder;

public class ReverseProxyLogHandlerBuilder implements HandlerBuilder {

	@Override
	public String name() {
		return "reverse-proxy-log";
	}

	@Override
	public Map<String, Class<?>> parameters() {
		return null;
	}

	@Override
	public Set<String> requiredParameters() {
		return null;
	}

	@Override
	public String defaultParameter() {
		return null;
	}

	@Override
	public HandlerWrapper build(final Map<String, Object> config) {
		return handler -> new ReverseProxyLogHandler(handler);
	}

}
