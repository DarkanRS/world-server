package com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.troll_stronghold

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class BurntmeatD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.getQuestStage(Quest.EADGARS_RUSE)) {

                STAGE_SPEAK_TO_BURNTMEAT -> {
                    player(CALM_TALK, "Er, hi.")
                    npc(npc, T_HAPPY_TALK, "Hmm? What human do in troll kitchen? Burntmeat tired of cooking goats. Human look tasty.")
                    player(WORRIED, "Oh, you don't want to eat me! I'm a tough, hardened adventurer, not tender or tasty at all!")
                    npc(npc, T_HAPPY_TALK, "Hmm. Burntmeat think you probably right. What human doing here?")
                    player(CALM_TALK, "I'm on a quest to find some goutweed.")
                    npc(npc, T_LAUGH, "Bwahahaha! Burntmeat not give his greatest cooking secret away so easily.")
                    npc(npc, T_CALM_TALK, "But Burntmeat also has quest for human!")
                    player(SKEPTICAL_THINKING, "Really? What is it?")
                    npc(npc, T_CALM_TALK, "Bring back a tasty human for Burntmeat's stew. If you find tasty human, Burntmeat will give you good reward.")
                    player(CALM_TALK, "Right. I'll just...go fetch that for you then. Bye!") { player.setQuestStage(Quest.EADGARS_RUSE, STAGE_BRING_HUMAN) }
                }

                in STAGE_BRING_HUMAN..STAGE_RETRIEVED_PARROT -> {
                    npc(npc, T_CALM_TALK, "Did you find tasty human?")
                    player(CALM_TALK, "Erm, not yet, but I'm working on it...")
                }

                STAGE_RECEIVED_FAKE_MAN -> {
                    if (player.containsOneItem(FAKE_MAN_ITEM)) {
                    npc(npc, T_HAPPY_TALK, "Did you find tasty human? Burntmeat smell something good.")
                    player(HAPPY_TALKING, "Yes! Look!")
                    npc(FAKE_MAN_NPC, NONE, "Heeeeeeelp!")
                    npc(npc, T_HAPPY_TALK, "Ah, dat look like nice tasty human.")
                    npc(FAKE_MAN_NPC, NONE, "Aaaargh! Somebody save me!")
                    npc(npc, T_HAPPY_TALK, "Yep, sound like human too. Burntmeat put it in stew. Good work, human. Burntmeat give precious reward.")
                    player(CONFUSED, "This is burnt meat.") {
                        player.inventory.replace(FAKE_MAN_ITEM, BURNT_MEAT)
                        player.setQuestStage(Quest.EADGARS_RUSE, STAGE_GAVE_FAKE_MAN_TO_BURNTMEAT)
                    }
                    npc(npc, T_HAPPY_TALK, "It first thing I ever try to cook! Very precious to Burntmeat.")
                    player(CALM_TALK, "Thank you... and how's the stew?")
                    exec { burntmeatStewDialogue(player, npc) }
                    } else {
                        npc(npc, T_CALM_TALK, "Did you find tasty human?")
                        player(CALM_TALK, "Erm, not yet, but I'm working on it...")
                    }
                }

                in STAGE_GAVE_FAKE_MAN_TO_BURNTMEAT..STAGE_UNLOCKED_STOREROOM -> {
                    player(CALM_TALK, "How's the stew?")
                    exec { burntmeatStewDialogue(player, npc) }
                }

            }
        }
    }

    private fun burntmeatStewDialogue(player: Player, npc: NPC) {
        player.startConversation {
            npc(npc, T_HAPPY_TALK, "Slurp, mmm... Human stew cheer Burntmeat up!")
            label("initialOps")
            options {
                op("So, where can I get some goutweed?") {
                    player(SKEPTICAL_THINKING, "So, where can I get some goutweed?")
                    npc(npc, T_LAUGH, "Hah! Trolls pick it all until none left, many years ago. Only remaining stock in storeroom.")
                    npc(npc, T_HAPPY_TALK, "It well guarded, and Burntmeat hide key in fake bottom of kitchen drawer. Nobody find it there.") {
                        if (player.getQuestStage(Quest.EADGARS_RUSE) == STAGE_GAVE_FAKE_MAN_TO_BURNTMEAT) player.setQuestStage(Quest.EADGARS_RUSE, STAGE_DISCOVERED_KEY_LOCATION)
                    }
                    player(TALKING_ALOT, "That's some well-guarded secret alright. I'll just be off on my way now.")
                }
                op("I'll be going now.") {
                    player(CALM_TALK, "I'll be going now.")
                    npc(npc, T_CALM_TALK, "Bye, bye.")
                }
            }
        }
    }
}
