package com.rs.game.content.quests.plaguecity.cutscene

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile

class PlagueCityCutscene (player: Player) {
    init {
        player.cutscene {
            fadeInAndWait()
            hideMinimap()
            endTile = Tile.of(CUTSCENE_END_TILE)
            player.interfaceManager.removeSubs(*InterfaceManager.Sub.ALL_GAME_TABS)
            dynamicRegion(player.tile, 312, 1216, 8, 8)
            val edmond = npcCreate(EDMOND_BELOW_GROUND, 18, 13, 0)
            entityTeleTo(player, 18, 12)
            wait(0)
            edmond.faceDir(Direction.SOUTH)
            player.faceDir(Direction.SOUTH)
            wait(0)
            camPos(22, 15, 1300)
            camLook(17, 12, 1200)
            fadeOut()
            wait(2)

            player.forceTalk("1...")
            wait(3)
            player.forceTalk("2...")
            wait(3)
            player.forceTalk("3...")
            wait(3)
            player.forceTalk("Pull!")
            wait(1)
            player.soundEffect(PULL_GRILL, false)
            player.anim(ROPE_PULL_ANIM)
            edmond.anim(ROPE_PULL_ANIM)
            wait(Animation(ROPE_PULL_ANIM).defs.emoteGameTicks)
            player.anim(-1)
            edmond.anim(-1)
            player.vars.setVarBit(GRILL_VB, 2)
            player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_GRILL_REMOVED)
            wait(1)
            edmond.forceTalk("Whew!")
            wait(2)

            fadeInAndWait()
            player.interfaceManager.sendSubDefaults(*InterfaceManager.Sub.ALL_GAME_TABS)
            returnPlayerFromInstance()
            unhideMinimap()
            camPosResetSoft()
            stop()
            fadeOut()

            player.lock()
            player.faceTile(Tile.of(EDMOND_SEWER_SPAWN_LOC))
            dialogue {
                npc(EDMOND_BELOW_GROUND, CALM_TALK, "Once you're in the city look for my old friend, Jethick. I expect he'll help you. Send him my regards; I haven't seen him since before Elena was born.")
                player(CALM_TALK, "Thanks, I will.")
                exec { player.unlock() }
            }

        }
    }
}
