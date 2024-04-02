package com.rs.engine.cutscenekt

import com.rs.engine.thread.AsyncTaskExecutor
import com.rs.game.model.entity.player.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

class CutscenePresenter {
    private var currentCutscene: Cutscene? = null

    fun tick() {
        val cutscene = currentCutscene ?: return
        if (!cutscene.started) {
            cutscene.start()
            cutscene.coroutine.resume(Unit)
        }
        cutscene.tick()

        if (!cutscene.isWaiting())
            cutscene.stop()
    }

    fun present(player: Player, block: suspend Cutscene.(CoroutineScope) -> Unit) {
        val cutscene = Cutscene(player)
        currentCutscene = cutscene
        cutscene.coroutine = suspend {
            block(cutscene, CoroutineScope(AsyncTaskExecutor.getWorldThreadExecutor().asCoroutineDispatcher()))
        }.createCoroutine(completion = cutscene)
    }

    fun stop() {
        currentCutscene = null
    }
}

fun Player.cutscene(block: suspend Cutscene.(CoroutineScope) -> Unit) {
    this.cutscenePresenter.present(this, block)
}