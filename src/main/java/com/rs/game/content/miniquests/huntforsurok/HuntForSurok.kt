package com.rs.game.content.miniquests.huntforsurok

import com.rs.engine.cutscene.Cutscene
import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.miniquest.MiniquestHandler
import com.rs.engine.miniquest.MiniquestOutline
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.content.miniquests.huntforsurok.npcs.AnnaJones
import com.rs.game.content.skills.mining.Pickaxe
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.ObjectClickEvent
import com.rs.plugin.handlers.ObjectClickHandler
import com.rs.plugin.kts.onObjectClick
import java.util.function.Consumer

@MiniquestHandler(
    miniquest = Miniquest.HUNT_FOR_SUROK,
    startText = "Speak to Surok by the statue of Saradomin, east of Varrock.",
    itemsText = "Combat equipment to fight a strong level 107 ork who is resistant to range attacks and survive taking multiple hits from different types of monsters.",
    combatText = "Bork (level 107)",
    rewardsText = "5,000 Slayer XP<br>Ability to slay Bork daily (for 1,500 Slayer XP, <br>Summoning charms, big bones, and gems in the<br>Chaos Tunnels<br>Ability to wear Dagon'hai robes",
    completedStage = 5
)
@PluginEventHandler
class HuntForSurok : MiniquestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        0 -> listOf("I can start this miniquest by speaking to Surok after mining the statue of Saradomin to gain access to the Tunnels of Chaos.")
        1 -> listOf("I confronted Surok outside the statue and he fled into the tunnels beneath.")
        2 -> listOf("I found Surok fleeing from the Tunnels of Chaos and into a portal leading into the Chaos Tunnels.")
        3 -> listOf("I found Surok again near the fire giants but he fled off to the south.")
        4 -> listOf("I found Surok again near the skeletons next to the moss giant room but he got away again fleeing to the west.")
        5 -> listOf("I managed to defeat Bork but Surok managed to escape somewhere unknown.", "MINIQUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.SLAYER, 5000.0)
        sendQuestCompleteInterface(player, 11014)
    }

    override fun updateStage(player: Player) {
        if (player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) >= 1) {
            player.vars.saveVarBit(4312, 2)
            player.vars.saveVarBit(4314, 2)
        }
        if (player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) >= 2) player.vars.saveVarBit(4311, 1)
    }
}

@ServerStartupEvent
fun mapHuntForSurok() {
    onObjectClick(23074) { it.player.useStairs(Tile.of(3284, 3467, 0)) }

    onObjectClick(23096) { e ->
        when (e.option) {
            "Excavate" -> {
                if (e.player.getQuestStage(Quest.WHAT_LIES_BELOW) < 4) {
                    e.player.startConversation {
                        npc(AnnaJones.ID, HeadE.CALM_TALK, "Excuse me. I am working on that statue at the moment. Please don't touch it.")
                        player(HeadE.AMAZED_MILD, "You are? But you're just sitting there.")
                        npc(AnnaJones.ID, HeadE.CALM_TALK, "Yes. I'm on a break.")
                        player(HeadE.CONFUSED, "Oh, I see. When does your break finish?")
                        npc(AnnaJones.ID, HeadE.CALM_TALK, "When I decide to start work again. Right now, I'm enjoying sitting on this bench.")
                    }
                    return@onObjectClick
                }
                if (!e.player.getBool("annaTunnelTalk")) {
                    e.player.startConversation(AnnaJones(e.player))
                    return@onObjectClick
                }
                if (e.player.skills.getLevelForXp(Skills.MINING) < 42) {
                    e.player.simpleDialogue("You need a Mining level of 42 to excavate the statue.")
                    return@onObjectClick
                }
                val pick = Pickaxe.getBest(e.player)
                if (pick == null) {
                    e.player.simpleDialogue("You need a pickaxe to dig out the statue.")
                    return@onObjectClick
                }
                e.player.repeatAction(pick.ticks) {
                    e.player.anim(pick.animId)
                    if (Utils.skillSuccess(e.player.skills.getLevel(Skills.MINING), 16, 100)) {
                        e.player.anim(-1)
                        e.player.vars.saveVarBit(3524, 1)
                        if (e.player.isQuestComplete(Quest.WHAT_LIES_BELOW)) e.player.vars.saveVarBit(4312, 1)
                        e.player.startConversation {
                            npc(AnnaJones.ID, HeadE.CHEERFUL, "You did it! Oh, well done! How exciting!")
                            player(HeadE.CHEERFUL, "Right, well, I better see what's down there, then.")
                        }
                        return@repeatAction false
                    }
                    return@repeatAction true
                }
            }

            "Enter" -> {
                if (e.player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) != 1) {
                    e.player.useStairs(Tile.of(3179, 5191, 0))
                    return@onObjectClick
                }
                e.player.cutscene {
                    endTile = Tile.of(3179, 5191, 0)
                    fadeInAndWait()
                    e.player.setMiniquestStage(Miniquest.HUNT_FOR_SUROK, 2)
                    dynamicRegion(Tile.of(3179, 5191, 0), 393, 649, 5, 5, false)
                    val surok = npcCreate(7002, 21, 24, 0)
                    val aegis = npcCreate(5840, 14, 19, 0)
                    entityTeleTo(player, 21, 18)
                    aegis.faceDir(Direction.EAST)
                    camPos(27, 19, 7712)
                    camLook(15, 16, 0)
                    camPos(26, 25, 3087, 0, 10)
                    fadeOutAndWait()

                    aegis.forceTalk("Goodness me! It's Lord Magis!")
                    entityWalkTo(surok, 15, 16)
                    wait(4)
                    surok.forceTalk("I must escape!")
                    wait(2)
                    surok.spotAnim(110, 0, 96)
                    wait(1)
                    surok.finish()
                    wait(5)

                    fadeInAndWait()
                    camPosResetHard()
                    returnPlayerFromInstance()
                    fadeOutAndWait()
                }
            }
        }
    }
}
