package com.rs.game.content.achievements

import com.rs.cache.loaders.ItemDefinitions
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.player.Player
import com.rs.lib.util.Utils

class AchievementSystemD(p: Player, npcId: Int, reward: SetReward) {
    init {
        p.startConversation {
            label("initialOptions")
            options("What would you like to say?") {
                op("Tell me about the Achievement System.") {
                    player(HeadE.CHEERFUL, "Tell me about the Achievement System.")
                    npc(npcId, HeadE.CHEERFUL_EXPOSITION, "Very well: the Achievement System is a collection of deeds you may wish to complete while adventuring around the world.")
                    npc(npcId, HeadE.CHEERFUL_EXPOSITION, "You can earn special rewards for completing certain achievements; at the very least, each is worth a cash bounty from Explorer Jack in Lumbridge.")
                    npc(npcId, HeadE.CHEERFUL_EXPOSITION, "Some also give items that will help complete other achievements, any many count as progress towards the set for the area they're in.")
                    goto("initialOptions")
                }
                op("Am I eligible for any rewards?") {
                    if (reward.hasRequirements(p, reward.itemIds[0], false)) {
                        options("Which item would you like to claim?") {
                            for (itemId in reward.itemIds) {
                                op(ItemDefinitions.getDefs(itemId).name) {
                                    if (reward.hasRequirements(p, itemId, false)) {
                                        player(HeadE.CONFUSED, "Could I claim ${Utils.addArticle(ItemDefinitions.getDefs(itemId).name)}?")
                                        npc(npcId, HeadE.CHEERFUL, "Of course, you've earned it!")
                                        item(itemId, "You've been handed ${Utils.addArticle(ItemDefinitions.getDefs(itemId).name)}.") {
                                            p.inventory.addItem(itemId)
                                        }
                                    } else {
                                        options {
                                            op("What requirements do I need?") {
                                                if (!reward.hasRequirements(p, itemId)) {
                                                    npc(npcId, HeadE.SAD, "You do not yet meet the requirements for ${Utils.addArticle(ItemDefinitions.getDefs(itemId).name)}. They have been listed in your chat box.")
                                                }
                                            }
                                        }
                                    }
                                    options {
                                        op("Farewell.")
                                    }
                                }
                            }
                        }
                    } else {
                        npc(npcId, HeadE.SHAKING_HEAD, "Unfortunately not. The requirements for claiming the first tier will be listed in your chat box.") { reward.hasRequirements(p, reward.itemIds[0]) }
                    }
                }
                op("Sorry, I was just leaving.") { player(HeadE.CALM, "Sorry, I was just leaving.") }
            }
        }
    }
}
