package com.rs.game.content.quests.elderkiln

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest.ELDER_KILN
import com.rs.game.content.minigames.fightkiln.FightKilnController
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.Entity.MoveType
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.InstancedController
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.WorldUtil.regionIdToChunkCoords

@ServerStartupEvent
fun mapHandlers() {
    onObjectClick(68107) { (player, _, option) ->
        when(option) {
            "Enter" ->
                if (player.isQuestComplete(ELDER_KILN))
                    FightKilnController.enterFightKiln(player, false)
                else
                    enterQuestKiln(player)
            "Quick-Play Fight Kiln" ->
                if (player.isQuestComplete(ELDER_KILN))
                    FightKilnController.enterFightKiln(player, true)
        }
    }
}

private fun enterQuestKiln(player: Player) {
    if (player.getQuestStage(ELDER_KILN) < STAGE_GO_TO_KILN) {
        player.npcDialogue(15194, T_CALM_TALK, "No one allowed to pass through here. Much too dangerous for human and is sacred ground for TzHaar.")
        return
    }

    when(player.getQuestStage(ELDER_KILN)) {
        STAGE_GO_TO_KILN -> player.startConversation {
            npc(15194, T_CALM_TALK, "TzHaar-Mej-Jeh say you may pass through here. This go to ancient Kiln. Be careful, loud noises through there, sound like wild Ket-Zek. Take pickaxe too. No one been through for long time.")
            exec {
                player.cutscene {
                    fadeInAndWait()
                    dynamicRegion(Tile.of(player.tile), 592, 645, 2, 2, copyNpcs = true)
                    lowerAspectRatio()
                    entityTeleTo(player, 7, 12)
                    wait(1)
                    val xox = NPC(15134, tileFromLocal(8, 10))
                    xox.faceEntity(player)
                    player.faceEntity(xox)
                    camPos(6, 6, 2812)
                    camLook(7, 11, 1112)
                    fadeOutAndWait()
                    player.startConversation {
                        npc(xox, T_CONFUSED, "Ga'al-Xox and ${player.displayName} go now?")
                        player(CONFUSED, "Are you sure about this?")
                        npc(xox, T_CONFUSED, "Ga'al-Xox must be good Tzhaar.")
                        cosmeticOptions("But why? What do you owe the TzHaar?", "I suppose that's your choice.")
                        npc(xox, T_CALM_TALK, "All TzHaar put TzHaar before their self. Even Ga'al.")
                        npc(xox, T_CALM_TALK, "Ga'al follow you now.")
                    }
                    waitForDialogue()
                    fadeInAndWait()
                    player.setQuestStage(ELDER_KILN, STAGE_ESCORT_GAAL_KILN)
                    player.controllerManager.startController(EscortGaalKilnController())
                }
            }
        }
        STAGE_ESCORT_GAAL_KILN -> player.controllerManager.startController(EscortGaalKilnController())
    }
}

private val roomRegionIds = arrayOf(18258, 18514, 18770, 19026, 19282)

class EscortGaalKilnController() : InstancedController(Instance.of(Tile.of(4743, 5173, 0), 8, 8, true).setEntranceOffset(intArrayOf(26, 4, 1))) {
    lateinit var xox: NPC

    override fun onBuildInstance() {
        val chunk = regionIdToChunkCoords(roomRegionIds[player.questManager.getAttribs(ELDER_KILN).getI("kilnRoom", 0)])
        player.lock()
        instance.copyMapAllPlanes(chunk.first, chunk.second).thenAccept {
            player.cutscene {
                endTile = instance.getLocalTile(instance.entranceOffset[0], instance.entranceOffset[1])
                instance.teleportLocal(player, 26, 4, 0)
                xox = NPC(15134, Tile.of(0, 0, 3))
                xox.run = true
                xox.isIgnoreNPCClipping = true
                instance.teleportLocal(xox, 26, 5, 0)
                fadeOutAndWait()
                xox.follow(player)
            }
        }
    }

