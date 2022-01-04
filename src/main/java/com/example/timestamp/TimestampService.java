package com.example.timestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.dapr.client.DaprClient;
import io.dapr.client.domain.State;
import io.dapr.utils.TypeRef;
import reactor.core.publisher.Mono;

@Component
public class TimestampService {

	private final TypeRef<List<String>> stateType = new TypeRef<>() {
	};
	private final String storeName = "statestore";
	private final String key = "timestamps";
	private final DaprClient daprClient;

	public TimestampService(DaprClient daprClient) {
		this.daprClient = daprClient;
	}

	public Mono<List<String>> timestamps() {
		return daprClient.getState(storeName, key, stateType)
				.mapNotNull(State::getValue)
				.defaultIfEmpty(List.of());
	}

	public Mono<Void> addTimestamp() {
		return timestamps()
				.flatMap(timestamps -> {
					var timestamp = LocalDateTime.now().toString();
					var newTimestamps = Stream.concat(
							timestamps.stream(),
							Stream.of(timestamp)).toList();
					return daprClient.saveState(storeName, key, newTimestamps);
				});
	}
}
