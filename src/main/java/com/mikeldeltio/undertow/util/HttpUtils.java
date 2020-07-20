package com.mikeldeltio.undertow.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;

public class HttpUtils {

	private static final Logger LOGGER = Logger.getLogger(HttpUtils.class);

	public static String readRequestBody(HttpServerExchange exchange) {
		PooledByteBuffer bufferPool = exchange.getConnection().getByteBufferPool().allocate();
		ByteBuffer buffer = bufferPool.getBuffer();
		try {
			StreamSourceChannel inChannel = exchange.getRequestChannel();
			int bytesRead = inChannel.read(buffer);
			while (bytesRead != -1) {
				bytesRead = inChannel.read(buffer);
			}
			buffer.flip();
		} catch (Exception e) {
			if (bufferPool != null && bufferPool.isOpen()) {
				IoUtils.safeClose(bufferPool);
			}
			e.printStackTrace();
		}
		Connectors.ungetRequestBytes(exchange, bufferPool);
		Connectors.resetRequestChannel(exchange);
		try {
			return new String(getByteArrayFromByteBuffer(buffer), exchange.getRequestCharset());
		} catch (UnsupportedEncodingException exception) {
			LOGGER.error(exception);
			return new String();
		}
	}

	private static byte[] getByteArrayFromByteBuffer(ByteBuffer buffer) {
		byte[] bytesArray = new byte[buffer.remaining()];
		buffer.get(bytesArray, 0, bytesArray.length);
		buffer.rewind();
		return bytesArray;
	}
}
