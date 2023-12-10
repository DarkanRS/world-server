package com.rs.plugin.kts

import com.rs.cache.loaders.ObjectType
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.events.*
import com.rs.plugin.handlers.*
import com.rs.utils.TriFunction
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "PluginScript",
    fileExtension = "plugin.kts",
    compilationConfiguration = PluginCompilationConfiguration::class,
    evaluationConfiguration = PluginEvaluationConfiguration::class
)
abstract class PluginScript