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
		String timerName = UUID.randomUUID().toString();
		String callback = "handleTimer";
		String state = "timer/" + timestamps.timestamp();
		Duration dueTime = Duration.ofSeconds(seconds);
		return registerActorTimer(timerName, callback, state, dueTime, Duration.ZERO).then();
	}

	@Override
	public Mono<Void> handleTimer(String state) {
		return timestamps.addTimestamp(state);
	}

	@Override
	public Mono<Void> reminder(int seconds) {
		String reminderName = UUID.randomUUID().toString();
		String state = "reminder/" + timestamps.timestamp();
		Duration dueTime = Duration.ofSeconds(seconds);
		return registerReminder(reminderName, state, dueTime, Duration.ZERO);
	}

	@Override
	public TypeRef<String> getStateType() {
		return TypeRef.get(String.class);
	}

	@Override
	public Mono<Void> receiveReminder(String reminderName, String state, Duration dueTime, Duration period) {
		return timestamps.addTimestamp(state);
	}
}
