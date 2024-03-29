package com.rs.engine.cutscene

import com.rs.engine.dialogue.DialogueDsl
import com.rs.game.model.entity.player.Player

@DslMarker
annotation class CutsceneDsl

@CutsceneDsl
open class CutsceneBuilder {
    private var cutscene = object: Cutscene() {
        override fun construct(player: Player?) { }
    }



    internal open fun build(): Cutscene = cutscene
}

fun Player.playCutscene(block: CutsceneBuilder.() -> Unit) {
    playCutscene(CutsceneBuilder().apply(block).build())
}