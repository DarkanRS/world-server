package com.rs.game.model.entity;

import com.rs.game.model.entity.player.Player

class ScheduleBuilder {
    private val actions = sortedMapOf<Int, MutableList<() -> Unit>>()
    private var currentTime = 0

    fun wait(ticks: Int) {
        currentTime += ticks
    }

    fun action(block: () -> Unit) {
        actions.getOrPut(currentTime) { mutableListOf() }.add(block)
    }

    operator fun (() -> Unit).unaryPlus() {
        action(this)
    }

    fun build(): (Int) -> Boolean = { tick ->
        actions[tick]?.forEach { it.invoke() }
        val futureActionsExist = actions.keys.any { it > tick }
        if (!futureActionsExist) actions.clear()
        futureActionsExist
    }
}

fun Player.schedule(init: ScheduleBuilder.() -> Unit) {
    val builder = ScheduleBuilder().apply(init)
    this.tasks.scheduleTimer(0, 0, builder.build())
}
