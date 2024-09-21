package com.rs.game.content.quests.elderkiln

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.world.npcs.PKBotNPC
import com.rs.game.map.instance.Instance
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.InstancedController
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val LOLTHENKILL = 15152
const val ODISCHAMP = 15153
const val FIRECAPEZORZ = 15154
const val NOREMORSE77 = 15155
const val FIGHTPITPKER = 15156
const val KET_HARDENED = 15168
const val XIL_HARDENED = 15169
const val GAAL_XOX = 15135

@ServerStartupEvent
fun mapSaveGaalFightPits() {
    onNpcClick(LOLTHENKILL) { it.player.npcDialogue(it.npc, HeadE.CONFUSED, "Yea wut?") }
    onNpcClick(ODISCHAMP) { it.player.npcDialogue(it.npc, HeadE.CALM_TALK, "Hurry up, let's just get in the Pits. I wanna go kill frosties.") }
    onNpcClick(FIRECAPEZORZ) { it.player.npcDialogue(it.npc, HeadE.CONFUSED, "These noobs don't stand a chance.") }
    onNpcClick(NOREMORSE77) { it.player.npcDialogue(it.npc, HeadE.CONFUSED, "Have you seen any dragon claws lying around? I can't go into the Pits without my claws!") }
    onNpcClick(FIGHTPITPKER) { (player, npc) ->
        player.startConversation {
            npc(npc, HeadE.CALM_TALK, "Ten points if you kill another person; twenty if you kill a TzHaar. A point each for the Ga'al; they're fodder, dude.")
            player(HeadE.CONFUSED, "What are you doing?")
            npc(npc, HeadE.CALM_TALK, "Just making this a bit more fun.")
        }
    }
    onNpcClick(KET_HARDENED) { (player, npc) ->
        player.startConversation {
            npc(npc, T_CALM_TALK, "Are JalYt always this loud?")
            npc(npc, T_CALM_TALK, "Speak to TzHaar-Mej-Ak if you want fight in Fight Pit.")
        }
    }
    onNpcClick(XIL_HARDENED) { (player, npc) ->
        player.startConversation {
            npc(npc, T_CALM_TALK, "We here for fight, not talk.")
            npc(npc, T_CALM_TALK, "Speak to TzHaar-Mej-Ak if you want fight in Fight Pit.")
        }
    }
    onNpcClick(GAAL_XOX) { (player, npc) ->
        player.startConversation {
            npc(npc, T_CALM_TALK, "JalYt?")
            player(HeadE.CHEERFUL, "Wow...you can talk.")
            npc(npc, T_CALM_TALK, "Ga'al-Xox been here for long time. Ga'al-Xox one of first Ga'al.")
            player(HeadE.CHEERFUL, "You've taught yourself to speak. That's impressive.")
            npc(npc, T_CALM_TALK, "Ga'al-Xox speak good. Ga'al-Xox real TzHaar.")
            player(HeadE.CONFUSED, "What are you doing here?")
            npc(npc, T_CALM_TALK, "Ga'al-Xox here to die with honor!")
            player(HeadE.CONFUSED, "Er-")
            npc(npc, T_CALM_TALK, "Ga'al-Xox fight in Fight Pit with great honor!")
            label("ops")
            options {
                op("Can you help me convince that Ga'al to come with me?") {
                    npc(npc, T_CALM_TALK, "No, no. Ga'al here to die with honor!")
                    player(HeadE.CALM_TALK, "But TzHaar-Mej-Jeh has a plan that will be able to get the Ga'al's memories back.")
                    npc(npc, T_CALM_TALK, "Ga'al meant to stay here!")
                    goto("ops")
                }
                op("I need a Ga'al. Would you come with me? Jeh wouldn't know the difference.") {
                    npc(npc, T_CALM_TALK, "Why JalYt need Ga'al?")
                    player(HeadE.SKEPTICAL, "TzHaar-Mej-Jeh said he wanted his Ga'al, but you would do just as well. I'm sure he wouldn't know the difference.")
                    player(HeadE.CALM_TALK, "So, Ga'al, will you come with me? TzHaar-Mej-Jeh thinks there is a way of giving you back your memories and turning you into a real TzHaar.")
                    npc(npc, T_CALM_TALK, "Ga'al-Xox meant to stay here. Ga'al-Xox die with great honor!")
                    goto("ops")
                }
                op("I need to go.")
            }
        }
    }
}