    override fun onDestroyInstance() {

    }

    override fun processObjectClick1(obj: GameObject): Boolean {
        when (obj.id) {
            68121 -> {
                player.lock()
                player.walkToAndExecute(obj.tile.transform(0, 2)) {
                    player.schedule {
                        player.addWalkSteps(obj.tile.transform(0, 1), 5, false)
                        wait(2)
                        player.faceTile(obj.tile.transform(0, -8))
                        wait(1)
                        jumpChasm(obj, -8, 2, Direction.SOUTH, -6, -2)
                    }
                }
            }
            68122 -> {
                player.lock()
                player.walkToAndExecute(obj.tile.transform(0, -1)) {
                    player.schedule {
                        player.addWalkSteps(obj.tile.transform(0, 0), 5, false)
                        wait(2)
                        player.faceTile(obj.tile.transform(0, 8))
                        wait(1)
                        if (player.questManager.getAttribs(ELDER_KILN).getB("learnedJump"))
                            jumpChasm(obj, 8, -1, Direction.NORTH, 6, 2)
                        else
                            playJumpCutscene()
                    }
                }
            }
        }
        return false
    }

    fun jumpChasm(obj: GameObject, playerYOffset: Int, xoxYOffset: Int, direction: Direction, xoxTeleYOffset: Int, walkToOffset: Int) {
        xox.stopAll()
        player.forceMove(player.transform(0, playerYOffset), 9310, 0, 60) {
            xox.lock()
            xox.walkToAndExecute(obj.tile.transform(0, xoxYOffset)) {
                xox.schedule {
                    xox.addWalkSteps(obj.tile.transform(0, xoxYOffset - 1), 5, false)
                    wait(2)
                    xox.faceDir(direction)
                    wait(1)
                    xox.anim(16226)
                    wait(5)
                    xox.tele(xox.transform(0, xoxTeleYOffset))
                    xox.anim(-1)
                    wait(1)
                    xox.addWalkSteps(xox.transform(0, walkToOffset), 5, false)
                    wait(2)
                    xox.unlock()
                    xox.follow(player)
                }
            }
        }
    }

    fun playJumpCutscene() {
        player.cutscene {
            endTile = player.transform(0, 8)
            fadeInAndWait()
            lowerAspectRatio()
            camPos(26, 1, 3660)
            camLook(26, 10, 3660)
            xox.stopAll()
            entityTeleTo(xox, 27, 9)
            fadeOutAndWait()
            player.forceMove(player.transform(0, 8), 9310, 0, 60)
            wait(4)
            player.faceDir(Direction.SOUTH)
            entityMoveTo(xox, 27, 11, MoveType.WALK)
            wait(4)
            dialogue {
                cosmeticOptions("Come on, Xox!", "Why aren't you following?")
                npc(xox, T_CALM_TALK, "Ground stop here.")
                player(LAUGH, "But it continues over here.")
                npc(xox, T_CALM_TALK, "Ga'al-Xox not there.")
                player(LAUGH, "You need to jump across!")
                npc(xox, T_CALM_TALK, "Jump? What jump?")
                player(LAUGH, "It's what I just did!")
                npc(xox, T_CALM_TALK, "I don't think TzHaar can jump.")
                player(LAUGH, "All you need to do is run, bend your legs and spring up. Go on - try to jump the chasm.")
                npc(xox, T_CALM_TALK, "The no-ground?")
                player(LAUGH, "Yeah - go for it!")
            }
            waitForDialogue()
            xox.anim(16226)
            wait(5)
            xox.tele(xox.transform(0, 6))
            xox.anim(-1)
            wait(2)
            fadeInAndWait()
            xox.tele(player.tile)
            xox.follow(player)
            camPosResetHard()
            restoreDefaultAspectRatio()
            fadeOutAndWait()
            player.questManager.getAttribs(ELDER_KILN).setB("learnedJump", true)
        }
    }
}