package com.rs.utils.bench

import com.rs.cache.Cache
import com.rs.game.model.entity.pathing.FixedTileStrategy
import com.rs.game.model.entity.pathing.RouteFinder
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private lateinit var executorService: ExecutorService
private lateinit var executorCoroutineDispatcher: ExecutorCoroutineDispatcher

fun main() {
    executorService = Executors.newVirtualThreadPerTaskExecutor()
    executorCoroutineDispatcher = executorService.asCoroutineDispatcher()

    for (i in 0..50)
        runLoomCoroutineTest()
    println("JVM heated up. Starting...");

    var results: MutableList<Double> = mutableListOf();
    for (i in 0..50) {
        var start = System.nanoTime()
        runLoomCoroutineTest()
        results.add((System.nanoTime() - start) / 1000000.0);
    }

    println("Calculated " + RouteFinder.COUNT + " routes across Burthorpe.")
    println("Kotlin loom coroutines: "+results.average()+" ms")
}

fun runLoomCoroutineTest() {
    Cache.init("../cache/");
    runBlocking(executorCoroutineDispatcher) {
        (0 until 1000).map { _ ->
            launch {
                RouteFinder.find(2888, 3452, 0, 1, FixedTileStrategy(2917, 3524), true)
            }
        }.forEach { it.join() }
    }
}