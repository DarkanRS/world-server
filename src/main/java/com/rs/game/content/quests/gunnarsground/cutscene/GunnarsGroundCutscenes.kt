package com.rs.game.content.quests.gunnarsground.cutscene

import com.rs.engine.cutscene.Cutscene
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.gunnarsground.dialogues.GudrunD
import com.rs.game.content.quests.gunnarsground.utils.*
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.interactions.StandardEntityInteraction
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.InterfaceManager
import com.rs.lib.game.Animation
import java.util.stream.Stream

class GunnarsGroundCutscenes : Cutscene() {
    override fun construct(player: Player) {

        // Listening Cutscene
        fadeInBG(1)
        hideMinimap()
        delay(0)
        action { lowerAspectRatio() }
        delay(0)
        player.interfaceManager.removeSubs(*InterfaceManager.Sub.ALL_GAME_TABS)
        dynamicRegion(player.tile, 382, 427, 8, 8)
        delay(0)
        npcCreate("Dororan", STANDING_DORORAN, 23, 19, 0)
        npcFaceDir("Dororan", Direction.NORTH)
        npcAnim("Dororan", 14725)
        playerMove(22, 19, 0, Entity.MoveType.TELE)
        delay(0)
        playerFaceDir(Direction.NORTH)
        delay(0)
        playerAnim(Animation(14724))
        camPos(23, 12, 1300)
        camLook(22, 33, 0)
        camPos(23, 14, 1200, 1, 1)
        fadeOutBG(0)

        delay(1)
        dialogue(Dialogue().addNPC(STANDING_DORORAN, CALM_TALK, "How long have they been in there?"), true)
        delay(2)

        // Speech Cutscene
        fadeInBG(1)
        delay(0)
        npcDestroy("Dororan")
        delay(0)
        npcCreate("ChieftainGunthor", CHIEFTAIN_GUNTHOR, 23, 9, 0)
        npcCreate("Haakon", HAAKON, 22, 9, 0)
        npcCreate("Kjell", KJELL, 21, 10, 0)
        npcCreate("Gudrun", GUDRUN_QUESTING, 24, 10, 0)
        npcCreate("Dororan", STANDING_DORORAN, 26, 12, 0)
        playerMove(27, 9, 0, Entity.MoveType.TELE)
        delay(0)
        playerAnim(Animation(-1))
        action {
            Stream.of("ChieftainGunthor", "Haakon", "Kjell", "Gudrun", "Dororan").forEach { label: String ->
                getNPC(label).setRandomWalk(false)
                getNPC(label).faceSouth()
            }
        }
        delay(0)
        playerFaceDir(Direction.WEST)
        delay(0)
        camPos(23, 3, 1200)
        camLook(23, 11, 100)
        camPos(23, 3, 1400, 1, 0)
        delay(0)
        npcAnim("ChieftainGunthor", ANIM_CHIEFTAIN_SPEECH)
        fadeOutBG(0)

        dialogue(
            Dialogue().addNPC(CHIEFTAIN_GUNTHOR, ANGRY, "Freemen! Freemen! I have an announcement!")
                .addNPC(KJELL, ANGRY, "Hear the chieftain speak! Hear him!")
                .addNPC(CHIEFTAIN_GUNTHOR, ANGRY, "We have always borne the legacy of our ancestors, and we have borne it with honour!")
                .addNPC(KJELL, ANGRY, "FOR GUNNAR!")
                .addNPC(CHIEFTAIN_GUNTHOR, ANGRY, "And though we honour them still, the time of our ancestors is past. This is the time of Gunthor!")
                .addNPC(HAAKON, ANGRY, "FOR GUNTHOR!")
                .addNPC(CHIEFTAIN_GUNTHOR, ANGRY, "Gunthor says: This is Gunnar's ground, bought with blood! Let it remain Gunnar's ground forever! Here we settle!")
                .addNPC(CHIEFTAIN_GUNTHOR, ANGRY, "GUNNAR'S GROUND!")
                .addNPC(HAAKON, ANGRY, "GUNNAR'S GROUND!"), true
        )
        npcAnim("Kjell", ANIM_KJELL_CHEER)
        npcAnim("Haakon", ANIM_HAAKON_CHEER)
        delay(2)

        // Hug Cutscene
        fadeInBG(0)
        dynamicRegion(player.tile, 382, 427, 8, 8)
        delay(1)
        npcDestroy("Dororan")
        npcDestroy("ChieftainGunthor")
        npcDestroy("Kjell")
        npcDestroy("Haakon")
        npcDestroy("Gudrun")
        delay(0)
        spawnObj(5657, 1, 26, 9, 0)
        playerMove(27, 10, 0, Entity.MoveType.TELE)
        delay(0)
        playerFaceTile(25, 9)
        delay(0)
        camPos(28, 5, 1200)
        camLook(25, 11, 100)
        fadeOutBG(0)

        dialogue(
            Dialogue()
                .addNPC(GUDRUN_QUESTING, AMAZED, "That was brilliant! I must know who wrote that poem.")
                .addNPC(DORORAN_QUESTING, CALM_TALK, "Um, that would be me. Hello.")
                .addNPC(GUDRUN_QUESTING, AMAZED, "That line about beauty was for me, wasn't it?")
                .addNPC(DORORAN_QUESTING, AMAZED, "Uh, yes.")
                .addNPC(GUDRUN_QUESTING, AMAZED, "You're the mystery poet who sent me the gold ring!")
                .addNPC(DORORAN_QUESTING, SAD, "Sorry.")
                .addNPC(GUDRUN_QUESTING, HAPPY_TALKING, "I have no idea dwarves could be so romantic! Come here!"), true
        )
        delay(2)

        fadeInBG(2)
        action {
            restoreDefaultAspectRatio()
            player.interfaceManager.closeInterfacesOverGameWindow()
            player.interfaceManager.sendSubDefaults(*InterfaceManager.Sub.ALL_GAME_TABS)
            player.questManager.setStage(Quest.GUNNARS_GROUND, STAGE_POST_CUTSCENES)
            player.musicsManager.unlockMusic(896)
        }
        returnPlayerFromInstance()
        fadeOutBG(1)

        action {
            player.lock()
            World.getNPCsInChunkRange(player.chunkId, 3).firstOrNull { it.id == GUDRUN_POST_CUTSCENE }?.let {
                player.interactionManager.setInteraction(StandardEntityInteraction(it, 0) {
                    player.faceEntity(it)
                    it.faceEntity(player)
                    GudrunD(player, it)
                    delay(1)
                    player.unlock()
                })
            }
        }
    }
}

        /*action {
            player.startConversation {
                npc(DORORAN, HAPPY_TALKING, "How long have they been in there?")
                options {
                    op("They're just starting.") {
                        player(HAPPY_TALKING, "They're just starting.") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                        goto("goingToWork")
                    }
                    op("You're late.") {
                        player(FRUSTRATED, "You're late.")
                        goto("goingToWork")
                    }
                }
                label("goingToWork")
                npc(DORORAN, WORRIED, "This isn't going to work.")
                options {
                    op("Why's that?") {
                        player(CONFUSED, "Why's that?") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                        goto("whatWasIThinking")
                    }
                    op("You're so pessimistic.") {
                        player(FRUSTRATED, "You're so pessimistic.") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                        goto("whatWasIThinking")
                    }
                }
                label("whatWasIThinking")
                npc(DORORAN, MORTIFIED, "What was I thinking? You should go in there and stop them before Gudrun makes a fool of herself.")
                options {
                    op("Okay, I will.") {
                        player(CONFUSED, "Okay, I will.") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                        npc(DORORAN, TERRIFIED, "No! Wait, stay here, it's too late now. We'll just have to see how it turns out.")
                        goto("hearWhatsHappening")
                    }
                    op("Don't be silly.") {
                        player(FRUSTRATED, "Don't be silly.") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                        npc(DORORAN, WORRIED, "You're right, it's too late now. We'll just have to see how it turns out.")
                        goto("hearWhatsHappening")
                    }
                }
                label("hearWhatsHappening")
                npc(DORORAN, SKEPTICAL_THINKING, "I can't hear what's happening. Can you hear what's happening?")
                player(CALM_TALK, "Gunthor is laughing at something.")
                npc(DORORAN, WORRIED, "He's probably considering the various tortures he has planned for me.")
                options {
                    op("Why would he do that?") {
                        player(CONFUSED, "Why would he do that?") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                        npc(DORORAN, TERRIFIED, "No! Wait, stay here, it's too late now. We'll just have to see how it turns out.")
                        goto("nowsYourChanceOptions")
                    }
                    op("Now you're just being ridiculous.") {
                        player(FRUSTRATED, "Now you're just being ridiculous.") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                        npc(DORORAN, WORRIED, "The poem says you can honour your ancestors by settling peacefully on the land they conquered. He'll probably find it insulting.")
                        goto("nowsYourChanceOptions")
                    }
                }
                label("nowsYourChanceOptions")
                options {
                    op("Now's your chance to find out.") {
                        player(CONFUSED, "Now's your chance to find out.") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                    }
                    op("You're doomed.") {
                        player(FRUSTRATED, "You're doomed.") {
                            playerAnim(REASSURE_DORORAN_ANIM)
                        }
                    }
                }
            }
        }*/
