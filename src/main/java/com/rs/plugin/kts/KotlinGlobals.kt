package com.rs.plugin.kts

import com.rs.plugin.events.NPCClickEvent
import com.rs.plugin.handlers.NPCClickHandler

fun handleNpcClick(vararg npcNamesOrIds: Any, options: Array<String>? = null, eventHandler: (NPCClickEvent) -> Unit) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    NPCClickEvent.registerMethod(NPCClickEvent::class.java, NPCClickHandler(npcNamesOrIds, options) { eventHandler(it) })
}