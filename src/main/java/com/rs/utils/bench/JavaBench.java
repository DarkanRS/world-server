package com.rs.utils.bench;

import com.rs.cache.Cache;
import com.rs.game.model.entity.pathing.FixedTileStrategy;
import com.rs.game.model.entity.pathing.RouteFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JavaBench {
    private static ExecutorService service = Executors.newFixedThreadPool(1);
    public static void main(String[] args) throws IOException, InterruptedException {
        Cache.init("../cache/");
        for (int i = 0;i <= 50;i++) {
            test();
        }
        System.out.println("JVM heated up. Starting...");

        List<Double> results = new ArrayList<>();
        for (int i = 0;i <= 50;i++) {
            var start = System.nanoTime();
            test();
            results.add((System.nanoTime() - start) / 1000000.0);
        }
        service.shutdown();

        System.out.println("Calculated " + RouteFinder.COUNT + " routes across Burthorpe.");
        System.out.println("Java executor service: "+results.stream().mapToDouble(a -> a).average().getAsDouble()+" ms");
    }

    private static void test() {
        CompletableFuture[] futures = new CompletableFuture[1000];
        for (int i = 0;i < futures.length;i++) {
            futures[i] = CompletableFuture.runAsync(() -> RouteFinder.find(2888, 3452, 0, 1, new FixedTileStrategy(2917, 3524), true), service);
        }
        CompletableFuture.allOf(futures).join();
        for (CompletableFuture f : futures)
            if (f.isCompletedExceptionally())
                f.exceptionNow().printStackTrace();
    }
}
