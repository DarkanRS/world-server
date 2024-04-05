package com.rs.game.model.entity.async

import com.rs.lib.util.Logger
import kotlin.coroutines.*

class ScheduledTask(val mapping: String? = null, private val onStop: ((ScheduledTask).() -> Unit)? = null) : Continuation<Unit> {
    lateinit var coroutine: Continuation<Unit>
    private var waitCondition: WaitCondition? = null
    var started = false

    override val context: CoroutineContext = EmptyCoroutineContext

    internal fun tick() {
        val next = waitCondition ?: return

        if (next.wait.canContinue()) {
            next.continuation.resume(Unit)
        }
    }

    override fun resumeWith(result: Result<Unit>) {
        waitCondition = null
        result.exceptionOrNull()?.let { e -> Logger.handle(ScheduledTask::class.java, "resumeWith", e) }
    }

    fun stop() {
        waitCondition = null
        onStop?.invoke(this)
    }

    fun isWaiting(): Boolean = waitCondition != null

    suspend fun wait(ticks: Int): Unit = suspendCoroutine {
        waitCondition = WaitCondition(TickWait(ticks), it)
    }

    suspend fun wait(condition: () -> Boolean): Unit = suspendCoroutine {
        waitCondition = WaitCondition(ConditionalWait { condition() }, it)
    }
}