fun saveGaalFightPitsAkDialogue(player: Player, npc: NPC) {
    player.startConversation {
        npc(npc.id, T_CALM_TALK, "TzHaar! JalYt! Gather here for the Fight Pit.")
        npc(LOLTHENKILL, HeadE.CALM_TALK, "This is going to be sweet!")
        npc(NOREMORSE77, HeadE.CONFUSED, "Anyone seen my D claws?")
        npc(npc.id, T_CALM_TALK, "Mighty warriors, prove your worth in fight to death!")
        npc(FIGHTPITPKER, HeadE.CALM_TALK, "So, are we all agreed on how many points you get per kill?")
        npc(ODISCHAMP, HeadE.CALM_TALK, "Nobody cares about your point system, Morean. It's stupid.")
        npc(FIGHTPITPKER, HeadE.ANGRY, "I'm not Morean, I'm FightpitPKer! Master of the bow, ruler of the P-")
        npc(KET_HARDENED, HeadE.T_ANGRY, "JalYt, be quiet!")
        npc(npc.id, T_CALM_TALK, "Ah, you again. You want to fight in Fight Pit?")
        label("ops")
        options {
            op("I've come for that Ga'al.") {
                npc(npc.id, T_CALM_TALK, "This is not your concern, JalYt.")
                player(HeadE.CALM_TALK, "But TzHaar-Mej-Jeh has a plan; he thinks he can return the Ga'als' memories.")
                npc(npc.id, T_CALM_TALK, "TzHaar-Mej-Jeh is not behaving like a good TzHaar. Good TzHaar think of TzHaar society first. TzHaar-Mej-Jeh thinks of nothing but his little Ga'al.")
                player(HeadE.CALM_TALK, "But he is thinking of the future of TzHaar society! We have to protect that Ga'al. TzHaar-Mej-Jeh needs him.")
                npc(npc.id, T_CALM_TALK, "TzHaar-Mej-Jeh's Ga'al will die in Fight Pit. This beyond your control...")
                npc(npc.id, HeadE.T_LAUGH, "...unless you think you could protect him in the Fight Pit.")
                simple("The only way to save Ga'al is to fight in the Fight Pit. Are you ready to travel to the Fight Pit to protect him?")
                options {
                    op("Yes.") {
                        npc(npc.id, T_CALM_TALK, "Fine! Let's get started, crowd getting rowdy.")
                        exec { player.controllerManager.startController(SaveGaalFightPitsController()) }
                    }
                    op("No.")
                }
            }
            op("What you're doing to the Ga'al is wrong.") {
                npc(npc.id, T_CALM_TALK, "Ga'al are a disease on TzHaar society, JalYt.")
                options {
                    op("But you can't just kill them!") {
                        npc(npc.id, T_CALM_TALK, "Our ways are not your soft ways, JalYt. To die in Fight Pit is great honor for any TzHaar.")
                    }
                    op("I suppose you're protecting your society.") {
                        npc(npc.id, T_CALM_TALK, "You smart for JalYt. Most JalYt think what we do to Ga'al is wrong.")
                    }
                }
            }
            op("What's going on here?") {
                npc(npc.id, T_CALM_TALK, "TzHaar organize big fights in the Fight Pit for TzHaar, JalYt and Ga'al.")
                npc(npc.id, T_CALM_TALK, "It about to start.")
                goto("ops")
            }
        }
    }
}

