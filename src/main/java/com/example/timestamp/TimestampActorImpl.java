package com.example.timestamp;

import java.time.Duration;
import java.util.UUID;

import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorRuntimeContext;
import io.dapr.actors.runtime.Remindable;
import io.dapr.utils.TypeRef;
import reactor.core.publisher.Mono;

public class TimestampActorImpl extends AbstractActor implements TimestampActor, Remindable<TimerReminderState> {

	private final TimestampService timestamps;

	public TimestampActorImpl(ActorRuntimeContext<TimestampActorImpl> runtimeContext, ActorId id,
			TimestampService timestamps) {
		super(runtimeContext, id);
		this.timestamps = timestamps;
	}

	@Override
	public Mono<String> timer(int seconds) {
		String timerName = UUID.randomUUID().toString();
		String callback = "handleTimer";
		TimerReminderState state = new TimerReminderState("timer/" + timerName, timestamps.now());
		Duration dueTime = Duration.ofSeconds(seconds);
		return registerActorTimer(timerName, callback, state, dueTime, Duration.ZERO);
	}

	@Override
	public Mono<Void> removeTimer(String timerName) {
		return unregisterTimer(timerName);
	}

	@Override
	public Mono<Void> handleTimer(TimerReminderState state) {
		return timestamps.addTimestamp(state.source(), state.sourceTimestamp());
	}

	@Override
	public Mono<String> reminder(int seconds) {
		String reminderName = UUID.randomUUID().toString();
		TimerReminderState state = new TimerReminderState("reminder/" + reminderName, timestamps.now());
		Duration dueTime = Duration.ofSeconds(seconds);
		return registerReminder(reminderName, state, dueTime, Duration.ZERO)
				.thenReturn(reminderName);
	}

	@Override
	public Mono<Void> removeReminder(String reminderName) {
		return unregisterReminder(reminderName);
	}

	@Override
	public TypeRef<TimerReminderState> getStateType() {
		return TypeRef.get(TimerReminderState.class);
	}

	@Override
	public Mono<Void> receiveReminder(String reminderName, TimerReminderState state, Duration dueTime,
			Duration period) {
		return timestamps.addTimestamp(state.source(), state.sourceTimestamp());
	}
}
