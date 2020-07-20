package com.mikeldeltio.undertow.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class LogTestAppender extends AppenderSkeleton {

	public List<LoggingEvent> events = new ArrayList<LoggingEvent>();

	@Override
	protected void append(LoggingEvent event) {
		events.add(event);
	}

	@Override
	public void close() {

	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}
