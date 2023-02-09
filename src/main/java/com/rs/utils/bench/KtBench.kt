package com.rs.utils.bench

import com.rs.cache.Cache
import com.rs.game.model.entity.pathing.FixedTileStrategy
import com.rs.game.model.entity.pathing.RouteFinder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    for (i in 0..50) {
        runCoroutineTest()
    }
    println("JVM heated up. Starting...");

    var results: MutableList<Double> = mutableListOf();
    for (i in 0..50) {
        var start = System.nanoTime()
        runCoroutineTest()
        results.add((System.nanoTime() - start) / 1000000.0);
    }

    println("Calculated " + RouteFinder.COUNT + " routes across Burthorpe.")
    println("Kotlin coroutines: "+results.average()+" ms")
}

fun runCoroutineTest() {
    Cache.init("../cache/");
    runBlocking {
        (0 until 1000).map { i ->
            launch {
                RouteFinder.find(2888, 3452, 0, 1, FixedTileStrategy(2917, 3524), true)
            }
        }.forEach { it.join() }
    }
}