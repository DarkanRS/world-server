package com.rs.plugin.kts

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.system.exitProcess

class KotlinScriptEvaluator {
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
            val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<PluginScriptTemplate> {
                jvm { dependenciesFromCurrentContext(wholeClasspath = true) }
                compilerOptions.append("-Xadd-modules=ALL-MODULE-PATH")
            }
            return BasicJvmScriptingHost().eval(
                scriptFile.toScriptSource(),
                compilationConfiguration,
                ScriptEvaluationConfiguration {

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