package com.example.timestamp;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorClient;
import io.dapr.actors.client.ActorProxyBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/timestamps")
public class TimestampController {

	private final TimestampService timestamps;
	private final ActorProxyBuilder<TimestampActor> actorProxyBuilder;

	public TimestampController(TimestampService timestamps, ActorClient actorClient) {
		this.timestamps = timestamps;
		this.actorProxyBuilder = new ActorProxyBuilder<>(TimestampActor.class, actorClient);
	}

	@GetMapping
	public Mono<List<TimestampState>> timestamps() {
		return timestamps.timestamps();
	}

	@PostMapping
	public Mono<Void> addTimestamp() {
		return timestamps.addTimestamp("controller");
	}

	@PostMapping("/{actorId}/timer")
	public Mono<String> addTimer(@PathVariable ActorId actorId, @RequestParam int seconds) {
		TimestampActor timestampActor = actorProxyBuilder.build(actorId);
		return timestampActor.timer(seconds);
	}

	@PostMapping("/{actorId}/reminder")
	public Mono<String> addReminer(@PathVariable ActorId actorId, @RequestParam int seconds) {
		TimestampActor timestampActor = actorProxyBuilder.build(actorId);
		return timestampActor.reminder(seconds);
	}

	@DeleteMapping("/{actorId}/timer")
	public Mono<Void> removeTimer(@PathVariable ActorId actorId, @RequestParam String name) {
		TimestampActor timestampActor = actorProxyBuilder.build(actorId);
		return timestampActor.removeTimer(name);
	}

	@DeleteMapping("/{actorId}/reminder")
	public Mono<Void> removeReminer(@PathVariable ActorId actorId, @RequestParam String name) {
		TimestampActor timestampActor = actorProxyBuilder.build(actorId);
		return timestampActor.removeReminder(name);
	}
}
