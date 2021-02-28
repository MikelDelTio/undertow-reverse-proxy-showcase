package com.mikeldeltio.undertow.conduit;

import static com.mikeldeltio.undertow.handler.ReverseProxyLogHandler.doLogResponse;
import static com.mikeldeltio.undertow.util.HttpUtils.shouldBufferContent;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.StreamSinkConduit;

import io.undertow.server.HttpServerExchange;

public class ReverseProxyResponseStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {

	private final HttpServerExchange exchange;

	private final long responseContentLength;

	private boolean processedResponse = false;

	private boolean readResponse;

	private byte[] bufferedResponse = new byte[] {};

	public ReverseProxyResponseStreamSinkConduit(StreamSinkConduit next, HttpServerExchange exchange) {
		super(next);
		this.exchange = exchange;
		this.responseContentLength = exchange.getResponseContentLength();
		if (shouldBufferContent(responseContentLength)) {
			readResponse = true;
		} else {
			readResponse = false;
		}
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		if (!processedResponse) {
			if (readResponse) {
				byte[] response = new byte[src.remaining()];
				src.get(response);
				int alreadyWrittenSize = bufferedResponse.length;
				bufferedResponse = ByteBuffer.allocate(bufferedResponse.length + response.length).put(bufferedResponse)
						.put(response).array();
				if (responseContentLength == bufferedResponse.length) {
					doLogResponse(exchange, bufferedResponse);
					processedResponse = true;
					return super.write(ByteBuffer.wrap(bufferedResponse)) - alreadyWrittenSize;
				} else {
					return bufferedResponse.length - alreadyWrittenSize;
				}
			}
		}
		return super.write(src);
	}

	@Override
	public void terminateWrites() throws IOException {
		if (!processedResponse) {
			doLogResponse(exchange, bufferedResponse);
			processedResponse = true;
		}
		super.terminateWrites();
	}

}