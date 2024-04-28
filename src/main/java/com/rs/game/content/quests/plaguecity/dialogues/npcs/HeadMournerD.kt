package com.rs.game.content.quests.plaguecity.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class HeadMournerD (player: Player, npc: NPC) {
    init {
        var hasAttemptedPrisonHouse = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ATTEMPTED_PRISON_HOUSE_DOORS)
        player.startConversation {
            npc(npc, CONFUSED, "How did you get into West Ardougne? Ah well you'll have to stay, can't risk you spreading the plague outside.")
            label("initialOps")
            options {
                if (hasAttemptedPrisonHouse && player.questManager.getStage(Quest.PLAGUE_CITY) < STAGE_GAVE_HANGOVER_CURE) {
                    op("I need clearance to enter a plague house.") {
                        player(CALM_TALK, "I need clearance to enter a plague house. It's in the southeast corner of West Ardougne.")
                        npc(npc, LAUGH, "You must be nuts, absolutely not!")
                        options {
                            op("There's a kidnap victim inside!") {
                                player(CALM_TALK, "There's a kidnap victim inside!")
                                npc(npc, FRUSTRATED, "Well they're as good as dead then, no point in trying to save them.")
                                goto("initialOps")
                            }
                            op("I've got a gasmask though...") {
                                player(CALM_TALK, "I've got a gasmask though...")
                                npc(npc, FRUSTRATED, "It's not regulation. Anyway you're not properly trained to deal with the plague.")
                                player(CONFUSED, "How do I get trained?")
                                npc(npc, FRUSTRATED, "It requires a strict 18 months of training.")
                                player(SAD, "I don't have that sort of time.")
                                goto("initialOps")
                            }
                            op("Yes, I'm utterly crazy.") {
                                player(CALM_TALK, "Yes, I'm utterly crazy.")
                                npc(npc, FRUSTRATED, "You're wasting my time, I have a lot of work to do!")
                                goto("initialOps")
                            }
                        }
                    }
                }
                op("So what's a mourner?") {
                    player(CONFUSED, "So what's a mourner?")
                    npc(npc, CALM_TALK, "We're working for King Lathas of East Ardougne trying to contain the accursed plague sweeping West Ardougne. We also do our best to ease these people's suffering.")
                    npc(npc, CALM_TALK, "We're nicknamed mourners because we spend a lot of time at plague victim funerals, no-one else is allowed to risk the funerals. It's a demanding job, and we get little thanks from the people here.")
                }
                op("I haven't got the plague though...") {
                    player(SKEPTICAL_HEAD_SHAKE, "I haven't got the plague though.")
                    npc(npc, CALM_TALK, "Can't risk you being a carrier. That protective clothing you have isn't regulation issue. It won't meet safety standards.")
                }
                if (player.questManager.getStage(Quest.PLAGUE_CITY) < STAGE_GAVE_HANGOVER_CURE)
                    op("I'm looking for a woman named Elena.") {
                        player(CALM_TALK, "I'm looking for a woman named Elena.")
                        npc(npc, CALM_TALK, "Ah yes, I've heard of her. A missionary I believe. She must be mad coming over here voluntarily. I hear rumours she has probably caught the plague now. Very tragic, a stupid waste of life.")
                    }
            }

        }
    }
}
