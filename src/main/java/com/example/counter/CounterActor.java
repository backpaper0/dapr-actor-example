package com.example.counter;

import java.util.List;

public interface CounterActor {

	int count();

	int countWithSleep(long sleep);

	List<Object> countAndHostname();
}
