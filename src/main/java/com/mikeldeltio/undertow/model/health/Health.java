package com.mikeldeltio.undertow.model.health;

import static com.mikeldeltio.undertow.model.health.Status.DOWN;
import static com.mikeldeltio.undertow.model.health.Status.UP;
import static java.util.Collections.unmodifiableMap;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Health {

	private final Status status;

	private final Map<String, Object> details;

	private Health(Builder builder) {
		this.status = builder.status;
		this.details = unmodifiableMap(builder.details);
	}

	@JsonInclude(Include.NON_EMPTY)
	public Map<String, Object> getDetails() {
		return this.details;
	}

	public Status getStatus() {
		return this.status;
	}

	public static Builder down() {
		return status(DOWN);
	}

	public static Builder down(Throwable ex) {
		return down().withException(ex);
	}

	public static Builder status(Status status) {
		return new Builder(status);
	}

	public static Builder up() {
		return status(UP);
	}

	public static class Builder {

		private Status status;

		private Map<String, Object> details;

		public Builder(Status status) {
			this.status = status;
			this.details = new LinkedHashMap<>();
		}

		public Builder withException(Throwable ex) {
			return withDetail("error", ex.getClass().getName() + ": " + ex.getMessage());
		}

		public Builder withDetail(String key, Object value) {
			this.details.put(key, value);
			return this;
		}

		public Health build() {
			return new Health(this);
		}
	}

}
