package com.rs.plugin.kts

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.script.ScriptException
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class KotlinScriptEvaluator {
    companion object {
        fun loadAndExecuteScripts(): Int {
            var scriptCount = 0;
            Files.walk(Paths.get("./src/main"))
                .filter { Files.isRegularFile(it) && it.toString().endsWith(".plugin.kts") }
                .forEach { path ->
                    try {
                        evalFile(path.toFile())
                        scriptCount++;
                    } catch (e: ScriptException) {
                        throw e;
                    }
                }
            return scriptCount
        }

        @KotlinScript(fileExtension = "plugin.kts")
        abstract class PluginScript

        private fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
            val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<PluginScript> {
                jvm { dependenciesFromCurrentContext(wholeClasspath = true) }
            }
            return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, ScriptEvaluationConfiguration {
                
            })
        }
    }
}