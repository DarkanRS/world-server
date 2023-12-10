package com.rs.game.content.world

import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.World
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.handlers.NPCClickHandler
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.overrideNpcLOS

val MUSICIANS = arrayOf(29, 30, 3463, 3509, 3611, 5442, 5439, 8698, 8699, 8700, 8701, 8702, 8703, 8704, 8705, 8706, 8707, 8708, 8709, 8712, 8713, 8715, 8716, 8717, 8718, 8719, 8720, 8721, 8722, 8723, 14628, 14629, 14640)

fun isNearby(p: Player): Boolean {
    val nearbyNPCs = World.getNPCsInChunkRange(p.chunkId, 1)
    for (nearbyNPC in nearbyNPCs) {
        if (nearbyNPC == null)
            continue
        for (musicianId in MUSICIANS)
            if (nearbyNPC.id == musicianId && p.withinDistance(nearbyNPC.middleTile, 2))
                return true
    }
    return false
}

@ServerStartupEvent
fun mapPlugin() {
    overrideNpcLOS(*MUSICIANS)

    onNpcClick(*MUSICIANS) { e ->
        when (e.option.lowercase()) {
            "talk-to" -> {
                e.npc.resetDirection()
                e.player.startConversation {
                    options {
                        option("Who are you?") {
                            npc(e.npcId, HeadE.CALM_TALK, "Me? I'm a musician! Let me help you relax: sit down, rest your weary limbs and allow me to wash away the troubles of the day. After a long trek, what could be better than some music to give you the energy to continue?")
                            npc(e.npcId, HeadE.CALM_TALK, "Did you know music has curative properties? Music stimulates the healing humours in your body, so they say.")
                            player(HeadE.CALM_TALK, "Who says that, then?")
                            npc(e.npcId, HeadE.CALM_TALK, "I was told by a travelling medical practitioner, selling oil extracted from snakes. It's a commonly known fact, so he said. After resting to some music, you will be able to run longer, and your life points will increase noticeably. A panacea, if you will. Ah, the power of music.")
                            player(HeadE.CALM_TALK, "So, just listening to some music will cure me of all my ills?")
                            npc(e.npcId, HeadE.CALM_TALK, "Well, not quite. Poison, lack of faith, and dismembered limbs are all a bit beyond even the most rousing of harmonies, but I guarantee you will feel refreshed, and better equipped to take on the challenges of the day.")
                            player(HeadE.CALM_TALK, "Does this cost me anything?")
                            npc(e.npcId, HeadE.CALM_TALK, "Oh, no! My reward is the pleasure I bring to the masses. Just remember me and tell your friends, and that is payment enough. So sit down and enjoy!")
                        }

                        option("Can I ask you some questions about resting?") {
                            options {
                                option("How does resting work?") {
                                    npc(e.npcId, HeadE.CALM_TALK, "Have you ever been on a long journey, and simply wanted to have a rest? When you’re running from city to city, it’s so easy to run out of breath, don’t you find?")
                                    player(HeadE.CALM_TALK, "Yes, I can never run as far as I’d like.")
                                    npc(e.npcId, HeadE.CALM_TALK, "Well, you may rest anywhere, simply choose the Rest option on the run buttons. When you are nice and relaxed, you will recharge your run energy more quickly and your life points twice as fast as you would do so normally.")
                                    npc(e.npcId, HeadE.CALM_TALK, "Of course, you can’t do anything else while you’re resting, other than talk.")
                                    player(HeadE.CALM_TALK, "Why not?")
                                    npc(e.npcId, HeadE.CALM_TALK, "Well, you wouldn’t be resting, now would you? Also, you should know that resting by a musician, has a similar effect but the benefits are greater.")
                                }

                                option("What's special about resting by a musician?") {
                                    npc(e.npcId, HeadE.CALM_TALK, "The effects of resting are enhanced by music. Your run energy will recharge many times the normal rate, and your life points three times as fast. Simply sit down and rest as you would normally, nice and close to the musician. ")
                                    npc(e.npcId, HeadE.CALM_TALK, "You’ll turn to face the musician and hear the music. Like resting anywhere, if you do anything other than talk, you will stop resting.")
                                }

                                option("Can you summarise the effects for me?") {
                                    npc(e.npcId, HeadE.CALM_TALK, "Certainly. You can rest anywhere, simply choose the Rest option on the run buttons. Resting anywhere will replenish your run energy more quickly than normal, your life points will replenish twice as fast as well! ")
                                    npc(e.npcId, HeadE.CALM_TALK, "Resting by a musician will replenish your run energy many times faster than normal, and your life points will also replenish three times as fast.")
                                }

                                option("That's all for now.")
                            }
                        }

                        option("Can I ask you some questions about running?") {
                            npc(e.npcId, HeadE.CALM_TALK, "Running? Of course! Not that I do much running, I prefer to saunter. But you adventuring types always seem to be in a rush, zipping hither and thither.")
                            player(HeadE.CALM_TALK, "Why do I need to run anyway?")
                            npc(e.npcId, HeadE.CALM_TALK, "Running is the simplest way to get somewhere quickly. When you run you move twice as fast as you normally would. Also, you don’t look like the cowardly type, but most creatures can’t run very fast, so if you don’t want to fight, you can always run away.")
                            player(HeadE.CALM_TALK, "Can I keep running forever?")
                            npc(e.npcId, HeadE.CALM_TALK, "No, eventually you’ll get tired. When that happens you will stop running, and start walking. It takes a while to get your breath back, but once you’ve recovered it a little, you can start running again. You recover quickly whilst resting, or more slowly whilst walking.")
                            npc(e.npcId, HeadE.CALM_TALK, "You may start running by clicking once on the Run button, which is the running man icon at the top-right of the minimal. Clicking the Run button a second time will switch you back to walking. It tells you how much run energy you currently have.")
                        }

                        option("That's all for now.")
                    }
                }
            }

            "listen-to" -> {
                if (e.player.emotesManager.isAnimating) {
                    e.player.sendMessage("You can't rest while performing an emote.")
                    return@onNpcClick
                }
                if (e.player.isLocked) {
                    e.player.sendMessage("You can't rest while performing an action.")
                    return@onNpcClick
                }
                e.player.stopAll()
                e.npc.resetDirection()
                e.player.actionManager.setAction(RestMusician(e.npcId))
            }
        }
    }
}
