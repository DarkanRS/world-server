package com.rs.game.content.quests.death_plateau.objects

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile

class SabbotCaveEnter(player: Player) {
    init {
        if (!player.questManager.getAttribs(Quest.DEATH_PLATEAU).getB(RECEIVED_SANDWICHES) && player.getQuestStage(Quest.DEATH_PLATEAU) == STAGE_MINED_TUNNEL) {
            player.cutscene {
                dialogue {
                    npc(SABBOT, CALM_TALK, "Hey. Take these.") {
                        player.inventory.addItemDrop(23065, 4)
                        player.questManager.getAttribs(Quest.DEATH_PLATEAU).setB(RECEIVED_SANDWICHES, true)
                    }
                    npc(SABBOT, CALM_TALK, "Use these rabbit sandwiches to heal when your health gets low")
                    player(CALM_TALK, "Thanks! Where did you get them?")
                    npc(SABBOT, CALM_TALK, "Freda keeps sending us them. She knows I hate tomato!")
                    npc(SABBOT, CALM_TALK, "Those caves'll likely be dangerous, and even if ya make it to the other side you'll face some trolls.")
                    npc(SABBOT, CALM_TALK, "So, either I leave 'em to start stinkin' up the cave or you eat 'em. Either way is good with me!")
                }
            }
        } else {
            player.faceDir(Direction.SOUTH)
            player.tele(Tile.of(3405, 4283, 2))
        }
    }
}