class SaveGaalFightPitsController : InstancedController(Instance.of(Tile.of(4670, 5160, 0), 8, 8)) {
    override fun onBuildInstance() {
        instance.copyMapAllPlanes(568, 632).thenAccept {
            player.cutscene {
                endTile = tileFromLocal(30, 32)
                fadeInAndWait()
                instance.teleportTo(player)
                entityTeleTo(player, 30, 32)
                wait(1)

                val xil2 = npcCreatePersistent(15178, 16, 39, 0) {
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }
                val noremorse77 = PKBotNPC(instance.getLocalTile(36, 26))
                noremorse77.apply {
                    maxMelee()
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }

                val firecapezorz = PKBotNPC(instance.getLocalTile(24, 28, 0))
                val xil1 = npcCreatePersistent(15178, 22, 27, 0) {
                    combatTarget = firecapezorz
                    setCanAggroNPCs(true)
                    isForceAgressive = true
                    isSpawned = true
                }
                firecapezorz.apply {
                    maxMelee()
                    combatTarget = xil1
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }

                val ket1 = npcCreatePersistent(15177, 41, 35, 0) {
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }
                val ket2 = npcCreatePersistent(15177, 41, 33, 0) {
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }
                ket1.combatTarget = ket2
                ket2.combatTarget = ket1

                val odischamp = PKBotNPC(instance.getLocalTile(33, 38, 0))
                val gaalXox = npcCreatePersistent(15180, 26, 35, 0)
                val gaalJeh = npcCreatePersistent(15179, 26, 33, 0)
                odischamp.apply {
                    maxMage()
                    combatTarget = gaalXox
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }
                gaalXox.isSpawned = true
                gaalXox.combatTarget = odischamp

                val fightPitPker = PKBotNPC(instance.getLocalTile(28, 46, 0))
                val lolThenKill = PKBotNPC(instance.getLocalTile(20, 46, 0))
                lolThenKill.apply {
                    maxMage()
                    combatTarget = fightPitPker
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }
                fightPitPker.apply {
                    maxRange()
                    combatTarget = lolThenKill
                    isForceAgressive = true
                    setCanAggroNPCs(true)
                    isSpawned = true
                }

                fadeOutAndWait()
                player.unlock()

                wait {
                    setOf(gaalXox, gaalJeh).any { it.isDead || it.hasFinished() } ||
                    setOf(xil1, xil2, ket1, ket2, noremorse77, firecapezorz, odischamp, lolThenKill, fightPitPker).all { it.isDead || it.hasFinished() }
                }
                player.lock()
                if (setOf(gaalXox, gaalJeh).any { it.isDead || it.hasFinished() }) {
                    player.safeDeath(instance.returnTo, "You failed to protect one of the Ga'al.") {
                        instance.destroy()
                        player.controllerManager.forceStop()
                    }
                    return@cutscene
                }
                fadeInAndWait()
                lowerAspectRatio()
                entityTeleTo(player, 28, 32)
                entityTeleTo(gaalXox, 29, 36)
                entityTeleTo(gaalJeh, 25, 35)
                val mejAk = npcCreatePersistent(15158, 31, 28, 0) { faceTile(instance.getLocalTile(27, 36)) }
                camPos(28, 24, 3144)
                camLook(28, 34, 1023)
                setOf(player, gaalXox, gaalJeh).forEach { it.faceEntityTile(mejAk) }
                fadeOutAndWait()
                dialogue {
                    npc(mejAk.id, T_CONFUSED, "You think you can save this...Ga'al?")
                }
                waitForDialogue()
                camPos(28, 34, 2408)
                camLook(36, 25, 100)
                val mejJeh = npcCreatePersistent(15166, 38, 25, 0)
                entityWalkTo(mejJeh, 33, 30)
                wait { !mejJeh.hasWalkSteps() }
                mejJeh.faceEntityTile(mejAk)
                dialogue {
                    npc(mejJeh.id, T_CALM_TALK, "We must talk, TzHaar-Mej-Ak. I know how to give memories to the Ga'al.")
                    npc(mejAk.id, T_ANGRY, "TzHaar-Mej-Jeh? All you talk of now is Ga'al. You have forgotten what it is to be a TzHaar. It is my duty to remind you...")
                    npc(mejAk.id, T_ANGRY, "Enter, Champion!")
                }
                waitForDialogue()
                val champion = npcCreate(15182, 25, 44, 0)
                setOf(player, gaalXox, gaalJeh, mejJeh).forEach { it.faceEntityTile(champion) }
                camPos(28, 24, 3144)
                camLook(28, 34, 1023)
                dialogue {
                    npc(mejAk.id, T_ANGRY, "Kill his Ga'al!")
                    npc(mejJeh.id, T_ANGRY, "No!")
                }
                waitForDialogue()
                champion.interactWithEntity(gaalJeh, 1) {
                    gaalJeh.schedule {
                        champion.anim(16090)
                        wait(1)
                        gaalJeh.anim(16222)
                        gaalJeh.applyHit(Hit.melee(champion, gaalJeh.hitpoints))

                    }
                }
                wait(10)
                dialogue {
                    npc(mejAk.id, T_CALM_TALK, "Now, TzHaar-Ket-Yit'tal, let's finish this fight. Empty the Fight Pit!")
                }
                waitForDialogue()
                fadeInAndWait()
                champion.finish()
                val championBoss = npcCreatePersistent(15181, 24, 35, 0)
                camPosResetHard()
                restoreDefaultAspectRatio()
                fadeOutAndWait()
                championBoss.combatTarget = player
                player.unlock()
                wait { championBoss.isDead || championBoss.hasFinished() }
                fadeInAndWait()
                player.setQuestStage(Quest.ELDER_KILN, STAGE_GO_TO_KILN)
                player.tele(instance.returnTo)
                player.controllerManager.forceStop()
                fadeOutAndWait()
            }
        }
    }

    override fun onDestroyInstance() { }

    override fun sendDeath(): Boolean {
        player.safeDeath(instance.returnTo, "You have been defeated.") {
            instance.destroy()
            player.controllerManager.forceStop()
        }
        return false
    }
}