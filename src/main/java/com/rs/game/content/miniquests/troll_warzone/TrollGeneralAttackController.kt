package com.rs.game.content.miniquests.troll_warzone

import com.rs.engine.cutscene.Cutscene
import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.*
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.pathfinder.Direction
import com.rs.game.World.addGroundItem
import com.rs.game.World.getNPCsInChunkRange
import com.rs.game.World.spawnNPC
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.Entity.MoveType
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.InstancedController
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Item
import com.rs.lib.game.Tile

class TrollGeneralAttackController : InstancedController(Instance.of(OUTSIDE, 8, 8).persist().setEntranceOffset(intArrayOf(32, 12, 0))) {
    @Transient
    private var stage = 0

    @Transient
    private var ozan: NPC? = null

    @Transient
    private var keymans: NPC? = null

    @Transient
    private var trollGeneral: NPC? = null

    override fun onBuildInstance() {
        player.lock()
        instance.copyMapAllPlanes(272, 544).thenAccept {
            player.cutscene {
                val instance = this@TrollGeneralAttackController.instance ?: return@cutscene
                fadeIn()
                wait(5)
                instance.teleportLocal(player, 32, 12, 0)
                endTile = Tile.of(getX(32), getY(12), 0)
                ozan = npcCreatePersistent(14983, 33, 28, 0) {
                    isIgnoreNPCClipping = true
                    run = true
                }
                keymans = npcCreatePersistent(14988, 34, 28, 0) {
                    isIgnoreNPCClipping = true
                    run = true
                }
                npcCreatePersistent(14980, 27, 43, 0) { setRandomWalk(true) }
                npcCreatePersistent(14981, 29, 46, 0) { setRandomWalk(true) }
                npcCreatePersistent(14982, 31, 44, 0) { setRandomWalk(true) }
                player.hintIconsManager.addHintIcon(ozan, 0, -1, false)
                fadeOut()
                wait(5)
                player.unlock()
            }
        }
    }

    override fun onDestroyInstance() {
        player.setForceMultiArea(false)
    }

    override fun sendDeath(): Boolean {
        player.safeDeath(OUTSIDE)
        return false
    }

