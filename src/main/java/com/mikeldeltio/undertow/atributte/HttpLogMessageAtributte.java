package com.mikeldeltio.undertow.atributte;

import com.mikeldeltio.undertow.model.log.HttpLogMessage;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

public class HttpLogMessageAtributte {

	public final static AttachmentKey<HttpLogMessage> HTTP_LOG_MSG_ATTACHMENT_KEY = AttachmentKey
			.create(HttpLogMessage.class);

	public static HttpLogMessage getHttpLogMessage(HttpServerExchange exchange) {
		return exchange.getAttachment(HTTP_LOG_MSG_ATTACHMENT_KEY);
	}

	public static HttpLogMessage setHttpLogMessage(HttpServerExchange exchange, HttpLogMessage httpLogMessage) {
		return exchange.putAttachment(HTTP_LOG_MSG_ATTACHMENT_KEY, httpLogMessage);
	}

}