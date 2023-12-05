package com.rs.plugin.kts

import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

object PluginCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports("com.rs.plugin.kts.*", "com.rs.engine.dialogue.DialogueDsl", "com.rs.engine.dialogue.*", "com.rs.engine.dialogue.HeadE.*")
    jvm { dependenciesFromCurrentContext(wholeClasspath = true) }
    ide { acceptedLocations(ScriptAcceptedLocation.Everywhere) }
    compilerOptions.append("-Xadd-modules=ALL-MODULE-PATH")
})

object PluginEvaluationConfiguration : ScriptEvaluationConfiguration({

})