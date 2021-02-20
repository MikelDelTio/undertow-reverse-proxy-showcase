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

	private final long responseContentLength;

	private boolean processedResponse = false;

	private ByteArrayOutputStream outputStream;

	private byte[] bufferedResponse = new byte[] {};

	public ReverseProxyResponseStreamSinkConduit(StreamSinkConduit next, HttpServerExchange exchange) {
		super(next);
		this.exchange = exchange;
		this.responseContentLength = exchange.getResponseContentLength();
		if (responseContentLength <= 0L) {
			outputStream = new ByteArrayOutputStream();
		} else {
			if (responseContentLength > Integer.MAX_VALUE) {
				throw UndertowMessages.MESSAGES.responseTooLargeToBuffer(responseContentLength);
			}
			outputStream = new ByteArrayOutputStream((int) responseContentLength);
		}
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		if (!processedResponse) {
			byte[] response = new byte[src.remaining()];
			src.mark();
			src.get(response);
			src.reset();
			bufferedResponse = ByteBuffer.allocate(bufferedResponse.length + response.length).put(bufferedResponse)
					.put(response).array();
			if (responseContentLength == bufferedResponse.length) {
				doLogResponse(exchange, outputStream);
				processedResponse = true;
			}
		}
		return super.write(src);
	}

	@Override
	public void terminateWrites() throws IOException {
		if (!processedResponse) {
			if (outputStream.size() <= 0) {
				doLogResponse(exchange, outputStream);
			}
			outputStream.close();
		}
		super.terminateWrites();
	}

}