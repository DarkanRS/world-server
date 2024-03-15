package com.rs.game.model.entity.async

import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation

data class WaitCondition(val wait: Wait, val continuation: Continuation<Unit>)

abstract class Wait {
    abstract fun canContinue(): Boolean
}

class TickWait(ticks: Int) : Wait() {
    private val ticks = AtomicInteger(ticks)
    override fun canContinue(): Boolean = ticks.decrementAndGet() <= 0
}

class ConditionalWait(private val predicate: () -> Boolean) : Wait() {
    override fun canContinue(): Boolean = predicate.invoke()
}