package com.mikeldeltio.undertow.handler;

import static com.mikeldeltio.undertow.config.ConfigurationLoader.getConfiguration;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mikeldeltio.undertow.base.ReverseProxyTestBase;

import okhttp3.Request;
import okhttp3.Response;

public class HealthCheckHandlerTests extends ReverseProxyTestBase {

	@Test
	public void testHealthCheck() throws Exception {
		Request request = new Request.Builder()
				.url("http://localhost:" + getConfiguration().getServer().getPort() + "/health").get().build();
		Response response = httpClient.newCall(request).execute();
		assertEquals("Response code is not right", 200, response.code());
		assertEquals("Response message is not right", "OK", response.message());
	}

}
