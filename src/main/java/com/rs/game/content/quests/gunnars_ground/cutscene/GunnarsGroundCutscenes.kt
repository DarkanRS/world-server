package com.rs.game.content.quests.gunnars_ground.cutscene

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.gunnars_ground.dialogues.GudrunD
import com.rs.game.content.quests.gunnars_ground.utils.*
import com.rs.game.model.entity.interactions.StandardEntityInteraction
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager

class GunnarsGroundCutscenes (player: Player) {
    init {
        player.cutscene {
            fadeInAndWait()
            hideMinimap()
            lowerAspectRatio()
            player.interfaceManager.removeSubs(*InterfaceManager.Sub.ALL_GAME_TABS)
            dynamicRegion(player.tile, 382, 427, 8, 8)
            val dororan = npcCreate(CUTSCENE_DORORAN, 23, 19, 0)
            dororan.faceDir(Direction.NORTH)
            dororan.anim(ANIM_DORORAN_LISTENING)
            entityTeleTo(player, 22, 19)
            player.musicsManager.playSongWithoutUnlocking(CUTSCENE_MUSIC_ID)
            player.faceDir(Direction.NORTH)
            player.anim(ANIM_PLAYER_LISTENING)
            wait(1)
            camPos(23, 12, 1300)
            camLook(22, 33, 500)
            camPos(23, 14, 1200, 1, 1)
            fadeOut()
            wait(0)

            dialogue {
                npc(CUTSCENE_DORORAN, HAPPY_TALKING, "How long have they been in there?")
                options {
                    op("They're just starting.") {
                        player(HAPPY_TALKING, "They're just starting.") { reassureAnim(player) }
                        goto("goingToWork")
                    }
                    op("You're late.") {
                        player(FRUSTRATED, "You're late.") { reassureAnim(player) }
                        goto("goingToWork")
                    }
                }
                label("goingToWork")
                npc(CUTSCENE_DORORAN, WORRIED, "This isn't going to work.") { resetAnim(player) }
                options {
                    op("Why's that?") {
                        player(CONFUSED, "Why's that?") { reassureAnim(player) }
                        goto("whatWasIThinking")
                    }
                    op("You're so pessimistic.") {
                        player(FRUSTRATED, "You're so pessimistic.") { reassureAnim(player) }
                        goto("whatWasIThinking")
                    }
                }
                label("whatWasIThinking")
                npc(CUTSCENE_DORORAN, MORTIFIED, "What was I thinking? You should go in there and stop them before Gudrun makes a fool of herself.") { resetAnim(player) }
                options {
                    op("Okay, I will.") {
                        player(CONFUSED, "Okay, I will.") { reassureAnim(player) }
                        npc(CUTSCENE_DORORAN, TERRIFIED, "No! Wait, stay here, it's too late now. We'll just have to see how it turns out.") { resetAnim(player) }
                        goto("hearWhatsHappening")
                    }
                    op("Don't be silly.") {
                        player(FRUSTRATED, "Don't be silly.") { reassureAnim(player) }
                        npc(CUTSCENE_DORORAN, WORRIED, "You're right, it's too late now. We'll just have to see how it turns out.") { resetAnim(player) }
                        goto("hearWhatsHappening")
                    }
                }
                label("hearWhatsHappening")
                npc(CUTSCENE_DORORAN, SKEPTICAL_THINKING, "I can't hear what's happening. Can you hear what's happening?") { resetAnim(player) }
                player(CALM_TALK, "Gunthor is laughing at something.") { reassureAnim(player) }
                npc(CUTSCENE_DORORAN, WORRIED, "He's probably considering the various tortures he has planned for me.") { resetAnim(player) }
                options {
                    op("Why would he do that?") {
                        player(CONFUSED, "Why would he do that?") { reassureAnim(player) }
                        goto("poemSays")
                    }
                    op("Now you're just being ridiculous.") {
                        player(FRUSTRATED, "Now you're just being ridiculous.") { reassureAnim(player) }
                        label("poemSays")
                        npc(CUTSCENE_DORORAN, WORRIED, "The poem says you can honour your ancestors by settling peacefully on the land they conquered. He'll probably find it insulting.") { resetAnim(player) }
                        goto("nowsYourChanceOptions")
                    }
                }
                label("nowsYourChanceOptions")
                options {
                    op("Now's your chance to find out.") {
                        player(CONFUSED, "Now's your chance to find out.") { reassureAnim(player) }
                    }
                    op("You're doomed.") {
                        player(FRUSTRATED, "You're doomed.") { reassureAnim(player) }
                    }
                }
            }
            waitForDialogue()
            fadeInAndWait()

            // Speech Cutscene
            player.anim(-1)
            entityTeleTo(dororan, 26, 12)
            player.appearance.transformIntoNPC(1957)
            dororan.anim(-1)
            val chieftainGunthor = npcCreate(CUTSCENE_CHIEFTAIN_GUNTHOR, 23, 9, 0)
            val haakon = npcCreate(CUTSCENE_HAAKON, 22, 9, 0)
            val kjell = npcCreate(CUTSCENE_KJELL, 21, 10, 0)
            val gudrun = npcCreate(CUTSCENE_GUDRUN, 24, 10, 0)
            wait(0)
            listOf(dororan, chieftainGunthor, haakon, kjell, gudrun).forEach { npc ->
                npc.setRandomWalk(false)
                npc.faceDir(Direction.SOUTH)
            }
            wait(0)
            player.faceDir(Direction.WEST)
            wait(1)
            camPos(23, 3, 1200)
            camLook(23, 11, 100)
            camPos(23, 3, 1400, 1, 1)
            fadeOut()
            wait(1)

            dialogue {
                npc(CUTSCENE_DORORAN, WORRIED, "I hope they at least give me a decent burial.")
                npc(CUTSCENE_CHIEFTAIN_GUNTHOR, ANGRY, "Freemen! Freemen! I have an announcement!") { chieftainGunthor.anim(ANIM_CHIEFTAIN_SPEECH) }
                npc(CUTSCENE_KJELL, ANGRY, "Hear the chieftain speak! Hear him!")
                npc(CUTSCENE_CHIEFTAIN_GUNTHOR, ANGRY, "We have always borne the legacy of our ancestors, and we have borne it with honour!")
                npc(CUTSCENE_KJELL, ANGRY, "FOR GUNNAR!")
                npc(CUTSCENE_CHIEFTAIN_GUNTHOR, ANGRY, "And though we honour them still, the time of our ancestors is past. This is the time of Gunthor!")
                npc(CUTSCENE_HAAKON, ANGRY, "FOR GUNTHOR!")
                npc(CUTSCENE_CHIEFTAIN_GUNTHOR, ANGRY, "Gunthor says: This is Gunnar's ground, bought with blood! Let it remain Gunnar's ground forever! Here we settle!")
                npc(CUTSCENE_CHIEFTAIN_GUNTHOR, ANGRY, "GUNNAR'S GROUND!")
                npc(CUTSCENE_HAAKON, ANGRY, "GUNNAR'S GROUND!") {
                    kjell.anim(ANIM_KJELL_CHEER)
                    haakon.anim(ANIM_HAAKON_CHEER)
                }
            }
            waitForDialogue()
            fadeInAndWait()

            // Hug Cutscene
            listOf(dororan, chieftainGunthor, haakon, kjell, gudrun).forEach { npc ->
                npc.finish()
            }
            player.appearance.transformIntoNPC(-1)
            entityTeleTo(player, 27, 10)
            wait(0)
            player.musicsManager.playSongWithoutUnlocking(CUTSCENE_HUG_MUSIC_ID)
            objCreate(5657, 1, 26, 9, 0)
            player.faceTile(tileFromLocal(25, 9))
            wait(0)
            camPos(28, 5, 1200)
            camLook(25, 11, 100)
            fadeOut()
            wait(1)

            dialogue {
                npc(CUTSCENE_GUDRUN, AMAZED, "That was brilliant! I must know who wrote that poem.")
                npc(CUTSCENE_DORORAN, CALM_TALK, "Um, that would be me. Hello.")
                npc(CUTSCENE_GUDRUN, AMAZED, "That line about beauty was for me, wasn't it?")
                npc(CUTSCENE_DORORAN, AMAZED, "Uh, yes.")
                npc(CUTSCENE_GUDRUN, AMAZED, "You're the mystery poet who sent me the gold ring!")
                npc(CUTSCENE_DORORAN, SAD, "Sorry.")
                npc(CUTSCENE_GUDRUN, HAPPY_TALKING, "I have no idea dwarves could be so romantic! Come here!")
            }
            waitForDialogue()
            wait(3)

            fadeInAndWait()
            player.interfaceManager.sendSubDefaults(*InterfaceManager.Sub.ALL_GAME_TABS)
            player.questManager.setStage(Quest.GUNNARS_GROUND, STAGE_POST_CUTSCENES)
            returnPlayerFromInstance()
            unhideMinimap()
            camPosResetSoft()
            stop()
            fadeOut()
            wait(2)

            player.musicsManager.playSongAndUnlock(GUNNARSGRUNN_MUSIC_ID)
            player.musicsManager.refreshListConfigs()

            player.lock()
            World.getNPCsInChunkRange(player.chunkId, 3).firstOrNull { it.id == GUDRUN_POST_CUTSCENE }?.let {
                player.interactionManager.setInteraction(StandardEntityInteraction(it, 0) {
                    player.faceEntity(it)
                    it.faceEntityTile(player)
                    GudrunD(player, it)
                })
            }
            wait(1)
            player.unlock()
        }
    }
}
private fun resetAnim(player: Player) {
    player.anim(ANIM_PLAYER_LISTENING)
}
private fun reassureAnim(player: Player) {
    player.anim(ANIM_REASSURE_DORORAN)
}
