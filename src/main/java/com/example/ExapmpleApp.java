package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.counter.CounterActorImpl;

import io.dapr.actors.client.ActorClient;
import io.dapr.actors.runtime.ActorRuntime;

@SpringBootApplication
public class ExapmpleApp {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		SpringApplication.run(ExapmpleApp.class, args);
	}

	@Bean(destroyMethod = "close")
	public ActorRuntime actorRuntime() {
		ActorRuntime actorRuntime = ActorRuntime.getInstance();
		actorRuntime.registerActor(CounterActorImpl.class);
		return actorRuntime;
	}

	@Bean(destroyMethod = "close")
	public ActorClient actorClient() {
		return new ActorClient();
	}
}
