package com.rs.plugin.kts

import com.rs.plugin.events.NPCClickEvent
import com.rs.plugin.handlers.NPCClickHandler
import java.nio.file.Files
import java.nio.file.Paths
import javax.script.ScriptEngineManager
import javax.script.ScriptException

fun loadAndExecuteScripts() {
    val scriptEngine = ScriptEngineManager().getEngineByExtension("kts")
    Files.walk(Paths.get("./src/main"))
        .filter { Files.isRegularFile(it) && it.toString().endsWith(".plugin.kts") }
        .forEach { path ->
            try {
                val script = path.toFile().readText()
                scriptEngine.eval(script)
            } catch (e: ScriptException) {
                throw e;
            }
        }
}

fun handleNpcClick(vararg npcNamesOrIds: Any, options: Array<String>? = null, eventHandler: (NPCClickEvent) -> Unit) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    NPCClickEvent.registerMethod(NPCClickEvent::class.java, NPCClickHandler(npcNamesOrIds, options) { eventHandler(it) })
}