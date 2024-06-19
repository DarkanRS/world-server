package com.rs.game.content.quests.plague_city.dialogues.objects

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plague_city.utils.*
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject

class RehnisonDoorD (player: Player, obj: GameObject) {
    init {
        when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

            in STAGE_UNSTARTED..STAGE_SPOKEN_TO_JETHICK -> {
                player.startConversation {
                    npc(TED_REHNISON, FRUSTRATED, "Go away. We don't want any.")
                    if (player.inventory.containsOneItem(BOOK_TURNIP_GROWING_FOR_BEGINNERS)) {
                        player(CALM_TALK, "I'm a friend of Jethick's, I have come to return a book he borrowed.")
                        npc(TED_REHNISON, HAPPY_TALKING, "Oh... Why didn't you say, come in then.")
                        item(BOOK_TURNIP_GROWING_FOR_BEGINNERS, "You hand the book to Ted as you enter.") {
                            Doors.handleDoor(player, obj)
                            player.inventory.deleteItem(BOOK_TURNIP_GROWING_FOR_BEGINNERS, 1)
                            player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_GAVE_BOOK_TO_TED)
                        }
                        npc(TED_REHNISON, HAPPY_TALKING, "Thanks, I've been missing that.")
                    }
                }
            }

            else -> { Doors.handleDoor(player, obj) }

        }
    }
}
