package com.rs.game.model.entity.async

import com.rs.engine.thread.AsyncTaskExecutor
import com.rs.game.model.entity.Entity
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.*
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

class AsyncTaskScheduler {
    private val tasks = LinkedList<ScheduledTask>()
    private val namedTasks: MutableMap<String, ScheduledTask> = Object2ObjectOpenHashMap()

    fun tick() {
        val currentTasks = tasks.toList()
        for (task in currentTasks) {
            if (!tasks.contains(task)) continue

            if (!task.started) {
                task.started = true
                task.coroutine.resume(Unit)
            }

            task.tick()

            if (!task.isWaiting())
                tasks.remove(task)
        }
    }

    fun schedule(name: String? = null, block: suspend ScheduledTask.(CoroutineScope) -> Unit) {
        val task = ScheduledTask(name)
        if (namedTasks[name] != null)
            cancel(name)
        if (name != null)
            namedTasks[name] = task
        task.coroutine = suspend {
            block(task, CoroutineScope(AsyncTaskExecutor.getWorldThreadExecutor().asCoroutineDispatcher()))
        }.createCoroutine(completion = task)
        tasks.add(task)
    }

    fun cancel(name: String?) {
        val task = namedTasks[name];
        if (task != null) {
            task.stop()
            tasks.remove(task)
            namedTasks.remove(name)
        }
    }

    fun stopAll() {
        tasks.forEach { it.stop() }
        tasks.clear()
        namedTasks.clear()
    }
}

fun Entity.schedule(mapping: String, task: suspend ScheduledTask.(CoroutineScope) -> Unit) {
    this.asyncTasks.schedule(mapping, task)
}

fun Entity.schedule(task: suspend ScheduledTask.(CoroutineScope) -> Unit) {
    this.asyncTasks.schedule(null, task)
}

fun Entity.cancel(mapping: String) {
    this.asyncTasks.cancel(mapping)
}