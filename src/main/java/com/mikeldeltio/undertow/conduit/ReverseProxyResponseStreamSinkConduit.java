package com.mikeldeltio.undertow.conduit;

import static com.mikeldeltio.undertow.handler.ReverseProxyLogHandler.doLogResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.StreamSinkConduit;

import io.undertow.UndertowMessages;
import io.undertow.server.HttpServerExchange;

public class ReverseProxyResponseStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {

	private final HttpServerExchange exchange;

	private ByteArrayOutputStream outputStream;

	public ReverseProxyResponseStreamSinkConduit(StreamSinkConduit next, HttpServerExchange exchange) {
		super(next);
		this.exchange = exchange;
		long length = exchange.getResponseContentLength();
		if (length <= 0L) {
			outputStream = new ByteArrayOutputStream();
		} else {
			if (length > Integer.MAX_VALUE) {
				throw UndertowMessages.MESSAGES.responseTooLargeToBuffer(length);
			}
			outputStream = new ByteArrayOutputStream((int) length);
		}
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		int start = src.position();
		for (int i = start; i < start + src.limit(); ++i) {
			outputStream.write(src.get(i));
		}
		outputStream.flush();
		if (outputStream.size() > 0) {
			doLogResponse(exchange, outputStream);
		}
		return super.write(src);
	}

	@Override
	public void terminateWrites() throws IOException {
		if (outputStream.size() <= 0) {
			doLogResponse(exchange, outputStream);
		}
		outputStream.close();
		super.terminateWrites();
	}

}