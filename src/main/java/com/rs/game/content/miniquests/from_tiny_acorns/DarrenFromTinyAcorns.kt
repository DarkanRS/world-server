package com.rs.game.content.miniquests.from_tiny_acorns

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Tile

class DarrenFromTinyAcorns(p: Player, npc: NPC) {
    init {
        p.startConversation {
            when(p.getMiniquestStage(Miniquest.FROM_TINY_ACORNS)) {
                0 -> {
                    npc(npc, HAPPY_TALKING, "Ah, ${p.displayName}! Can I borrow you for a moment?")
                    npc(npc, HAPPY_TALKING, "I've got some work in dire need of a hero and you're the best agent I have to take it on!")
                    npc(npc, HAPPY_TALKING, "I'll put it to you very directly, ${p.displayName}. It's an expensive business expanding the guild.")
                    npc(npc, HAPPY_TALKING, "The price of construction work is exorbitant, and if we're to get the premises up to the size of our eventual status deserves we shall be needing more money.")
                    player(CALM_TALK, "I understand well.")
                    npc(npc, HAPPY_TALKING, "That's why I've bought a very expensive toy dragon, which is quite nearly completed.")
                    player(CALM_TALK, "Wait, what?")
                    npc(npc, HAPPY_TALKING, "Allow me to explain... There is a master craftsman recently arrived in Varrock, a dwarf by the name of Urist Loric.")
                    npc(npc, HAPPY_TALKING, "He does clockwork and delicate crafts, and works extensively in precious stones.")
                    npc(npc, HAPPY_TALKING, "I have commissioned him to construct a red dragon - worth the entirety of our available monies - out of ruby.")
                    player(SKEPTICAL_HEAD_SHAKE, "This sounds more like madness than adventure at the moment.")
                    npc(npc, HAPPY_TALKING, "I have no intention of buying it. What I need you to do, my dear " + p.getPronoun("fellow", "lady") + ", is steal the dragon from his stall in Varrock.")
                    npc(npc, HAPPY_TALKING, "You can then be very surprised and dismayed at him and demand my money returned.")
                    player(CALM_TALK, "Then I bring you the toy and your money, and you fence the toy and double your investment?")
                    npc(npc, HAPPY_TALKING, "Double? Ha! I'm having a bad day.")
                    options {
                        op("Alright, I'll do it.") {
                            player(CALM_TALK, "Alright, I'll do it.")
                            npc(npc, HAPPY_TALKING, "I knew I could count on you.") { p.miniquestManager.setStage(Miniquest.FROM_TINY_ACORNS, 1) }
                        }
                        op("Let me think about it and come back.") {
                            player(CALM_TALK, "Let me think about it and come back.")
                            npc(npc, HAPPY_TALKING, "Don't be too long; if he finishes the thing I'll have to take delivery of it.")
                        }
                    }
                }
                1 -> {
                    player(CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                    player(CALM_TALK, "I've not got the baby toy dragon and initial investment back yet, I'm afraid.")
                    npc(npc, HAPPY_TALKING, "Well, do hop to it, there's a good " + p.getPronoun("chap","lass") + ". Time's a ticking!")
                    player(CALM_TALK, "Do you have any practical advice for how I should go about this?")
                    npc(npc, HAPPY_TALKING, "Practical? My word, no. Robin's your fellow if you want practical matters attended to; I mostly do strategy.")
                }
                2 -> {
                    player(CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                    player(CALM_TALK, "I've got the baby toy dragon but I don't have the initial investment back yet, I'm afraid.")
                    npc(npc, HAPPY_TALKING, "Excellent! Well, do hop to it, there's a good " + p.getPronoun("chap","lass") + ". Time's a ticking!")
                }
                3 -> {
                    player(CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                    exec {
                        val dragonID = 18651
                        val noteID = 18652
                        if (!p.inventory.containsItem(dragonID) || !p.inventory.containsItem(noteID)) {
                            player(SHAKING_HEAD, "I don't have everything I need yet.")
                            npc(npc, TALKING_ALOT, "As a reminder, I need you to bring me the baby toy dragon and my initial investment.")
                            return@exec
                        }
                        if (p.bank.containsItem(dragonID, 1) || p.bank.containsItem(noteID, 1)) {
                            player(SHAKING_HEAD, "I have the goods! I'll just need a moment to visit the bank before I hand them over.")
                            return@exec
                        }
                        npc(npc, HAPPY_TALKING, "I knew you wouldn't let me down! Fairly simple caper, was it?")
                        player(CALM_TALK, "Just needed a little finesse, that's all.")
                        npc(npc, HAPPY_TALKING, "Ah, finesse! Very well done indeed. Some day I hope to show you myself in action; for now, however, I shall be rather busy paying the builders.")
                        exec {
                            p.fadeScreen {
                                p.inventory.deleteItem(18651, 1)
                                p.inventory.deleteItem(18652, 1)
                                p.skills.addXp(Skills.THIEVING, 1000.0)
                                p.tele(Tile.of(3223, 3269, 0))
                                p.miniquestManager.complete(Miniquest.FROM_TINY_ACORNS)
                            }
                        }
                    }
                }
            }
        }
    }
}
