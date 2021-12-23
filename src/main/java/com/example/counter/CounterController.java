package com.example.counter;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorClient;
import io.dapr.actors.client.ActorProxyBuilder;

@RestController
public class CounterController {

	private final ActorProxyBuilder<CounterActor> actorProxyBuilder;

	public CounterController(ActorClient actorClient) {
		this.actorProxyBuilder = new ActorProxyBuilder<>(
				"CounterActorImpl",
				CounterActor.class,
				actorClient);
	}

	@GetMapping("/{id}")
	public Object count(@PathVariable ActorId id) {
		CounterActor counterActor = actorProxyBuilder.build(id);
		int count = counterActor.count();
		return Map.of("count", count);
	}

	@GetMapping("/{id}/sleep/{sleep}")
	public Object countWithSleep(@PathVariable long sleep, @PathVariable ActorId id) {
		CounterActor counterActor = actorProxyBuilder.build(id);
		int count = counterActor.countWithSleep(sleep);
		return Map.of("count", count);
	}

	@GetMapping("/and-hostname/{id}")
	public Object countAndHostname(@PathVariable ActorId id) {
		CounterActor counterActor = actorProxyBuilder.build(id);
		return counterActor.countAndHostname();
	}
}
