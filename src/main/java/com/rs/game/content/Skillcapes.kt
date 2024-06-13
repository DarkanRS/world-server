package com.rs.game.content

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants

enum class Skillcapes(
    val untrimmed: Int,
    val trimmed: Int,
    val hood: Int,
    val master: Int,
    val verb: String
) {
    Attack(9747, 9748, 9749, 25324, "a master in the fine art of attacking"),
    Defence(9753, 9754, 9755, 25326, "a master in the fine art of defence"),
    Strength(9750, 9751, 9752, 25325, "as strong as is possible"),
    Constitution(9768, 9769, 9770, 25332, "as resilient as is possible"),
    Ranging(9756, 9757, 9758, 25327, "a master in the fine art of ranging"),
    Prayer(9759, 9760, 9761, 25328, "as devoted to prayer as possible"),
    Magic(9762, 9763, 9764, 25329, "as powerful a wizard as I"),
    Cooking(9801, 9802, 9803, 25344, "a master in the culinary arts"),
    Woodcutting(9807, 9808, 9809, 25346, "a master woodsman"),
    Fletching(9783, 9784, 9785, 25337, "a master in the fine art of fletching"),
    Fishing(9798, 9799, 9800, 25343, "a master fisherman"),
    Firemaking(9804, 9805, 9806, 25345, "a master of fire"),
    Crafting(9780, 9781, 9782, 25336, "a master craftsman"),
    Smithing(9795, 9796, 9797, 25342, "a master blacksmith"),
    Mining(9792, 9793, 9794, 25341, "a master miner"),
    Herblore(9774, 9775, 9776, 25334, "a master herbalist"),
    Agility(9771, 9772, 9773, 25333, "as agile as possible"),
    Thieving(9777, 9778, 9779, 25335, "a master thief"),
    Slayer(9786, 9787, 9788, 25338, "an incredible student"),
    Farming(9810, 9811, 9812, 25347, "a master farmer"),
    Runecrafting(9765, 9766, 9767, 25330, "a master runecrafter"),
    Hunter(9948, 9949, 9950, 25339, "a master hunter"),
    Construction(9789, 9790, 9791, 25331, "a master home builder"),
    Summoning(12169, 12170, 12171, 25348, "a master summoner"),
    Dungeoneering(18508, 18509, 18510, 19709, "a master dungeon delver");

    private fun getGiveCapeDialogue(player: Player, npcId: Int, masterCape: Boolean, dialogue: DialogueBuilder) {
        if (!player.inventory.hasCoins(if (masterCape) 120000 else 99000)) {
                dialogue.player(SAD_MILD, "But, unfortunately, I was mistaken.")
                dialogue.npc(npcId, NO_EXPRESSION, "Well, come back and see me when you do.")
            return
        }
        player.startConversation {
            npc(npcId, CHEERFUL, if (ordinal == Constants.FIREMAKING) "I'm sure you'll look hot in that cape." else "Excellent! Wear that cape with pride my friend.") {
                player.inventory.removeCoins(if (masterCape) 120000 else 99000)
                if (!masterCape) player.inventory.addItem(hood, 1)
                player.inventory.addItem(if (masterCape) master else if (player.skills.checkMulti99s()) trimmed else untrimmed, 1)
            }
        }
    }

    fun getOffer99CapeDialogue(player: Player, npcId: Int) {
        player.startConversation {
            npc(npcId, CHEERFUL, "Ah, this is a Skillcape of ${name}. I have mastered the art of ${name.lowercase()} and wear it proudly to show others.")
            player(SKEPTICAL, "Hmm, interesting.")
            if (player.skills.getLevelForXp(ordinal) >= 99) {
                npc(npcId, NO_EXPRESSION, "Ah, but I see you are already $verb, perhaps you have come to me to purchase a Skillcape of $name, and thus join the elite few who have mastered this exacting skill?")
                options {
                    op("Yes, I'd like to buy one please.") {
                        npc(npcId, NO_EXPRESSION, "Most certainly; unfortunately being such a prestigious item, they are appropriately expensive. I'm afraid I must ask you for 99,000 gold.")
                        options {
                            op("99,000 coins? That's much too expensive.") {
                                player(HAPPY_TALKING, "99,000 coins? That's much too expensive.")
                                npc(npcId, CALM_TALK, "...")
                            }
                            op("I think I have the money right here, actually.") {
                                player(HAPPY_TALKING, "I think I have the money right here, actually.")
                                exec { getGiveCapeDialogue(player, npcId, false, this) }
                            }
                        }
                    }
                    if (player.skills.is120(this@Skillcapes.ordinal)) {
                        op("I've mastered this skill. Is there anything else?") {
                            npc(npcId, AMAZED, "I've been saving this master cape for someone truly $verb. Is that really you?")
                            player(CONFUSED, "I think so. I mean I really have mastered everything there is to know.")
                            npc(npcId, AMAZED, "I can see that! I would be glad to offer you this cape.")
                            options("Buy a master cape for 120,000 coins?") {
                                op("Yes, please.") {
                                    player(HAPPY_TALKING, "Yes, please. I think I have the money right here, actually.") { getGiveCapeDialogue(player, npcId, true, this) }
                                }
                                op("No, thanks.")
                            }
                        }
                    }
                    op("Nevermind.") {
                        npc(npcId, NO_EXPRESSION, "No problem; there are many other adventurers who would love the opportunity to purchase such a prestigious item! You can find me here if you change your mind.")
                    }
                }
            } else {
                options {
                    op("Please tell me more about skillcapes.") {
                        player(HAPPY_TALKING, "Please tell me more about skillcapes.")
                        npc(npcId, CALM_TALK, "Of course. Skillcapes are a symbol of achievement. Only people who have mastered a skill and reached level 99 can get their hands on them and gain the benefits they carry.")
                    }
                    op("Bye.") {
                        player(HAPPY_TALKING, "Bye.")
                        npc(npcId, CALM_TALK, "Bye.")
                    }
                }
            }
        }
    }
}
