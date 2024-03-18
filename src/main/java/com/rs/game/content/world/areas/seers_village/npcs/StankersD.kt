package com.rs.game.content.world.areas.seers_village.npcs

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.game.content.achievements.AchievementSystemDialogue
import com.rs.game.content.achievements.SetReward
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.utils.shop.ShopsHandler

class StankersD(player: Player, npc: NPC) : Conversation(player) {
    init {
        player.startConversation(Dialogue()
            .addNPC(npc.id, HeadE.HAPPY_TALKING, "Hello, bold adventurer.")
            .addOptions("What would you like to say?") { initialOps -> initialOptions(initialOps, npc) })
    }

    private fun initialOptions(initialOps: Options, npc: NPC) {
        initialOps.add("Are these your trucks?") {
            player.startConversation(Dialogue()
                .addPlayer(HeadE.CALM, "Are these your trucks?")
                .addNPC(npc.id, HeadE.CALM, "Yes, I use them to transport coal over the river. I will let other people use them too, I'm a nice person like that...")
                .addNPC(npc.id, HeadE.CALM, "Just put coal in a truck and I'll move it down to my depot over the river.")
                .addOptions("What would you like to say?") { initialOps -> initialOptions(initialOps, npc) })
        }

        initialOps.add("Hello, Mr Stankers.") {
            player.startConversation(Dialogue()
                .addPlayer(HeadE.CHEERFUL, "Hello, Mr Stankers.")
                .addNPC(npc.id, HeadE.CALM, "Would you like a poison chalice?")
                .addOptions("Would you like a poison chalice?") { chaliceOps -> chaliceOptions(chaliceOps, npc) })
        }

        initialOps.add("About the Achievement System...") {
            AchievementSystemDialogue(player, npc.id, SetReward.SEERS_HEADBAND).start()
        }
    }

    private fun chaliceOptions(chaliceOps: Options, npc: NPC) {
        chaliceOps.add("Yes, please.") {
            player.startConversation(
                Dialogue()
                    .addPlayer(HeadE.NO_EXPRESSION, "Yes, please.")
                    .addNPC(npc.id, HeadE.CHEERFUL, "Take one from my shop. You might consider buying one of my fine pickaxes while you're looking.")
                    .addPlayer(HeadE.CONFUSED, "Pickaxes?")
                    .addNPC(npc.id, HeadE.CALM, "Yes, I've started a business. It's not going very well so far, but the poison chalices are quite a nice loss-leader.")
                    .addNext {
                        ShopsHandler.openShop(player, "stankers_bronze_pickaxes")
                    }
                    .finish())
        }

        chaliceOps.add("What's a poison chalice?") {
            player.startConversation(Dialogue()
                .addPlayer(HeadE.CONFUSED, "What's a poison chalice?")
                .addNPC(npc.id, HeadE.CHEERFUL, "It's an exciting drink I've invented. I don't know what it tastes like, I haven't tried it myself.")
                .addOptions("Would you like a poison chalice?") { chaliceOps -> chaliceOptions(chaliceOps, npc) })
        }

        chaliceOps.add("No, thank you.") {
            player.startConversation(Dialogue()
                .addPlayer(HeadE.NONE, "No, thank you.")
                .addOptions("What would you like to say?") { initialOps -> initialOptions(initialOps, npc) })
        }
    }
}