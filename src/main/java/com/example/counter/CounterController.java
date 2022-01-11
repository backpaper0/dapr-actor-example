package com.example.counter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.dapr.Topic;
import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorClient;
import io.dapr.actors.client.ActorProxyBuilder;
import io.dapr.client.DaprClient;
import io.dapr.client.domain.CloudEvent;
import reactor.core.publisher.Mono;

@RestController
public class CounterController {

	private static final Logger logger = LoggerFactory.getLogger(CounterController.class);
	private final ActorProxyBuilder<CounterActor> actorProxyBuilder;
	private final DaprClient daprClient;

	public CounterController(ActorClient actorClient, DaprClient daprClient) {
		this.actorProxyBuilder = new ActorProxyBuilder<>(
				"CounterActorImpl",
				CounterActor.class,
				actorClient);
		this.daprClient = daprClient;
	}

	@GetMapping("/counter/{id}")
	public Object count(@PathVariable ActorId id) {
		CounterActor counterActor = actorProxyBuilder.build(id);
		int count = counterActor.count();
		return Map.of("count", count);
	}

	@GetMapping("/counter/{id}/sleep/{sleep}")
	public Object countWithSleep(@PathVariable long sleep, @PathVariable ActorId id) {
		CounterActor counterActor = actorProxyBuilder.build(id);
		int count = counterActor.countWithSleep(sleep);
		return Map.of("count", count);
	}

	@GetMapping("/counter/and-hostname/{id}")
	public Object countAndHostname(@PathVariable ActorId id) {
		CounterActor counterActor = actorProxyBuilder.build(id);
		return counterActor.countAndHostname();
	}

	@GetMapping("/counter/{id}/pub")
	public Mono<Void> publish(@PathVariable String id) {
		return daprClient.publishEvent("pubsub", "count", id);
	}

	@Topic(pubsubName = "pubsub", name = "count")
	@PostMapping(path = "/counter/subscribe")
	public void subscribeCount(@RequestBody CloudEvent cloudEvent) {
		logger.info("Received message: {}", cloudEvent.getData());
		ActorId id = new ActorId((String) cloudEvent.getData());
		CounterActor counterActor = actorProxyBuilder.build(id);
		counterActor.count();
	}
}
