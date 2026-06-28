// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.shared.api.RateLimitExceededException;
import java.time.Clock;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssistantRateLimitService {
	private static final long WINDOW_MILLIS = 60_000L;
	private static final long STALE_WINDOW_MILLIS = WINDOW_MILLIS * 5L;

	private final AssistantProperties properties;
	private final Clock clock;
	private final ConcurrentHashMap<Long, WindowCounter> counters = new ConcurrentHashMap<>();

	@Autowired
	public AssistantRateLimitService(AssistantProperties properties) {
		this(properties, Clock.systemUTC());
	}

	AssistantRateLimitService(AssistantProperties properties, Clock clock) {
		this.properties = properties;
		this.clock = clock;
	}

	public void check(Long userId) {
		long now = clock.millis();
		cleanup(now);

		WindowCounter counter = counters.computeIfAbsent(
				userId,
				ignored -> new WindowCounter(now)
		);

		if (!counter.incrementAllowed(now, properties.rateLimitPerMinute())) {
			throw new RateLimitExceededException("Assistant rate limit exceeded. Try again later.");
		}
	}

	private void cleanup(long now) {
		Iterator<Map.Entry<Long, WindowCounter>> iterator = counters.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Long, WindowCounter> entry = iterator.next();

			if (now - entry.getValue().windowStartedAt() > STALE_WINDOW_MILLIS) {
				iterator.remove();
			}
		}
	}

	private static final class WindowCounter {
		private long windowStartedAt;
		private int count;

		private WindowCounter(long windowStartedAt) {
			this.windowStartedAt = windowStartedAt;
		}

		private synchronized boolean incrementAllowed(long now, int maxRequests) {
			if (now - windowStartedAt >= WINDOW_MILLIS) {
				windowStartedAt = now;
				count = 0;
			}

			if (count >= maxRequests) {
				return false;
			}

			count++;
			return true;
		}

		private synchronized long windowStartedAt() {
			return windowStartedAt;
		}
	}
}