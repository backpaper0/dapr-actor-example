package com.example.timestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.dapr.client.DaprClient;
import io.dapr.client.domain.State;
import io.dapr.utils.TypeRef;
import reactor.core.publisher.Mono;

@Component
public class TimestampService {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
	private final TypeRef<List<List<String>>> stateType = new TypeRef<>() {
	};
	private final String storeName = "statestore";
	private final String key = "timestamps";
	private final DaprClient daprClient;
	private final String source;

	public TimestampService(DaprClient daprClient) {
		this.daprClient = daprClient;
		this.source = Objects.toString(System.getenv("HOSTNAME"), "<none>");
	}

	public Mono<List<List<String>>> timestamps() {
		return daprClient.getState(storeName, key, stateType)
				.mapNotNull(State::getValue)
				.defaultIfEmpty(List.of());
	}

	public Mono<Void> addTimestamp(String source) {
		return daprClient.getState(storeName, key, stateType)
				.mapNotNull(State::getValue)
				.defaultIfEmpty(List.of())
				.flatMap(timestamps -> {
					var newState = List.of(timestamp(), source);
					var newTimestamps = Stream.concat(timestamps.stream(), Stream.of(newState)).toList();
					return daprClient.saveState(storeName, key, newTimestamps);
				});
	}

	public String timestamp() {
		return LocalDateTime.now(ZoneOffset.ofHours(9)).format(formatter);
	}

	public String source() {
		return source;
	}
}