    override fun processNPCClick1(npc: NPC): Boolean {
        npc.faceTile(player.tile)
        player.faceTile(npc.tile)
        when (npc.id) {
            14983, 14987 -> {
                when (stage) {
                    0 -> player.startConversation {
                        npc(npc.id, HeadE.CALM_TALK, "The trolls are bypassing Burthorpe's defenses through this tunnel!<br><br><col=2A32C9>Click the green button below or press the space bar.")
                        npc(npc.id, HeadE.CALM_TALK, "You lead, we'll follow.") {
                            stage = 1
                            ozan?.follow(player)
                            keymans?.follow(ozan)
                            player.hintIconsManager.removeAll()
                            trollGeneral = spawnNPC(14991, Tile.of(instance.getLocalX(36), instance.getLocalY(53), 0), true, true)
                            trollGeneral?.setRandomWalk(false)
                            trollGeneral?.isCantInteract = true
                            player.hintIconsManager.addHintIcon(instance.getLocalX(37), instance.getLocalY(54), 0, 50, 0, 0, -1, false)
                        }
                    }
                    1 -> player.npcDialogue(npc, HeadE.CALM_TALK, "I can hear some loud footsteps up ahead. You lead, we'll follow.")
                    2 -> player.npcDialogue(npc, HeadE.ANGRY, "There's no time to talk! Attack!")
                    3 -> player.startConversation {
                        npc(npc, HeadE.HAPPY_TALKING, "Congratulations all around, friends! Now all we need to do is-")
                        npc(npc, HeadE.CALM, "...")
                        npc(npc, HeadE.CONFUSED, "Hey, did you hear that?")
                        npc(npc, HeadE.CALM_TALK, "It came from over there.")
                        exec {
                            player.cutscene {
                                val instance = this@TrollGeneralAttackController.instance ?: return@cutscene
                                val ozan = ozan ?: return@cutscene
                                fadeIn()
                                wait(5)
                                val babyTroll = npcCreate(14846, 28, 48, 0)
                                player.hintIconsManager.removeUnsavedHintIcon()
                                ozan.tele(Tile.of(instance.getLocalX(36), instance.getLocalY(51), 0))
                                player.tele(Tile.of(instance.getLocalX(36), instance.getLocalY(48), 0))
                                camPos(36, 51, 1000)
                                camLook(31, 51, 0)
                                fadeOut()
                                wait(5)
                                npcMove(babyTroll, 28, 51, MoveType.RUN)
                                wait(2)
                                camPos(32, 45, 2000, 0, 10)
                                wait(1)
                                npcMove(babyTroll, 30, 51, MoveType.RUN)
                                wait(3)
                                ozan.run = false
                                ozan.addWalkSteps(instance.getLocalX(33), instance.getLocalY(51))
                                wait(4)
                                ozan.faceTile(Tile.of(instance.getLocalX(20), instance.getLocalY(51), 0))
                                wait(1)
                                ozan.tele(Tile.of(instance.getLocalX(31), instance.getLocalY(51), 0))
                                ozan.transformIntoNPC(14987)
                                ozan.anim(15817)
                                babyTroll.finish()
                                wait(23)
                                stage = 4
                                player.hintIconsManager.addHintIcon(instance.getLocalX(ozan.xInRegion), instance.getLocalY(ozan.yInRegion), 0, 50, 0, 0, -1, false)
                            }
                        }
                    }

                    4 -> {
                        val endDialogue = createDialogueSection {
                            npc(14846, HeadE.T_CONFUSED, "Food?")
                            npc(npc, HeadE.CALM_TALK, "I'm going to take this little fellow to the training grounds.")
                            npc(npc, HeadE.CALM_TALK, "Thanks again for your help. You should check in with Captain Jute outside the cave.")
                            exec {
                                player.cutscene {
                                    val instance = this@TrollGeneralAttackController.instance ?: return@cutscene
                                    val ozan = ozan ?: return@cutscene
                                    val keymans = keymans ?: return@cutscene
                                    ozan.addWalkSteps(ozan.transform(0, -10, 0).x, ozan.transform(0, -10, 0).y)
                                    keymans.addWalkSteps(keymans.transform(0, -10, 0).x, keymans.transform(0, -10, 0).y)
                                    fadeIn()
                                    wait(5)
                                    ozan.finish()
                                    keymans.finish()
                                    fadeOut()
                                    wait(5)
                                    stage = 5
                                    player.hintIconsManager.removeUnsavedHintIcon()
                                    player.hintIconsManager.addHintIcon(instance.getLocalX(32), instance.getLocalY(12), 0, 50, 0, 0, -1, false)
                                }
                            }
                        }

                        player.startConversation {
                            npc(npc, HeadE.CONFUSED, "Where did this little guy come from? Do trolls always bring babies along on raids?")
                            options {
                                op("Don't we have more important things to worry about?") {
                                    npc(npc, HeadE.SAD, "We can't just leave it here to die.")
                                    next(endDialogue)
                                }
                                op("He's so cute!") {
                                    npc(npc, HeadE.SAD_MILD, "Isn't he? He's so wubbly!")
                                    next(endDialogue)
                                }
                                op("We should kill it before it becomes a threat!") {
                                    npc(npc, HeadE.SAD, "I can't just execute a baby even if it is a troll!")
                                    next(endDialogue)
                                }
                            }
                        }
                    }
                }
            }

            14988 -> {
                when (stage) {
                    0 -> player.npcDialogue(npc, HeadE.CONFUSED, "Ozan, what should we do next?<br><br><col=2A32C9>Click the green button below or press the space bar.")
                    1 -> player.npcDialogue(npc, HeadE.AMAZED, "Whoah! There is something huge up ahead.")
                    2 -> player.npcDialogue(npc, HeadE.ANGRY, "There's no time to talk! Attack!")
                    3 -> player.npcDialogue(npc, HeadE.CONFUSED, "What should we do next, Ozan?")
                }
            }
        }
        return false
    }

