package com.example.timestamp;

import io.dapr.actors.ActorType;
import reactor.core.publisher.Mono;

@ActorType(name = "TimestampActor")
public interface TimestampActor {

	String hostname();

	Mono<Void> timer(int seconds);

	Mono<Void> handleTimer(String source);

	Mono<Void> reminder(int seconds);
}
