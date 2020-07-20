package com.mikeldeltio.undertow.handler;

import static com.mikeldeltio.undertow.config.ConfigurationLoader.getConfiguration;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mikeldeltio.undertow.base.ReverseProxyTestBase;
import com.mikeldeltio.undertow.util.LogTestAppender;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class ReverseProxyLogHandlerTest extends ReverseProxyTestBase {

	private static LogTestAppender appender;

	@BeforeClass
	public static void setupLogger() {
		appender = new LogTestAppender();
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
	}

	@Test
	public void testGetMethod() throws Exception {
		createResponseOnMockServer(mockServer, 200);
		Request request = new Request.Builder()
				.url("http://localhost:" + getConfiguration().getServer().getPort() + "/test").get().build();
		Response response = httpClient.newCall(request).execute();
		assertEquals("Response code is not right", 200, response.code());
		assertLogged(containsString("\"method\":\"GET\",\"url\":\"http://localhost:8080/test\",\"statusCode\":200"));

	}

	@Test
	public void testPostMethod() throws Exception {
		createResponseOnMockServer(mockServer, 201);
		Request request = new Request.Builder()
				.url("http://localhost:" + getConfiguration().getServer().getPort() + "/test")
				.post(RequestBody.create(new String().getBytes())).build();
		Response response = httpClient.newCall(request).execute();
		assertEquals("Response code is not right", 201, response.code());
		assertLogged(containsString("\"method\":\"POST\",\"url\":\"http://localhost:8080/test\",\"statusCode\":201"));
	}

	@Test
	public void testPutMethod() throws Exception {
		createResponseOnMockServer(mockServer, 204);
		Request request = new Request.Builder()
				.url("http://localhost:" + getConfiguration().getServer().getPort() + "/test")
				.put(RequestBody.create(new String().getBytes())).build();
		Response response = httpClient.newCall(request).execute();
		assertEquals("Response code is not right", 204, response.code());
		assertLogged(containsString("\"method\":\"PUT\",\"url\":\"http://localhost:8080/test\",\"statusCode\":204"));
	}

	@Test
	public void testDeleteMethod() throws Exception {
		createResponseOnMockServer(mockServer, 204);
		Request request = new Request.Builder()
				.url("http://localhost:" + getConfiguration().getServer().getPort() + "/test").delete().build();
		Response response = httpClient.newCall(request).execute();
		assertEquals("Response code is not right", 204, response.code());
		assertLogged(containsString("\"method\":\"DELETE\",\"url\":\"http://localhost:8080/test\",\"statusCode\":204"));
	}

	private void createResponseOnMockServer(MockWebServer mockServer, int statusCode) {
		MockResponse serverResponse = new MockResponse();
		serverResponse.setResponseCode(statusCode);
		mockServer.enqueue(serverResponse);
	}

	private void assertLogged(Matcher<String> matcher) {
		for (LoggingEvent event : appender.events) {
			if (matcher.matches(event.getMessage())) {
				return;
			}
		}
		fail("No event matches " + matcher);
	}

}
