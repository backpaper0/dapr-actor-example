package com.example.timestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import io.dapr.client.DaprClient;
import io.dapr.client.domain.State;
import io.dapr.utils.TypeRef;
import reactor.core.publisher.Mono;

@RestController
public class TimestampService {

	@Autowired
	private DaprClient daprClient;

	private final TypeRef<List<TimestampState>> stateType = new TypeRef<>() {
	};
	private final String storeName = "statestore";
	private final String key = "timestamps";

	public Mono<List<TimestampState>> timestamps() {
		return daprClient.getState(storeName, key, stateType)
				.mapNotNull(State::getValue)
				.defaultIfEmpty(List.of());
	}

	public Mono<Void> addTimestamp(String source) {
		return addTimestamp(source, now());
	}

	public Mono<Void> addTimestamp(String source, LocalDateTime sourceTimestamp) {
		return daprClient.getState(storeName, key, stateType)
				.mapNotNull(State::getValue)
				.defaultIfEmpty(List.of())
				.flatMap(timestamps -> {
					List<TimestampState> newTimestamps = new ArrayList<>();
					newTimestamps.addAll(timestamps);
					newTimestamps.add(new TimestampState(now(), source, sourceTimestamp));
					return daprClient.saveState(storeName, key, newTimestamps);
				});
	}

	public LocalDateTime now() {
		return LocalDateTime.now(ZoneOffset.ofHours(9));
	}
}
