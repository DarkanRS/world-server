package com.rs.game.content.quests.biohazard.dialogue.npcs.east_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class KingLathasD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                STAGE_SPEAK_TO_KING -> {
                    player(SKEPTICAL_THINKING, "I assume that you are the King of East Ardougne?")
                    npc(npc, FRUSTRATED, "You assume correctly, but where do you get such impertinence.")
                    player(CALM_TALK, "I get it from finding out that the plague is a hoax.")
                    npc(npc, SHAKING_HEAD, "A hoax? I've never heard such a ridiculous thing...")
                    player(CALM_TALK, "I have evidence, from Guidor of Varrock.")
                    npc(npc, CALM_TALK, "Ah... I see. Well then you are right about the plague. But I did it for the good of my people.")
                    player(CONFUSED, "When is it ever good to lie to people like that?")
                    npc(npc, SAD, "When it protects them from a far greater danger, a fear too big to fathom.")
                    options {
                        op("I don't understand...") {
                            player(CONFUSED, "I don't understand...")
                            npc(npc, SAD, "Their King, Tyras, journeyed out to the West on a voyage of discovery. But he was captured by the Dark Lord.")
                            npc(npc, SAD, "The Dark Lord agreed to spare his life, but only on one condition... That he would drink from the Chalice of Eternity.")
                            player(SKEPTICAL_THINKING, "So what happened?")
                            npc(npc, SAD, "The chalice corrupted him. He joined forces with the Dark Lord, the embodiment of pure evil, banished all those years ago...")
                            npc(npc, SAD, "And so I erected this wall, not just to protect my people, but to protect all the people of Gielinor.")
                            npc(npc, SAD, "Now, with the King of West Ardougne, the Dark Lord has an ally on the inside.")
                            npc(npc, SAD, "So I'm sorry that I lied about the plague. I just hope that you can understand my reasons.")
                            player(CALM_TALK, "Well at least I know now, but what can we do about it?")
                            npc(npc, CALM_TALK, "Nothing at the moment, I'm waiting for my scouts to come back. They will tell us how we can get through the mountains.")
                            npc(npc, SKEPTICAL, "When this happens, can I count on your support?")
                            player(CALM_TALK, "Absolutely!")
                            npc(npc, HAPPY_TALKING, "Thank the gods! I give you permission to use my training area.")
                            npc(npc, CALM_TALK, "It's located just to the north west of Ardougne, there you can prepare for the challenge ahead.")
                            player(CALM_TALK, "Ok. There's just one thing I don't understand, how do you know so much about King Tyras?")
                            npc(npc, SAD, "How could I not do? He was my brother.")
                            exec { player.completeQuest(Quest.BIOHAZARD) }
                        }
                        op("Well I've wasted enough of my time here.") {
                            player(CALM_TALK, "Well I've wasted enough of my time here.")
                            npc(npc, CALM_TALK, "No time is ever wasted, thanks for all you've done.")
                        }
                    }
                }

            }
        }
    }
}
