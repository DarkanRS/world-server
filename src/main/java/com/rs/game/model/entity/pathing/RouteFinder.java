package com.rs.game.model.entity.pathing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RouteFinder {
	private static final ExecutorService SERVICE = Executors.newVirtualThreadPerTaskExecutor();
	public static AtomicInteger COUNT = new AtomicInteger();
	
	public static CompletableFuture<Route> findAsync(int x, int y, int z, int size, RouteStrategy target, boolean ignoreTileEventTiles) {
		CompletableFuture<Route> future = new CompletableFuture<>();
		SERVICE.submit(() -> {
			try {
				future.complete(new Route().find(x, y, z, size, target, ignoreTileEventTiles));
				COUNT.incrementAndGet();
			} catch(Throwable e) {
				future.completeExceptionally(e);
			}
		});
		return future;
	}
	
	public static Route find(int x, int y, int z, int size, RouteStrategy target, boolean ignoreTileEventTiles) {
		Route route = new Route().find(x, y, z, size, target, ignoreTileEventTiles);
		COUNT.incrementAndGet();
		return route;
	}
}
