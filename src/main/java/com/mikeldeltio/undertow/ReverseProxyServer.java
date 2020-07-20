package com.mikeldeltio.undertow;

import static com.mikeldeltio.undertow.config.ConfigurationLoader.getConfiguration;

import java.io.IOException;
import java.util.Optional;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.PredicatedHandlersParser;

public class ReverseProxyServer {

	private static Undertow undertow;

	public static void main(String[] args) throws IOException {
		run();
	}

	public static void run() throws IOException {
		undertow = Undertow.builder()
				.addHttpListener(getConfiguration().getServer().getPort(), getConfiguration().getServer().getHost())
				.setIoThreads(getConfiguration().getServer().getIoThreads())
				.setWorkerThreads(getConfiguration().getServer().getWorkerThreads())
				.setHandler(Handlers.predicates(
						PredicatedHandlersParser.parse(getConfiguration().getServer().getPredicates(), null),
						exchange -> Optional.of(exchange).filter(HttpServerExchange::isResponseChannelAvailable)
								.ifPresent(ex -> ex.getResponseSender().send(ex.getRelativePath()))))
				.build();
		undertow.start();
	}

	public static void tearDown() {
		undertow.stop();
	}

}
