package com.rs.game.content.miniquests.lost_her_marbles

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills

class DarrenLostHerMarbles(val p: Player, val npc: NPC) {
    init {
        p.startConversation {
            val stage = p.getMiniquestStage(Miniquest.LOST_HER_MARBLES)
            val rewardClaimed = p.miniquestManager.getAttribs(Miniquest.LOST_HER_MARBLES).getI("rewardClaimed")
            val fragments = getTotalFragments(p)

            when (stage) {
                0 -> {
                    npc(
                        npc,
                        CALM_TALK,
                        "Ah, ${p.displayName}! Can I borrow you for a moment? I've got some work in dire need of a hero and you're the best agent I have to take it on!"
                    )
                    player(CALM_TALK, "What needs doing, Guildmaster?")
                    npc(npc, CALM_TALK, "Have you heard of Drannai Silverborn?")
                    player(CALM_TALK, "No, I can't say I have.")
                    npc(
                        npc,
                        CALM_TALK,
                        "Drannai is, or rather, was a mage of some repute. Fond of fire, don't you know. Very fond. Anyway, what most people know about her is that she created a solitaire set with pieces made of entrapped and solidified elemental fire."
                    )
                    player(CALM_TALK, "Solidified fire?")
                    npc(
                        npc,
                        CALM_TALK,
                        "Very valuable stuff, it is; like the very heart of a pristine jewel, all over, and full of magical energy to them as know how to harness it. That's beside the point, though, which is that some days ago she got something very wrong."
                    )
                    player(CALM_TALK, "Wrong how?")
                    npc(
                        npc,
                        CALM_TALK,
                        "We can't ask her that; they've only found her hat so far. Seems the explosion she caused scattered her laboratory over half of Gielinor, including these fragments of flame."
                    )
                    player(CALM_TALK, "And you want them?")
                    npc(
                        npc,
                        CALM_TALK,
                        "By now I imagine people will have stumbled across them here and there; we need them purloined before they realise what they have. Found a buyer who wants octets of them, see."
                    )
                    player(CALM_TALK, "They could be anywhere!")
                    npc(
                        npc,
                        CALM_TALK,
                        "Yes, there may be a certain amount of luck involved. Check pockets wherever you can; if you find any we'll pay handsomely in further training and in pecuniary considerations."
                    )
                    player(CALM_TALK, "How many does this buyer want?")
                    npc(
                        npc,
                        CALM_TALK,
                        "Thirty-two. Shouldn't think any one group would have found more than six, though. Tell you what, have a word with Robin; he'll know where to start looking."
                    )
                    options {
                        op("Accept Quest.") {
                            player(CALM_TALK, "Will Do!")
                            npc(npc, HAPPY_TALKING, "Good on you.") {
                                p.miniquestManager.setStage(Miniquest.LOST_HER_MARBLES, 1)
                            }
                        }
                        op("Decline Quest.") {
                            player(CALM_TALK, "Maybe later.")
                            npc(npc, HAPPY_TALKING, "As you wish.")
                        }
                    }
                }

                in 1..4 -> {
                    if (rewardClaimed == stage) {
                        npc(
                            npc,
                            SHAKING_HEAD,
                            "You've not collected your reward for the last lot yet! Have a word with Robin, yes?"
                        )
                    }
                    else {
                        if (fragments < stage * 8) {
                            player(
                                CALM_TALK,
                                "I'd like to talk about the caper I'm doing for you. I've had some success tracking down flame fragments."
                            )
                            npc(npc, CALM_TALK, "You have another set of eight fragments for me?")
                            player(CALM_TALK, "No, not a whole set.")
                            npc(
                                npc,
                                SHAKING_HEAD,
                                "Oh, I'm sorry; I can only take them in groups of eight. Get out there and pick a pocket or two."
                            )
                        }
                        else {
                            if (countItemsInInventory(p) >= 8) {
                                player(CALM_TALK, "I'd like to talk about the caper I'm doing for you. I've had some success tracking down flame fragments.")
                                when (stage) {
                                    1 -> {
                                        player(CALM_TALK, "That's my eighth.")
                                        npc(npc, CALM_TALK, "Magnificent work, ${p.displayName}! I'll see you get the standard fee; and have a word with Robin for your cut of our recent proceeds - a small bonus for your efforts.") {
                                            claimRewards(p, stage)
                                        }
                                    }
                                    2 -> {
                                        player(CALM_TALK, "That's my sixteenth.")
                                        npc(npc, CALM_TALK, "Halfway there, ${p.displayName}! I'll see you get the standard fee; and have a word with Robin for your cut of our recent proceeds - a small bonus for your efforts.") {
                                            claimRewards(p, stage)
                                        }
                                    }
                                    3 -> {
                                        player(CALM_TALK, "That's my twenty-fourth.")
                                        npc(npc, CALM_TALK, "We've got most of them now, ${p.displayName}! I'll see you get the standard fee; and have a word with Robin for your cut of our recent proceeds - a small bonus for your efforts.") {
                                            claimRewards(p, stage)
                                        }
                                    }
                                    4 -> {
                                        player(CALM_TALK, "I believe that's the whole set.")
                                        npc(npc, CALM_TALK, "It certainly is! What an incredible feat that was. I'll make sure you get your bounty and your prize right away.")
                                        player(CALM_TALK, "Thank you.")
                                        npc(npc, CALM_TALK, "As a personal vote of thanks, I'd like to give you some advanced training in pickpocketing techniques.")
                                        player(CALM_TALK, "You're going to teach me yourself?")
                                        npc(npc, CALM_TALK, "Well...no. I'm going to get Robin to do it. Go and have a word with him.") {
                                            claimRewards(p, stage)
                                        }
                                    }
                                    else -> ""
                                }
                            } else {
                                player(
                                    CALM_TALK,
                                    "I'd like to talk about the caper I'm doing for you. I've had some success tracking down flame fragments."
                                )
                                npc(npc, CALM_TALK, "Splendid! Bring them to me and I'll see that you're rewarded.")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun claimRewards(player: Player, stage: Int) {
        player.sendMessage("You gain 1200 Thieving XP as bounty on your flame fragments.")
        removeItems(player)
        player.skills.addXp(Skills.THIEVING, 1200.00)
        player.miniquestManager.getAttribs(Miniquest.LOST_HER_MARBLES).setI("rewardClaimed", stage)
    }
}