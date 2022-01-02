package com.example.timestamp;

import io.dapr.actors.ActorMethod;
import io.dapr.actors.ActorType;
import reactor.core.publisher.Mono;

@ActorType(name = "TimestampActor")
public interface TimestampActor {

	@ActorMethod(returns = String.class)
	Mono<String> timer(int seconds);

	@ActorMethod(returns = Void.class)
	Mono<Void> handleTimer(TimerReminderState state);

	@ActorMethod(returns = String.class)
	Mono<String> reminder(int seconds);

	@ActorMethod(returns = Void.class)
	Mono<Void> removeTimer(String timerName);

	@ActorMethod(returns = Void.class)
	Mono<Void> removeReminder(String reminderName);
}
