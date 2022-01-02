package com.example.timestamp;

import java.time.LocalDateTime;

public record TimestampState(
		@LocalDateTimeJson LocalDateTime timestamp,
		String source,
		@LocalDateTimeJson LocalDateTime sourceTimestamp) {
}
