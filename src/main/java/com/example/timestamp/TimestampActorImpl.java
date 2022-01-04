package com.example.timestamp;

import java.time.Duration;
import java.util.UUID;

import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorRuntimeContext;
import io.dapr.actors.runtime.Remindable;
import io.dapr.utils.TypeRef;
import reactor.core.publisher.Mono;

public class TimestampActorImpl extends AbstractActor implements TimestampActor, Remindable<String> {

	private final TimestampService timestamps;

	public TimestampActorImpl(ActorRuntimeContext<TimestampActorImpl> runtimeContext, ActorId id,
			TimestampService timestamps) {
		super(runtimeContext, id);
		this.timestamps = timestamps;
	}

	@Override
	public String hostname() {
		return System.getenv("HOSTNAME");
	}

	@Override
	public Mono<Void> timer(int seconds) {
		String callback = "handleTimer";
		Duration dueTime = Duration.ofSeconds(seconds);
		return registerActorTimer(null, callback, "", dueTime, Duration.ofMillis(-1)).then();
	}

	@Override
	public Mono<Void> handleTimer(String state) {
		return timestamps.addTimestamp();
	}

	@Override
	public Mono<Void> reminder(int seconds) {
		Duration dueTime = Duration.ofSeconds(seconds);
		return registerReminder(UUID.randomUUID().toString(), "", dueTime, Duration.ofMillis(-1));
	}

	@Override
	public TypeRef<String> getStateType() {
		return TypeRef.get(String.class);
	}

	@Override
	public Mono<Void> receiveReminder(String reminderName, String state, Duration dueTime, Duration period) {
		return timestamps.addTimestamp();
	}
}
