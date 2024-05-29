package com.rs.game.content.randomevents

import com.rs.game.World
import com.rs.game.model.entity.npc.OwnedNPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.utils.Ticks

abstract class RandomEventNPC(
    owner: Player,
    id: Int,
    tile: Tile,
    var duration: Int = Ticks.fromMinutes(10),
    private var startSoundId: Int? = DEFAULT_SOUND,
    hideFromOtherPlayers: Boolean
) : OwnedNPC(owner, id, tile, hideFromOtherPlayers) {

    companion object {
        const val DEFAULT_START_GFX = 86
        const val DEFAULT_SOUND = 1930
        const val DEFAULT_END_GFX = 1605
    }

    var claimed = false
    var ticks = 0

    init {
        playSoundAndAnim(startSoundId, DEFAULT_START_GFX)
        this.run = false
        this.faceEntity(owner)
        this.isAutoDespawnAtDistance = false
    }

    override fun processNPC() {
        if (owner.isDead || !withinDistance(owner, 16)) {
            finish()
            return
        }
        if (!claimed && (owner.interfaceManager.containsChatBoxInter() || owner.interfaceManager.containsScreenInter())) return
        ticks++
        entityFollow(owner, false, 0)
        duration--
        if (duration == -1)
            finish()
        super.processNPC()
    }

    override fun finish() {
        reset()
        playSoundAndAnim(DEFAULT_SOUND, DEFAULT_END_GFX)
        owner.interfaceManager.closeChatBoxInterface()
        super.finish()
    }

    private fun playSoundAndAnim(soundId: Int?, spotAnimId: Int?) {
        val actualSoundId = soundId ?: DEFAULT_SOUND
        val actualSpotAnimId = spotAnimId ?: DEFAULT_START_GFX
        owner.soundEffect(owner, actualSoundId, false)
        World.sendSpotAnim(tile, actualSpotAnimId)
    }
}