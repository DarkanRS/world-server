package com.rs.game.model.entity.pathing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RouteFinder {
	private static final ExecutorService SERVICE = Executors.newVirtualThreadPerTaskExecutor();
	public static AtomicInteger COUNT = new AtomicInteger();

}
