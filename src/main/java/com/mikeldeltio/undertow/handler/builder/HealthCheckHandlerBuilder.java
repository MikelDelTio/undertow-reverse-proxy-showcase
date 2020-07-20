package com.mikeldeltio.undertow.handler.builder;

import java.util.Map;
import java.util.Set;

import com.mikeldeltio.undertow.handler.HealthCheckHandler;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.handlers.builder.HandlerBuilder;

public class HealthCheckHandlerBuilder implements HandlerBuilder {

	@Override
	public String name() {
		return "health-check";
	}

	@Override
	public Map<String, Class<?>> parameters() {
		// Set the parameter names (avoid whitespaces) and its type here
		return null;
	}

	@Override
	public Set<String> requiredParameters() {
		// Return the names of the required parameters (configuration would fail if any
		// of these is missing)
		return null;
	}

	@Override
	public String defaultParameter() {
		// If there is only one parameter, or one required parameter, return it to make
		// it easier to configure
		return null;
	}

	@Override
	public HandlerWrapper build(final Map<String, Object> config) {
		// Get the configuration parameters here and parse them
		// Example: String myParameter = (String)config.get("my-parameter");
		// After that, return the builder
		return handler -> new HealthCheckHandler(handler);
	}

}
