package com.rs.game.content.randomevents

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.game.World
import com.rs.game.content.randomevents.DrunkenDwarf.Companion.NPC_ID
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.Ticks

@ServerStartupEvent
fun handleDrunkenDwarf() {
    onNpcClick(NPC_ID) { event ->
        val npc = event.npc as? RandomEventNPC
        npc?.let { handleNpcClick(event.player, it) } ?: event.player.sendMessage("Something went wrong...")
    }
}

private fun handleNpcClick(player: Player, npc: RandomEventNPC) {
    if (npc.owner != player) {
        player.startConversation(Conversation(Dialogue().addNPC(npc, HeadE.CALM_TALK, "You're not my matey!")))
    } else {
        if (!npc.claimed) {
            if (player.inCombat()) {
                giveItemsAndSetDuration(true, npc, player)
                return
            }
            player.startConversation(Conversation(player)
                .addNPC(npc, HeadE.HAPPY_TALKING, "I 'new it were you matey! 'Ere, have some ob the good stuff!")
                .addNext { giveItemsAndSetDuration(false, npc, player) }
            )
        } else {
            player.sendMessage("Too late!")
        }
    }
}

private fun giveItemsAndSetDuration(forceTalk: Boolean, npc: RandomEventNPC, player: Player) {
    if (forceTalk) npc.forceTalk("'Ere, have some ob the good stuff!")
    player.inventory.addItemDrop(1917, 1)
    player.inventory.addItemDrop(1971, 1)
    npc.duration = 1
    npc.claimed = true
}

class DrunkenDwarf(owner: Player, tile: Tile) : RandomEventNPC(owner, NPC_ID, tile, DURATION, 2297, false) {

    companion object {
        const val NPC_ID = 956
        const val ATTACK_DELAY_TICKS = 50
        val DURATION = Ticks.fromMinutes(10)

        val PHRASES = arrayOf(
            "'Ere, matey, 'ave some 'o the good stuff.",
            "Hey, @player!",
            "Oi, are you der @player!",
            "Dunt ignore your mate, @player!",
            "I've got something for you, @player!",
            "Aww comeon, talk to ickle me @player!"
        )
    }

    private var attackDelay = 0
    private var phraseIndex = 0

    init {
        run = false
        isAutoDespawnAtDistance = false
        faceEntity(owner)
        forceTalk("'Ello der ${owner.displayName}! *hic*")
    }

    override fun processNPC() {
        val timeElapsed = DURATION - duration
        super.processNPC()
        if (duration == 3) {
            forceTalk("Aw me is all alone...")
        }
        if (phraseIndex < PHRASES.size && timeElapsed >= 50 && timeElapsed % 50 == 0 && !claimed) {
            forceTalk(getNextPhrase(owner))
            World.soundEffect(tile, 2297)
        } else {
            if (timeElapsed >= 350 && !claimed) {
                initiateAttack(this)
            }
        }
    }

    private fun getNextPhrase(player: Player): String {
        val phrase = PHRASES[phraseIndex].replace("@player", player.displayName)
        phraseIndex++
        return phrase
    }

    private fun initiateAttack(npc: RandomEventNPC) {
        if (attackDelay <= 0) {
            if (!owner.inCombat()) {
                forceTalk("I hates you ${owner.displayName}!")
                if (npc.lineOfSightTo(owner, false)) {
                    owner.soundEffect(npc.combatDefinitions.attackSound, false)
                    npc.anim(npc.combatDefinitions.attackEmote)
                    World.sendProjectile(npc, owner, npc.combatDefinitions.attackProjectile, 12, 32, 0, 0.7, 15)
                    owner.anim(424)
                    owner.applyHit(Hit(npc, Utils.random(200), Hit.HitLook.RANGE_DAMAGE))
                    attackDelay = ATTACK_DELAY_TICKS
                } else {
                    finish()
                }
            } else {
                giveItemsAndSetDuration(true, npc, owner)
            }
        }
        if (attackDelay > 0) {
            if (owner.interfaceManager.containsChatBoxInter() || owner.interfaceManager.containsScreenInter()) return
            attackDelay--
        }
    }
}