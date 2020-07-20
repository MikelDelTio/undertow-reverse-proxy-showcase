package com.mikeldeltio.undertow.base;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.mikeldeltio.undertow.ReverseProxyServer;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;

public class ReverseProxyTestBase {

	protected MockWebServer mockServer = new MockWebServer();

	protected final OkHttpClient httpClient = new OkHttpClient();

	@BeforeClass
	public static void setup() throws IOException {
		ReverseProxyServer.run();
	}

	@Before
	public void setUpHttpServer() throws IOException {
		mockServer.start(8888);
	}

	@After
	public void clean() throws IOException {
		mockServer.shutdown();
	}

	@AfterClass
	public static void stopResources() {
		ReverseProxyServer.tearDown();
	}
}
