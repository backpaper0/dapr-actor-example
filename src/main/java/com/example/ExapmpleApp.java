package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.counter.CounterActorImpl;
import com.example.timestamp.TimestampActorImpl;
import com.example.timestamp.TimestampService;

import io.dapr.actors.client.ActorClient;
import io.dapr.actors.runtime.ActorRuntime;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;

@SpringBootApplication
public class ExapmpleApp {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		SpringApplication.run(ExapmpleApp.class, args);
	}

	@Bean(destroyMethod = "close")
	public ActorRuntime actorRuntime(TimestampService timestamps) {
		ActorRuntime actorRuntime = ActorRuntime.getInstance();
		actorRuntime.registerActor(CounterActorImpl.class);
		actorRuntime.registerActor(TimestampActorImpl.class,
				(runtimeContext, id) -> new TimestampActorImpl(runtimeContext, id, timestamps));
		return actorRuntime;
	}

	@Bean(destroyMethod = "close")
	public ActorClient actorClient() {
		return new ActorClient();
	}

	@Bean(destroyMethod = "close")
	public DaprClient daprClient() {
		return new DaprClientBuilder().build();
	}
}
