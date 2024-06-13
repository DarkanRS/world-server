package com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.taverley

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class SanfewDEadgarsRuse(player: Player, npc: NPC) {
    init {
        player.startConversation {
            val stage = player.getQuestStage(Quest.EADGARS_RUSE)
            when (stage) {

                STAGE_UNSTARTED -> {
                    npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                    player(CALM_TALK, "Have you any more work for me, to help reclaim the circle?")
                    npc(npc, TALKING_ALOT, "Ah, you've come just in time. I need a certain herb for the next part of the purification ritual...")
                    npc(npc, CALM_TALK, "It used to be quite common, but nowadays only the trolls know where to find it. They use it in their cooking, you see. It's a very prized ingredient.")
                    player(CALM_TALK, "And what exactly do you want me to do?")
                    npc(npc, CALM_TALK, "Journey to the north into the land of the trolls, and find the secret of the herb the trolls call 'goutweed'.")
                    npc(npc, CALM_TALK, "My friend Eadgar lives in the area, and he may be able to help you. Bring some goutweed back and I will teach you something useful.")
                    questStart(Quest.EADGARS_RUSE)
                    player(CALM_TALK, "I'll do it.") { player.setQuestStage(Quest.EADGARS_RUSE, STAGE_SPEAK_TO_EADGAR) }
                    npc(npc, CALM_TALK, "Thank you, adventurer!")
                }

                in STAGE_SPEAK_TO_EADGAR..STAGE_GET_PARROT -> {
                    npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                    exec { defaultSanfewDialogue(player, npc) }
                }

                in STAGE_NEED_TO_HIDE_PARROT..STAGE_HIDDEN_PARROT -> {
                    npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                    player(CALM_TALK, "Eadgar says he needs some dirty clothes for his plan, and he said you might be able to help.")
                    npc(npc, CALM_TALK, "Now why would he need that?")
                    player(CALM_TALK, "It's a long story.")
                    npc(npc, CALM_TALK, "Never mind then...<br>I think Tegid is doing his laundry outside. You could ask him if you can borrow one of his dirty robes.")
                }

                in STAGE_NEED_TROLL_POTION..STAGE_DISCOVERED_KEY_LOCATION -> {
                    npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                    exec { defaultSanfewDialogue(player, npc) }
                }

                STAGE_UNLOCKED_STOREROOM -> {
                    npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                    if (!player.containsOneItem(GOUTWEED)) {
                        exec { defaultSanfewDialogue(player, npc) }
                    } else {
                        player(CALM_TALK, "I have some goutweed!")
                        npc(npc, CALM_TALK, "Excellent! I will be able to complete the next part of the ritual now. I will teach you a new spell and give you some of my knowledge in Herblore as a token of thanks.")
                        npc(npc, CALM_TALK, "If you ever come across more goutweed, bring it to me; I don't need any more for the ritual, but it's still quite difficult for me to get. I'll exchange it for some other herbs.")
                        exec {
                            player.inventory.deleteItem(GOUTWEED, 1)
                            player.completeQuest(Quest.EADGARS_RUSE)
                        }
                    }
                }

            }
        }
    }

    private fun defaultSanfewDialogue(player: Player, npc: NPC) {
        player.startConversation {
            player(CALM_TALK, "What was I meant to be doing again?")
            npc(npc, CALM_TALK, "I've told you already. Journey to the north into the land of the trolls, and find the secret of the herb the trolls call goutweed.")
            npc(npc, CALM_TALK, "My friend Eadgar lives in the area, and he may be able to help you. Bring some goutweed back and I will teach you something useful.")
            player(CALM_TALK, "I'll get on with it.")
        }
    }
}
