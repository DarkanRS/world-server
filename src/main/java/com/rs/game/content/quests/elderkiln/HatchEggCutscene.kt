package com.rs.game.content.quests.elderkiln

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.sendOptionsDialogue
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.combat.CombatSpell
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.Teleport
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.InstancedController
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.lib.game.Tile
import com.rs.lib.net.ClientPacket
import com.rs.lib.util.Utils

private const val GAAL = 15131

fun hatchEggCutscene(player: Player) {
    player.controllerManager.startController(object : InstancedController(Instance.of(Tile.of(4709, 5160, 0), 8, 8)) {
        @Transient var hatchPercent = 0
        @Transient var currentSpot = 1
        @Transient var heatLevel = 0

        override fun onBuildInstance() {
            instance.copyMapAllPlanes(586, 642).thenAccept {
                player.cutscene {
                    fadeInAndWait()
                    instance.teleportTo(player)
                    entityTeleTo(player, 21, 23)
                    wait(1)
                    sendInterfaces()
                    camPos(15, 17, 3512)
                    camLook(20, 22, 2012)
                    player.faceTile(player.transform(4, 2))
                    val tzHaarMejs = arrayOf(TZHAAR_MEJ_JEH_BPOOL, TZHAAR_MEJ_AK_BPOOL).map { npcId ->
                        npcCreate(npcId, if (npcId == TZHAAR_MEJ_JEH_BPOOL) 20 else 23, if (npcId == TZHAAR_MEJ_JEH_BPOOL) 27 else 21, 0) {
                            faceTile(player.transform(4, 2))
                            schedule {
                                if (id == TZHAAR_MEJ_JEH_BPOOL)
                                    wait(25)
                                while (player.vars.getVarBit(10833) == 0) {
                                    forceTalk("Firing now!")
                                    wait(5)
                                    val delay = CombatSpell.FIRE_WAVE.cast(this@npcCreate, player.transform(4, 2), 80)
                                    anim(16122)
                                    wait(delay)
                                    changeHeat(25)
                                    wait(50)
                                }
                            }
                        }
                    }.toList()
                    val (jeh, ak) = tzHaarMejs
                    fadeOutAndWait()
                    player.unlock()
                    wait { hatchPercent >= 100 }
                    closeInterfaces()
                    player.lock()
                    player.vars.setVarBit(10833, 1)
                    dialogue { npc(jeh.id, T_CALM_TALK, "It hatches!") }
                    waitForDialogue()
                    World.getClosestObject(player.tile) { it.id == 68824 }?.animate(16245)
                    val gaal = npcCreate(GAAL, 23, 24, 0) {
                        faceDir(Direction.WEST)
                        anim(16244)
                    }
                    wait(7)
                    player.vars.setVarBit(10833, 2)
                    dialogue {
                        npc(ak.id, T_SAD, "It is like I said, TzHaar-Mej-Jeh! Another one born as Ga'al.")
                        npc(jeh.id, T_CONFUSED_DENIAL, "No, it cannot be.")
                        npc(jeh.id, T_ANGRY, "TzHaar, what is your caste?")
                        npc(gaal.id, T_CONFUSED, "Guh?")
                        npc(ak.id, T_SAD, "TzHaar-Mej-Jeh, egg is Ga'al, like all the others...")
                        npc(jeh.id, T_ANGRY_NO, "No...")
                        label("ops")
                        options {
                            ops("It hatched, isn't that good?", "What's a Ga'al?") {
                                npc(ak.id, T_CALM_TALK, "TzHaar are born with memories of all ancestors that came before. That is how we know our caste, how to do our work, what our laws are.")
                                npc(ak.id, T_CALM_TALK, "Ga'al are born without memory of what came before. Without memory, Ga'al cannot do work; they are clumsy, lazy and lawless.")
                                npc(jeh.id, T_CALM_TALK, "Almost all TzHaar are born this way now...those that do not die in the egg. We do not know why.")
                                npc(gaal.id, T_CONFUSED, "Guh-huh?")
                                npc(ak.id, T_CALM_TALK, "I shall take this Ga'al...away.")
                                npc(jeh.id, T_CALM_TALK, "But-")
                                npc(ak.id, T_CALM_TALK, "We must, TzHaar-Mej-Jeh. This you know.")
                                npc(ak.id, T_CALM_TALK, "Follow me, Ga'al.") { entityWalkTo(ak, 0, -15) }
                                npc(ak.id, T_CALM_TALK, "I said follow.")
                            }
                        }
                    }
                    waitForDialogue()
                    entityWalkTo(gaal, 0, -15)
                    wait(5)
                    fadeInAndWait()
                    player.tele(instance.returnTo)
                    closeInterfaces()
                    player.controllerManager.forceStop()
                    fadeOutAndWait()
                    dialogue {
                        npc(jeh.id, T_SAD, "TzHaar should not feel a bond with their egg. No one TzHaar is more important than another. But...")
                        npc(jeh.id, T_SAD, "Through our eggs we live on - new TzHaar carry dead parent's memories. Now, my memory will not pass on. I am old TzHaar. I will not lay another egg.")
                        player(CONFUSED, "What will happen to the Ga'al?")
                        npc(jeh.id, T_SAD, "TzHaar-Mej-Ak will gather Ga'al and have them fight in the Fight Pit. Ga'al do not know how to fight, so they die.")
                        options {
                            ops("That's barbaric!", "Why do you do this?") {
                                npc(jeh.id, T_CALM_TALK, "The Ga'al are weak and useless without memory. They serve no purpose in TzHaar society.")
                                options {
                                    ops("Can't you teach them your ways?", "But why kill them?") {
                                        npc(jeh.id, T_CALM_TALK, "If something has no purpose it must be destroyed. It TzHaar way. We must think of strength of TzHaar society as a whole, not the weakness of separate creatures.")
                                        npc(jeh.id, T_CALM_TALK, "We are based atheist philosophers that take our philosophy to its logical conclusion.")
                                        npc(jeh.id, T_CALM_TALK, "But it has become big problem for TzHaar. So few TzHaar are born now...just more and more Ga'al. We do not know what the cause of it is. TzHaar-Mej have investigated, but to no benefit or great discovery.")
                                        npc(jeh.id, T_CALM_TALK, "I have been thinking of something for some time, JalYt-Hur-${player.displayName}, but it is dangerous and uses old magic. Other TzHaar might not agree. They will say TzHaar-Mej-Jeh does it for his own memories, not for good of TzHaar.")
                                        player(CONFUSED, "What is it?")
                                        npc(jeh.id, T_CALM_TALK, "It is a way of giving Ga'al back their memories.")
                                        npc(jeh.id, T_CALM_TALK, "Other Mej will not like it. Many think we should not go back...there. But it is the only way. Otherwise, our memories will be lost.")
                                        npc(jeh.id, T_CALM_TALK, "We need a Ga'al - my Ga'al - to test this on. You will need to stop him entering the Fight Pit. Bring him to me at the library. There is research I must do before I speak to other TzHaar-Mej about this.")
                                        npc(jeh.id, T_CALM_TALK, "Go to Main Plaza in the centre of TzHaar City, west of here, and bring Ga'al back. Convince TzHaar-Mej-Ak if you must.")
                                        player(CALM_TALK, "I'll help.")
                                        npc(jeh.id, T_CALM_TALK, "Thank you, JalYt-Hur-${player.displayName}. Bring him to me at the library.")
                                        exec { player.setQuestStage(Quest.ELDER_KILN, STAGE_GAAL_TO_LIBRARY) }
                                    }
                                }
                            }
                        }
                    }
                    waitForDialogue()
                    player.unlock()
                }
            }
        }

        override fun onDestroyInstance() { }

        override fun processButtonClick(interfaceId: Int, componentId: Int, slotId: Int, slotId2: Int, packet: ClientPacket): Boolean {
            if (interfaceId != 1247)
                return false
            when(componentId) {
                9 -> spell(CombatSpell.FIRE_BOLT, 5)
                18 -> spell(CombatSpell.FIRE_BLAST, 10)
                29 -> spell(CombatSpell.FIRE_WAVE, 25)
                40 -> spell(CombatSpell.WATER_BOLT, -5)
                51 -> spell(CombatSpell.WATER_BLAST, -10)
                62 -> spell(CombatSpell.WATER_WAVE, -25)
                71 -> player.sendOptionsDialogue("Really exit?") {
                    opExec("Yes, I want to leave.") {
                        player.cutscenePresenter.stop()
                        forceClose()
                    }
                    op("Nevermind.")
                }
            }
            return false
        }

        fun spell(spell: CombatSpell, heatChange: Int) {
            player.lock(spell.cast(player, player.transform(4, 2), 80))
            changeHeat(heatChange)
        }

        override fun process() {
            when(currentSpot) {
                1 -> if (heatLevel in 20..40) hatchPercent++ else hatchPercent--
                2 -> if (heatLevel in 55..70) hatchPercent++ else hatchPercent--
                3 -> if (heatLevel in 80..90) hatchPercent++ else hatchPercent--
            }
            hatchPercent = Utils.clampI(hatchPercent, 0, 100)
            if (World.getServerTicks() % 20L == 0L)
                currentSpot = Utils.randomInclusive(1, 3)
            changeHeat(-1)
            updateInterface()
        }

        fun changeHeat(change: Int) {
            heatLevel = Utils.clampI(heatLevel+change, 0, 100)
        }

        fun updateInterface() {
            player.packets.sendRunScript(945, heatLevel, hatchPercent, currentSpot)
        }

        override fun sendInterfaces() {
            player.interfaceManager.removeSubs(*InterfaceManager.Sub.ALL_GAME_TABS.filter { it != InterfaceManager.Sub.TAB_INVENTORY }.toTypedArray())
            player.interfaceManager.sendOverlay(1246)
            player.interfaceManager.sendInventoryOverlay(1247)
        }

        fun closeInterfaces() {
            player.interfaceManager.removeOverlay()
            player.interfaceManager.sendSubDefaults(*InterfaceManager.Sub.ALL_GAME_TABS)
        }

        override fun canMove(dir: Direction?): Boolean {
            if (player.vars.getVarBit(10833) == 0)
                sendInterfaces()
            return false
        }

        override fun processTeleport(tele: Teleport?): Boolean {
            return false
        }
    })
}