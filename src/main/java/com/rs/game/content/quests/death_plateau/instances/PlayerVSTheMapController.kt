package com.rs.game.content.quests.death_plateau.instances

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.content.skills.magic.TeleType
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.InstancedController
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Item
import com.rs.lib.game.Tile

class PlayerVSTheMapController : InstancedController(Instance.of(INSIDE_CAVE, 11, 9).setEntranceOffset(intArrayOf(46, 49, 0))) {

    @Transient
    private var theMap: NPC? = null

    @Transient
    private var wasRunning = false

    override fun onBuildInstance() {
        instance.copyMapAllPlanes(351, 446).thenAccept {

            val foundTroll = player.getQuestStage(Quest.DEATH_PLATEAU) == STAGE_FOUND_TROLL
            val angeredTroll = player.getQuestStage(Quest.DEATH_PLATEAU) == STAGE_ANGERED_TROLL

            if (!foundTroll && !angeredTroll) {
                player.cutscene {
                    wait(2)
                    val instance = this@PlayerVSTheMapController.instance ?: return@cutscene
                    instance.teleportLocal(player, 46, 49, 0)
                    hideMinimap()
                    player.interfaceManager.removeSubs(*InterfaceManager.Sub.ALL_GAME_TABS)
                    endTile = Tile.of(getX(46), getY(47), 0)
                    theMap = npcCreatePersistent(15100, 49, 46, 0) { setRandomWalk(false) }
                    player.faceTile(Tile.of(getX(46), getY(47), 0))
                    wasRunning = player.isRunning
                    val theMap = theMap ?: return@cutscene
                    wait(0)
                    camPos(52, 47, 3000)
                    camLook(48, 43, 2300)
                    wait(2)
                    fadeOut()
                    wait(4)

                    camPos(49, 41, 3300, 10, 10)
                    wait(1)
                    camPos(47, 40, 3300, 8, 8)
                    camLook(46, 49, 100, 5, 5)
                    wait(5)

                    dialogue { player(CALM_TALK, "Will you look at that... Death Plateau!") }
                    waitForDialogue()
                    entityWalkTo(player, 46, 47)
                    wait(2)

                    dialogue { player(CALM_TALK, "I'm sure we'll be able to ambush the trolls from up here.") }
                    entityWalkTo(theMap, 47, 47)
                    waitForDialogue()
                    dialogue { player(CALM_TALK, "And best of all, those dirty, stupid trolls don't know a thing about it.") }
                    waitForDialogue()
                    wait(1)

                    dialogue {
                        player.faceEntityTile(theMap)
                        theMap.faceEntityTile(player)
                        npc(theMap, T_CALM_TALK, "'ullo.")
                        player(CALM_TALK, "Uh...")
                    }
                    waitForDialogue()
                    player.setRun(wasRunning)
                    player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_FOUND_TROLL)
                    player.interfaceManager.sendSubDefaults(*InterfaceManager.Sub.ALL_GAME_TABS)
                    unhideMinimap()
                    camPosResetSoft()
                    player.unlock()
                }
            } else {
                player.cutscene {
                    val instance = this@PlayerVSTheMapController.instance ?: return@cutscene
                    instance.teleportLocal(player, 46, 49, 0)
                    endTile = Tile.of(getX(46), getY(49), 0)
                    theMap = npcCreatePersistent(15100, 49, 46, 0) { setRandomWalk(true) }
                    wasRunning = player.isRunning
                    theMap ?: return@cutscene

                    if (angeredTroll) startCombat(false)

                    player.setRun(wasRunning)
                    player.unlock()
                }
            }
        }
    }

    override fun onDestroyInstance() { }

    private fun startCombat(forceTalk: Boolean) {
        val angeredTroll = player.getQuestStage(Quest.DEATH_PLATEAU) == STAGE_ANGERED_TROLL
        if (!angeredTroll) player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_ANGERED_TROLL)
        val theMap = theMap ?: return
        theMap.transformIntoNPC(15101)
        theMap.setRandomWalk(true)
        theMap.isForceAgressive = true
        theMap.setForceAggroDistance(70)
        theMap.combatTarget = player
        if (forceTalk) theMap.forceTalk("I eat you up!")
    }

    override fun onTeleported(type: TeleType) {
        player.controllerManager.forceStop()
    }

    override fun sendDeath(): Boolean {
        player.controllerManager.forceStop()
        return false
    }

    override fun processNPCDeath(npc: NPC) {
        if (npc.id == 15101) {
            World.addGroundItem(Item(526), npc.middleTile)
            player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_KILLED_THE_MAP)
            player.playerDialogue(HAPPY_TALKING, "I think that takes care of him. I'll just take that tunnel or teleport back and speak to Denulth.")
        }
    }

    override fun processNPCClick1(npc: NPC): Boolean {
        if (npc.id == 15100) {
            val theMap = theMap ?: return false
            player.startConversation {
                player(CALM_TALK, "Err...hello?")
                npc(theMap, T_SCARED, "'ullo.")
                label("initialOps")
                options {
                    op("Who are you?") {
                        npc(theMap, T_CALM_TALK, "I's The Map.")
                        player(CALM_TALK, "The map?")
                        npc(theMap, T_CALM_TALK, "Yus. I's got the map, so me and me mates can hav a look around fer ways t' get down t' hooman town for the fightin'.")
                        player(CALM_TALK, "You have a map to Burthorpe? Who made it?")
                        npc(theMap, T_CALM_TALK, "Big boss made it. But den I ate it.")
                        player(CALM_TALK, "You...ate the map?")
                        npc(theMap, T_CALM_TALK, "Yus. Den the uvver trolls say mean fings. 'Lookit dis stoopid troll. He so stoopid he eats the map. Den dey all called me The Map from den on.")
                        options {
                            op("What happened next?") {
                                npc(theMap, T_CALM_TALK, "Den, we's lost on dis slope. We's got no food. We's got no map. So...I ate everyone.")
                                player(CALM_TALK, "You ate everyone.")
                                npc(theMap, T_CALM_TALK, "Well dey was dead when I did it. So dat's okay.")
                                player(CALM_TALK, "Well, if you were starving and they were dead...")
                                npc(theMap, T_CALM_TALK, "Yus. Some of dem try and pretend to be alive still, but I is smart troll. Dey no fool me.")
                                player(CALM_TALK, "Uh...")
                                npc(theMap, T_CALM_TALK, "Hey, is you dead? Just checkin'.")
                                goto("initialOps")
                            }
                            op("What are you doing here?") {
                                player(CALM_TALK, "What are you doing here?")
                                goto("doingHere")
                            }
                            op("Prepare to die, troll!") {
                                exec {
                                    player.npcDialogue(npc, T_ANGRY, "I eat you, hooman!")
                                    startCombat(true)
                                }
                            }
                            op("Err, I've got to be going now.") {
                                npc(theMap, T_ANGRY, "No! You is food! I eat you!")
                                exec { startCombat(true) }
                            }
                        }
                    }
                    op("What are you doing here?") {
                        label("doingHere")
                        npc(theMap, T_SCARED, "I's lost! Me and da lads, we was lookin' for a way down to hooman town.")
                        npc(theMap, T_SCARED, "The boss, he say 'You, you carry dis map I's made and go down to fight'.")
                        npc(theMap, T_SCARED, "Den, I eat the map. And all de uvvers get angry. And now I is lost...")
                        goto("initialOps")
                    }
                    op("Prepare to die, troll!") {
                        exec {
                            player.npcDialogue(npc, T_ANGRY, "I eat you, hooman!")
                            startCombat(true)
                        }
                    }
                    op("Err, I've got to be going now.") {
                        npc(theMap, T_ANGRY, "No! You is food! I eat you!")
                        exec { startCombat(true) }
                    }
                }
            }
        }
        return false
    }

    override fun processObjectClick1(obj: GameObject): Boolean {
        if (obj.id == 67572) {
            player.tele(INSIDE_CAVE)
            player.controllerManager.forceStop()
        }

        if (obj.id == 3748 || obj.id == 34878) {
            player.sendMessage("<col=A31818>You think you'll be better off trying to get back down to town through the tunnel you came through.</col>")
            return false;
        }
        return true
    }

    companion object {
        private val INSIDE_CAVE: Tile = Tile.of(3435, 4240, 2)
    }
}