    override fun processObjectClick1(obj: GameObject): Boolean {
        if (obj.id == 66534) {
            if (stage == 5) {
                player.hintIconsManager.removeUnsavedHintIcon()
                player.miniquestManager.setStage(Miniquest.TROLL_WARZONE, 2)
                player.tele(OUTSIDE)
                player.controllerManager.forceStop()
                return false
            }
            player.sendOptionDialogue("Would you like to leave the tutorial area?") { ops: Options ->
                ops.add("Yes, please.") {
                    player.tele(OUTSIDE)
                    player.controllerManager.forceStop()
                }
                ops.add("No, I'm not done here yet.")
            }
            return false
        }
        return true
    }

    override fun canMove(dir: Direction): Boolean {
        if (stage == 0 && player.tile.xInRegion >= 32 && player.tile.xInRegion <= 35 && player.tile.yInRegion >= 27 && dir.dy > 0) return false
        if (stage == 1 && player.tile.yInRegion >= 49) {
            player.stopAll()
            player.lock()
            player.cutscene {
                val instance = this@TrollGeneralAttackController.instance ?: return@cutscene
                val ozan = ozan ?: return@cutscene
                val keymans = keymans ?: return@cutscene
                val trollGeneral = trollGeneral ?: return@cutscene
                ozan.stopAll()
                keymans.stopAll()
                player.tele(Tile.of(instance.getLocalX(34), instance.getLocalY(50), 0))
                ozan.tele(Tile.of(instance.getLocalX(33), instance.getLocalY(51), 0))
                keymans.tele(Tile.of(instance.getLocalX(35), instance.getLocalY(49), 0))
                camPos(33, 45, 2000, 0, 5)
                camLook(trollGeneral.xInRegion, trollGeneral.yInRegion, 10, 0, 5)
                wait(5)
                trollGeneral.faceTile(player.tile)
                player.faceTile(trollGeneral.tile)
                ozan.faceTile(trollGeneral.tile)
                keymans.faceTile(trollGeneral.tile)
                dialogue {
                    npc(ozan.id, HeadE.ANGRY, "The troll general! Bring him down!")
                    npc(trollGeneral.id, HeadE.T_ANGRY, "STUPID HUMANS! TROLLS SMASH YOUR STUPID FACE!")
                }
                waitForDialogue()
                trollGeneral.setForceMultiArea(true)
                ozan.setForceMultiArea(true)
                keymans.setForceMultiArea(true)
                player.setForceMultiArea(true)
                trollGeneral.isCantInteract = false
                trollGeneral.target = player
                trollGeneral.addReceivedDamage(player, 5000)
                ozan.target = trollGeneral
                keymans.target = trollGeneral
                camPosResetSoft()
            }
            stage = 2
            return false
        }
        return true
    }

    override fun processNPCDeath(npc: NPC) {
        if (npc.id == 14991 && stage == 2) {
            for (n in getNPCsInChunkRange(player.chunkId, 3)) {
                if (n.id in 14980..14982) n.finish()
            }
            addGroundItem(Item(23042), npc.middleTile)
            addGroundItem(Item(23031), npc.middleTile)
            stage = 3
            player.hintIconsManager.removeAll()
            ozan?.let {
                player.hintIconsManager.addHintIcon(instance.getLocalX(it.xInRegion), instance.getLocalY(it.yInRegion), 0, 50, 0, 0, -1, false)
            }
        }
    }

    companion object {
        private val OUTSIDE: Tile = Tile.of(2878, 3573, 0)
    }
}
