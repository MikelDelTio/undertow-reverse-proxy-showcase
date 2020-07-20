package com.mikeldeltio.undertow.config;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mikeldeltio.undertow.config.properties.Configuration;

public class ConfigurationLoader {

	private static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

	private static Configuration configuration;

	private static final String UNDERTOW_CONFIG_FILENAME = "/undertow.yaml";

	public static Configuration getConfiguration() throws IOException {
		if (configuration == null) {
			configuration = mapper.readValue(ConfigurationLoader.class.getResourceAsStream(UNDERTOW_CONFIG_FILENAME),
					Configuration.class);
		}
		return configuration;
	}
}