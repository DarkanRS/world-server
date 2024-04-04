package com.rs.game.content.randomevents

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.randomevents.Genie.Companion.XP_LAMP_ITEM_ID
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.NPCClickEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.Ticks

@ServerStartupEvent
fun handleGenieRandomEvent() {
    onNpcClick(Genie.NPC_ID) { event ->
        val npc = event.npc as? Genie ?: return@onNpcClick
        if (npc.ticks >= Genie.DURATION) return@onNpcClick
        if (npc.owner != event.player) {
            event.player.startConversation { npc(npc, HeadE.CALM_TALK, "This wish is for ${npc.owner.displayName}, not you!") }
            return@onNpcClick
        }
        handleGenieInteraction(event, npc)
    }
}

private fun handleGenieInteraction(e: NPCClickEvent, npc: Genie) {
    if (e.player.inCombat()) {
        handleCombatInteraction(e, npc)
    } else {
        handleNormalInteraction(e, npc)
    }
}

private fun handleCombatInteraction(e: NPCClickEvent, npc: Genie) {
    val player = e.player
    if (!npc.claimed) {
        player.sendMessage("The genie gives you a lamp!")
        player.inventory.addItemDrop(XP_LAMP_ITEM_ID, 1)
        if (!player.inventory.hasFreeSlots()) {
            player.sendMessage("The lamp has been placed on the ground.")
        }
        npc.forceTalk("Hope that satisfies you!")
        npc.claimed = true
        npc.tickCounter = 191
    } else {
        player.sendMessage("Too late!")
    }
}

fun handleNormalInteraction(e: NPCClickEvent, npc: Genie) {
    if (!npc.claimed) {
        e.player.startConversation {
            npc(Genie.NPC_ID, HeadE.HAPPY_TALKING, "Ah, so you are there master. I'm so glad you summoned me. Please take this lamp and make your wish!")
            exec {
                e.player.inventory.addItemDrop(XP_LAMP_ITEM_ID, 1)
                if (!e.player.inventory.hasFreeSlots()) e.player.sendMessage("The lamp has been placed on the ground.")
                npc.claimed = true
                npc.tickCounter = 193
            }
        }
    } else {
        e.player.sendMessage("Too late!")
    }
}

class Genie(owner: Player, tile: Tile) : RandomEventNPC(owner, NPC_ID, tile, DURATION, null, false) {

    companion object {
        const val NPC_ID = 3022
        const val XP_LAMP_ITEM_ID = 2528
        val DURATION = Ticks.fromMinutes(10)
    }

    var tickCounter = 0

    init {
        run = false
        forceTalk("Hello, Master ${owner.displayName}!")
        faceEntity(owner)
        isAutoDespawnAtDistance = false
    }

    override fun processNPC() {
        super.processNPC()
        entityFollow(owner, false, 0)
        if (!claimed && (owner.interfaceManager.containsChatBoxInter() || owner.interfaceManager.containsScreenInter())) return
        tickCounter++
        handleGenieActions()
    }

    private fun handleGenieActions() {
        when (tickCounter) {
            in 30..150 step 30 -> continueGenieDialogue()
            189 -> forceTalk("So rude!")
            190 -> handlePlayerIgnore()
            194 -> if (claimed) anim(863)
            197 -> finish()
        }
    }

    private fun continueGenieDialogue() {
        anim(863)
        forceTalk(getGenieDialogue())
    }

    private fun getGenieDialogue(): String {
        val dialogues = arrayOf("A wish for ", "I came from the desert, you know, ", "Not just anyone gets a wish, ", "Young " + (if (owner.appearance.isMale) "Sir" else "Madam") + ", these things are quite rare!", "Last chance, ")
        val index = tickCounter / 30 - 1
        return when {
            index < dialogues.size - 2 -> "${dialogues[index]}${owner.displayName}."
            index == dialogues.size - 2 -> dialogues[index]
            else -> "${dialogues[index]}${owner.displayName}."
        }
    }

    private fun handlePlayerIgnore() {
        if (!owner.inCombat()) {
            anim(3045)
            owner.spotAnim(80, 5, 60)
            owner.lock()
            owner.anim(836)
            owner.stopAll()
            owner.fadeScreen {
                spotAnim(1605)
                owner.tele(RandomEvents.getRandomTile())
                owner.sendMessage("The genie knocks you out and you wake up somewhere... different.")
                owner.anim(-1)
                owner.schedule {
                    wait(2)
                    owner.spotAnim(-1)
                    owner.unlock()
                }
            }
        }
    }
}
