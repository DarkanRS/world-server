package com.rs.game.content.world.areas.taverley.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.druidic_ritual.dialogues.npcs.SanfewD
import com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.taverley.SanfewDEadgarsRuse
import com.rs.game.content.quests.eadgars_ruse.utils.GOUTWEED
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.DropSets
import com.rs.utils.drop.DropTable

class Sanfew(player: Player, npc: NPC) {
    init {
        val meetsEadgarsRuseReqs = player.skills.getLevel(Skills.HERBLORE) >= 31 && player.isQuestComplete(Quest.DRUIDIC_RITUAL) && player.isQuestComplete(Quest.DEATH_PLATEAU) && player.isQuestComplete(Quest.TROLL_STRONGHOLD)

        if (player.isQuestStarted(Quest.DRUIDIC_RITUAL) && !player.isQuestComplete(Quest.DRUIDIC_RITUAL)) {
            SanfewD(player, npc)
        } else if (meetsEadgarsRuseReqs && !player.isQuestComplete(Quest.EADGARS_RUSE)) {
            SanfewDEadgarsRuse(player, npc)
        } else if (player.isQuestComplete(Quest.EADGARS_RUSE)) {
            player.startConversation {
                npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                label("initialOps")
                options {
                    if (!player.inventory.containsOneItem(GOUTWEED)) {
                        op("Did you say you needed more goutweed?") {
                            player(SKEPTICAL_THINKING, "Did you say you needed more goutweed?")
                            npc(npc, SHAKING_HEAD, "I don't need any more goutweed for the ritual, but it's still quite difficult for me to get.")
                            npc(npc, HAPPY_TALKING, "If you ever come across some, bring it to me; I'll exchange it for some other herbs.")
                            goto("initialOps")
                        }
                    } else {
                        op("I have some more goutweed for you.") {
                            player(HAPPY_TALKING, "I have some more goutweed for you.")
                            npc(npc, HAPPY_TALKING, "Ah, good. Here are some herbs in exchange.") {
                                val dropSet = DropSets.getDropSet("goutweed_herbs")
                                val amountOfGoutweed = player.inventory.getAmountOf(GOUTWEED)
                                player.inventory.deleteItem(GOUTWEED, amountOfGoutweed)
                                repeat(amountOfGoutweed) {
                                    val randomDrop = DropTable.calculateDrops(player, dropSet).first()
                                    player.inventory.addItem(randomDrop.id, 1)
                                }
                            }
                        }
                    }
                    op("Have you any more work for me, to help reclaim the circle?") {
                        player(SKEPTICAL_THINKING, "Have you any more work for me to help reclaim the stone circle?")
                        npc(npc, SHAKING_HEAD, "Well, not right now I don't think young 'un. In fact, I need to make some preparations myself for the ritual.")
                        npc(npc, CALM_TALK, "Rest assured, if I need any more help I will ask you again.")
                        goto("initialOps")
                    }
                    op("Actually, I don't need to speak to you.") {
                        player(CALM_TALK, "Actually, I don't need to speak to you.")
                        npc(npc, CALM_TALK, "Well, we all make mistakes sometimes.")
                    }
                }
            }
        } else {
            player.startConversation {
                npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                player(CALM_TALK, "Nothing at the moment.")
            }
        }
    }
}

@ServerStartupEvent
fun mapSanfew() {
    onNpcClick(454, options = arrayOf("Talk-to")) { (player, npc) -> Sanfew(player, npc) }
}
