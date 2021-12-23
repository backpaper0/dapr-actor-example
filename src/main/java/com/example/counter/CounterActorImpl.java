package com.example.counter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorMethodContext;
import io.dapr.actors.runtime.ActorRuntimeContext;
import reactor.core.publisher.Mono;

public class CounterActorImpl extends AbstractActor implements CounterActor {

	private int count;

	public CounterActorImpl(ActorRuntimeContext<CounterActorImpl> runtimeContext, ActorId id) {
		super(runtimeContext, id);
	}

	@Override
	public int count() {
		count++;
		return count;
	}

	@Override
	protected Mono<Void> onActivate() {
		return getActorStateManager()
				.get("count", int.class)
				.flatMap(count -> {
					this.count = count;
					return Mono.empty();
				});
	}

	@Override
	protected Mono<Void> onPostActorMethod(ActorMethodContext actorMethodContext) {
		return getActorStateManager()
				.set("count", this.count);
	}

	@Override
	public int countWithSleep(long sleep) {
		int count = this.count;
		try {
			TimeUnit.SECONDS.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace(System.out);
		}
		return this.count = count + 1;
	}

	@Override
	public List<Object> countAndHostname() {
		count++;
		return List.of(count, System.getenv("HOSTNAME"));
	}
}
