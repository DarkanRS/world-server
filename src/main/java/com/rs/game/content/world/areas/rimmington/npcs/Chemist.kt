package com.rs.game.content.world.areas.rimmington.npcs

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.achievements.AchievementSystemD
import com.rs.game.content.achievements.SetReward
import com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington.ChemistD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Chemist(player: Player, npc: NPC) {
    init {
        player.startConversation {
            options {
                op("Ask about lamp oil.") { mapLampOilDialogue(npc, this) }
                op("Ask about impling jars.") { mapImplingJarsDialogue(player, npc, this) }
                if (player.questManager.getStage(Quest.BIOHAZARD) in STAGE_RECEIVED_VIALS..STAGE_RECEIVED_TOUCH_PAPER)
                    op("Ask about Biohazard.") { ChemistD(player, npc, this) }
                op("About the Task System...") { exec { AchievementSystemD(player, npc.id, SetReward.FALADOR_SHIELD) } }
            }
        }
    }
}

fun mapLampOilDialogue(npc: NPC, dialogue: DialogueBuilder) {
    dialogue.player(CALM_TALK, "Hi, I need fuel for a lamp.")
    dialogue.npc(npc, CALM_TALK, "Hello there, the fuel you need is lamp oil, do you need help making it?")
    dialogue.options {
        op("Yes please.") {
            player(CALM_TALK, "Yes please.")
            npc(npc, CALM_TALK, "It's really quite simple. all set up, so there's no fiddling around with dials... Just put ordinary swamp tar in, and then use a lantern or lamp to get the oil out.")
            player(CALM_TALK, "Thanks.");
        }
        op("No thanks.") {
            player(CALM_TALK, "No thanks.")
        }
    }
}

fun mapImplingJarsDialogue(player: Player, npc: NPC, dialogue: DialogueBuilder) {

    if (player.inventory.containsItem(11262)) {
        dialogue.npc(npc, CALM_TALK, "My lamp oil still may be able to do what you want. Use the oil and flower mix on the still.")
        dialogue.npc(npc, CALM_TALK, "Once that's done, get one of those butterfly jars to collect the distillate.")
        dialogue.player(CALM_TALK, "Thanks!");
    } else if (player.inventory.containsItem(11264)) {
        dialogue.player(CALM_TALK, "Do you know how I might distil a mix of anchovy oil and flowers so that it forms a layer on the inside of a butterfly jar?")
        dialogue.npc(npc, CALM_TALK, "My lamp oil still may be able to do what you want. ${if (player.inventory.containsOneItem(6010, 6012, 6014, 2460, 2462, 2466, 2468, 2470, 2472, 2474, 2474, 2476)) "You'll need to mix your flowers to the anchovy oil first. Then use the oil and flower mix on the still." else "You'll need to get flowers first, then mix them to your anchovy oil. Then use the oil and flower mix on the still."}")
        dialogue.npc(npc, CALM_TALK, "Once that's done, ${if (player.inventory.containsOneItem(10012)) "use" else "get" } one of those butterfly jars to collect the distillate.")
        dialogue.player(CALM_TALK, "Thanks!");
    } else {
        dialogue.player(CALM_TALK, "I have a slightly odd question.")
        dialogue.npc(npc, CALM_TALK, "Jolly good, the odder the better. I like oddities.")
        dialogue.player(CALM_TALK, "Do you know how I might distil a mix of anchovy oil and flowers so that it forms a layer on the inside of a butterfly jar?")
        dialogue.npc(npc, CALM_TALK, "That is an odd question. I commend you for it. Why would you want to do that?")
        dialogue.player(CALM_TALK, "Apparently, if I can make a jar like this it will be useful for storing implings in.")
        dialogue.npc(npc, CALM_TALK, "So, do you have any of this fish-and-flower flavoured oil and a butterfly jar then?")
        dialogue.player(CALM_TALK, "Actually, no.")
        dialogue.npc(npc, CALM_TALK, "If you go and get them then I may be able to help you. I'm better at coming up with answers if the questions are in my hands.")
        dialogue.label("anythingElse?")
        dialogue.npc(npc, CALM_TALK, "Is there anything else you want to ask?")
        dialogue.options {
            op("So how do you make anchovy oil?") {
                player(CALM_TALK, "How do you make anchovy oil?")
                npc(npc, CALM_TALK, "Anchovies are pretty oily fish. I'd have thought you could just grind them up using a pestle and mortar and sieve out the bits.")
                npc(npc, CALM_TALK, "You'd probably want to remove any water first - Cooking should do that pretty well. I reckon you'll need to sieve 8 lots of anchovies paste to get one vial of anchovy oil.")
                goto("anythingElse?")
            }
            op("Do you have a sieve I can use?") {
                player(CALM_TALK, "Do you have a sieve I can use?")
                if (!player.inventory.containsOneItem(6097)) {
                    npc(npc, CALM_TALK, "Errm, yes. Here, have this one. It's only been used for sieving dead rats out of sewer water.")
                    player(CALM_TALK, "Err, why? Actually, on second thoughts I don't want to know.")
                    npc(npc, CALM_TALK, "Well, it should be ideally suited to your task.")
                    if (player.inventory.hasFreeSlots()) {
                        player(CALM_TALK, "Fair enough.") { player.inventory.addItemDrop(6097, 1) }
                    } else {
                        npc(npc, CALM_TALK, "I can't give you a sieve if you have nowhere to put it! Come back when you have.")
                        player(CALM_TALK, "Fair enough.")
                    }
                } else {
                    npc(npc, CALM_TALK, "Errm, yes. But you already have one. Two sieves is a bit excessive, don't you think?")
                }
            }
            op("I'd better go and get the ingredients.") {
                player(CALM_TALK, "I'd better go and get the ingredients.")
                npc(npc, CALM_TALK, "I think so.")
            }
        }
    }
}

@ServerStartupEvent
fun mapChemist() {
    onNpcClick(367, options = arrayOf("Talk-to")) { (player, npc) -> Chemist(player, npc) }
}
