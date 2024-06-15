package com.rs.game.content.quests.plague_city.dialogues.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plague_city.utils.PlagueCityUtils
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class NonCombatMournersD (player: Player, npc: NPC) {
    init {

        if (PlagueCityUtils().isInWestArdougne(npc.tile)) {
            player.startConversation {
                npc(npc, CONFUSED, "Hmmm, how did you get over here? You're not one of this rabble. Ah well, you'll have to stay. Can't risk you going back now.")
                options {
                    op("So what's a mourner?") {
                        player(CONFUSED, "So what's a mourner?")
                        npc(npc, CALM_TALK, "We're working for King Lathas of East Ardougne trying to contain the accursed plague sweeping West Ardougne. We also do our best to ease these people's suffering.")
                        npc(npc, CALM_TALK, "We're nicknamed mourners because we spend a lot of time at plague victim funerals, no-one else is allowed to risk the funerals. It's a demanding job, and we get little thanks from the people here.")
                    }
                    op("I haven't got the plague though...") {
                        player(CONFUSED, "I haven't got the plague though...")
                        npc(npc, CALM_TALK, "Can't risk you being a carrier. That protective clothing you have isn't regulation issue. It won't meet safety standards.")
                    }
                    if (!player.questManager.isComplete(Quest.PLAGUE_CITY)) {
                        op("I'm looking for a woman named Elena.") {
                            player(CONFUSED, "I'm looking for a woman named Elena.")
                            npc(npc, CALM_TALK, "Ah yes, I've heard of her. A missionary I believe. She must be mad coming over here voluntarily. I hear rumors she has probably caught the plague now. Very tragic, a stupid waste of life.")
                        }
                    }
                }
            }
        } else {
            val randomDialogue = (1..4).random()
            when (randomDialogue) {
                1 -> dialogue1(player, npc)
                2 -> dialogue2(player, npc)
                3 -> dialogue3(player, npc)
                4 -> dialogue4(player, npc)
                else -> dialogue1(player, npc)
            }
        }
    }

    private fun dialogue1(player: Player, npc: NPC) {
        player.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, SKEPTICAL, "Can I help you?")
            player(CALM_TALK, "What are you doing?")
            npc(npc, SKEPTICAL, "I'm guarding the border to West Ardougne. No-one except we mourners can pass through.")
            player(CALM_TALK, "Why?")
            npc(npc, SKEPTICAL, "The plague of course. We can't risk cross contamination.")
            player(CALM_TALK, "Ok then, see you around.")
            npc(npc, SKEPTICAL, "Maybe...")
        }
    }

    private fun dialogue2(player: Player, npc: NPC) {
        player.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, SKEPTICAL, "Can I help you?")
            player(CALM_TALK, "Just being polite.")
            npc(npc, SKEPTICAL, "I'm not here to chat.")
            player(CALM_TALK, "Sorry, what is it you do?")
            npc(npc, SKEPTICAL, "I protect people like you from the plague.")
            player(CALM_TALK, "How?")
            npc(npc, SKEPTICAL, "By making sure no-one crosses the wall.")
            player(CALM_TALK, "What if they do?")
            npc(npc, SKEPTICAL, "Then they must be treated immediately.")
            player(CALM_TALK, "Treated?")
            npc(npc, SKEPTICAL, "Any West Ardougnians which cross the wall must be detained and disposed of safely.")
            player(CALM_TALK, "Sounds like nasty work.")
            npc(npc, SKEPTICAL, "Some find it hard, personally I quite enjoy it.")
            player(CALM_TALK, "You're a very sick man!")
            npc(npc, SKEPTICAL, "What? I'm pretty sure I haven't caught the plague yet.")
        }
    }

    private fun dialogue3(player: Player, npc: NPC) {
        player.startConversation {
            player(CALM_TALK, "Hi.")
            npc(npc, SKEPTICAL, "What are you up to?")
            player(CALM_TALK, "Just sight-seeing.")
            npc(npc, SKEPTICAL, "This is no place for sight-seeing. Don't you know there's been a plague outbreak?")
            player(CALM_TALK, "Yes, I had heard.")
            npc(npc, SKEPTICAL, "Then I suggest you leave as soon as you can.")
            label("initialOps")
            options {
                op("What brought the plague to Ardougne?") {
                    player(CONFUSED, "What brought the plague to Ardougne?")
                    npc(npc, SKEPTICAL, "It's all down to King Tyras of West Ardougne. Rather than protecting his people he spends his time in the lands to the West. When he returned last he brought the plague with him then left before the problem became serious.")
                    player(SKEPTICAL_THINKING, "Does he know how bad the situation is now?")
                    npc(npc, SKEPTICAL, "If he did he wouldn't care. I believe he wants his people to suffer, he's an evil man.")
                    player(CONFUSED, "Isn't that treason?")
                    npc(npc, SKEPTICAL, "He's not my king.")
                    goto("initialOps")
                }
                op("What are the symptoms of the plague?") {
                    player(WORRIED, "What are the symptoms of the plague?")
                    npc(npc, SKEPTICAL, "The first signs are typical flu symptoms. These tend to be followed by severe nightmares, horrifying hallucinations which drive many to madness.")
                    player(WORRIED, "Sounds nasty.")
                    npc(npc, SKEPTICAL, "It gets worse. Next the victim's blood changes into a thick black tar-like liquid, at this point they're past help. Their skin is cold to the touch, the victim is now brain dead.")
                    npc(npc, SKEPTICAL, "Their body however lives on driven by the virus, roaming like a zombie, spreading itself further wherever possible.")
                    player(WORRIED, "I think I've heard enough.")
                    goto("initialOps")
                }
                op("Thanks for the advice.") { player(CALM_TALK, "Thanks for the advice.") }
            }
        }
    }

    private fun dialogue4(player: Player, npc: NPC) {
        player.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, SKEPTICAL, "Do you have a problem traveller?")
            player(CALM_TALK, "No, I just wondered why you're wearing that outfit... Is it fancy dress?")
            npc(npc, SKEPTICAL, "No! It's for protection.")
            player(CALM_TALK, "Protection from what?")
            npc(npc, SKEPTICAL, "The plague of course...")
        }
    }

}
