package com.rs.plugin.kts

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.createJvmEvaluationConfigurationFromTemplate
import kotlin.system.exitProcess

class PluginScriptHost {
    companion object {
        fun loadAndExecuteScripts(): Int {
            var scriptCount = 0;
            Files.walk(Paths.get("./plugins/"))
                .filter { Files.isRegularFile(it) && it.toString().endsWith(".plugin.kts") }
                .forEach { path ->
                    val result = evalFile(path.toFile())
                    if (result is ResultWithDiagnostics.Failure) {
                        logScriptError(result)
                        exitProcess(1)
                    } else
                        scriptCount++
                }
            return scriptCount
        }

        private fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
            return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), createJvmCompilationConfigurationFromTemplate<PluginScript> {
                defaultImports("com.rs.plugin.kts.*", "com.rs.engine.dialogue.DialogueDsl", "com.rs.engine.dialogue.*", "com.rs.engine.dialogue.HeadE.*")
                jvm { dependenciesFromCurrentContext(wholeClasspath = true) }
                ide { acceptedLocations(ScriptAcceptedLocation.Everywhere) }
                compilerOptions.append("-Xadd-modules=ALL-MODULE-PATH")
            }, createJvmEvaluationConfigurationFromTemplate<PluginScript> {

            })
        }

        private fun logScriptError(result: ResultWithDiagnostics.Failure) {
            println("Script evaluation failed:")
            result.reports.forEach { report ->
                println(" - ${report.message} (severity: ${report.severity})")
            }
        }
    }
}