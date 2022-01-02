package com.example.timestamp;

import java.time.LocalDateTime;

public record TimerReminderState(
		String source,
		@LocalDateTimeJson LocalDateTime sourceTimestamp) {
}