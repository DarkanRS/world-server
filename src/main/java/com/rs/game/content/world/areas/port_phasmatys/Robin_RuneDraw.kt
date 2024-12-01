package com.rs.game.content.world.areas.port_phasmatys

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.achievements.AchievementSystemD
import com.rs.game.content.achievements.SetReward
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onNpcClick
import kotlin.random.Random

class MutableInt(var value: Int)

class GameState(
    val runePool: MutableList<Rune>,
    val playerSlots: MutableMap<Int, Rune>,
    val opponentSlots: MutableMap<Int, Rune>,
    val playerScore: MutableInt,
    val opponentScore: MutableInt,
    var gameOver: Boolean = false
)

@ServerStartupEvent
fun RuneDraw() {
    onNpcClick(1694) { event ->
        val gameState = GameState(
            runePool = Rune.entries.toMutableList(),
            playerSlots = mutableMapOf(),
            opponentSlots = mutableMapOf(),
            playerScore = MutableInt(0),
            opponentScore = MutableInt(0)
        )
        event.player.tempAttribs.setO<GameState>("RuneDrawGameState", gameState)
        event.player.startConversation {
            options {
                op("About the Achievement System...") {
                    exec { AchievementSystemD(event.player, event.npcId, SetReward.MORYTANIA_LEGS) }
                }
                op("Do you want another game of Runedraw?") {
                    player(HeadE.HAPPY_TALKING, "Do you want a game of Runedraw?")
                    npc(event.npcId, HeadE.HAPPY_TALKING, "Certainly, I bet you 10gp you can't beat me!")
                    options {
                        op("Bet Robin 10gp.") {
                            exec {
                                if (!event.player.inventory.hasCoins(10)) {
                                    event.player.startConversation {
                                        player(HeadE.SHAKING_HEAD, "Sorry, I don't have enough money.")
                                        npc(event.npcId, HeadE.LAUGH, "Well, you shouldn't have gambled it all then!")
                                    }
                                    return@exec
                                }
                                event.player.inventory.removeCoins(10)
                                event.player.packets.setIFText(9, 2, "Rune-Draw")
                                event.player.packets.setIFText(9, 3, "Your Score")
                                event.player.packets.setIFText(9, 4, "Opponent's Score")
                                event.player.packets.setIFText(9, 5, "Draw")
                                event.player.packets.setIFText(9, 6, "Hold")
                                event.player.packets.setIFText(9, 29, "")
                                event.player.packets.setIFText(9, 31, "")
                                event.player.packets.setIFHidden(9, 30, true)
                                event.player.packets.setIFHidden(9, 32, true)
                                event.player.interfaceManager.sendInterface(9)
                            }
                        }
                        op("Nevermind.") {
                            player(HeadE.CALM_TALK, "Actually, I'd best save my money.")
                            npc(
                                event.npcId,
                                HeadE.CALM_TALK,
                                "Well, you know where to find me if you change your mind!"
                            )
                        }
                    }
                }
            }
        }
    }

    onButtonClick(9) { (player, _, componentId) ->
        val gameState = player.tempAttribs.getO<GameState>("RuneDrawGameState")
            ?: return@onButtonClick

        when (componentId) {
            5 -> {
                if (gameState.gameOver) return@onButtonClick
                player.packets.setIFHidden(9, 5, true)
                player.packets.setIFHidden(9, 6, true)
                drawRuneForPlayer(player, gameState) {
                    if (!gameState.gameOver) {
                        drawRuneForOpponent(player, gameState) {
                            if (!gameState.gameOver) {
                                player.packets.setIFHidden(9, 5, false)
                                player.packets.setIFHidden(9, 6, false)
                            }
                        }
                    }
                }
            }
            6 -> {
                if (gameState.gameOver) return@onButtonClick
                player.packets.setIFHidden(9, 5, true)
                player.packets.setIFHidden(9, 6, true)
                opponentDrawsUntilWinOrDeath(player, gameState)
            }
        }
    }
}

fun drawRuneForPlayer(player: Player, gameState: GameState, onComplete: () -> Unit) {
    val playerSlots = listOf(7, 20, 23, 22, 26, 24, 27, 25, 21, 28)
    assignRuneToNextFreeSlot(player, playerSlots, gameState.runePool, gameState.playerSlots) { rune ->
        gameState.playerScore.value += rune.points
        player.packets.setIFText(9, 29, gameState.playerScore.value.toString())
        gameState.playerSlots.forEach { (slotId, slotRune) ->
            player.packets.setIFHidden(9, slotId, false)
            player.packets.setIFItem(9, slotId, slotRune.id, 1)
        }
        if (rune == Rune.DEATH_RUNE) {
            player.packets.setIFText(9, 29, "DEATH")
            player.packets.setIFHidden(9, 32, false)
            player.packets.setIFText(9, 32, "Opponent Wins!")
            player.sendMessage("You loose and Robin keeps your 10gp")
            endGame(player, gameState)
        }
        onComplete()
    }
}

fun drawRuneForOpponent(player: Player, gameState: GameState, onComplete: () -> Unit) {
    val opponentSlots = listOf(9, 17, 10, 15, 14, 16, 13, 12, 11, 18)
    player.tasks.scheduleTimer(0, 0) { tick: Int? ->
        when (tick) {
            1 -> {
                assignRuneToNextFreeSlot(player, opponentSlots, gameState.runePool, gameState.opponentSlots) { rune ->
                    gameState.opponentScore.value += rune.points
                    player.packets.setIFText(9, 31, gameState.opponentScore.value.toString())
                    gameState.opponentSlots.forEach { (slotId, slotRune) ->
                        player.packets.setIFHidden(9, slotId, false)
                        player.packets.setIFItem(9, slotId, slotRune.id, 1)
                    }
                    if (rune == Rune.DEATH_RUNE) {
                        player.packets.setIFText(9, 31, "DEATH")
                        player.packets.setIFHidden(9, 30, false)
                        player.packets.setIFText(9, 30, "You Win!")
                        player.inventory.addCoins(20)
                        player.sendMessage("You win and collect 20gp from Robin")
                        endGame(player, gameState)
                    }
                    if (!gameState.gameOver) {
                        player.packets.setIFHidden(9, 5, false)
                        player.packets.setIFHidden(9, 6, false)
                    }
                    onComplete()
                }
                return@scheduleTimer false
            }
        }
        true
    }
}

fun opponentDrawsUntilWinOrDeath(player: Player, gameState: GameState) {
    if (gameState.gameOver) return
    if (gameState.opponentScore.value > gameState.playerScore.value) {
        player.packets.setIFHidden(9, 32, false)
        player.packets.setIFText(9, 32, "Opponent Wins!")
        endGame(player, gameState)
        return
    }
    drawRuneForOpponent(player, gameState) {
        if (gameState.gameOver) return@drawRuneForOpponent
        if (gameState.opponentScore.value <= gameState.playerScore.value && gameState.runePool.isNotEmpty()) {
            opponentDrawsUntilWinOrDeath(player, gameState)
        } else if (gameState.opponentScore.value > gameState.playerScore.value) {
            player.packets.setIFHidden(9, 32, false)
            player.packets.setIFText(9, 32, "Opponent Wins!")
            player.sendMessage("You loose and Robin keeps your 10gp")
            endGame(player, gameState)
        }
    }
}

fun assignRuneToNextFreeSlot(
    player: Player,
    slots: List<Int>,
    runePool: MutableList<Rune>,
    slotMap: MutableMap<Int, Rune>,
    onRuneAssigned: (Rune) -> Unit
) {
    for (slotId in slots) {
        if (!slotMap.containsKey(slotId)) {
            val rune = runePool.removeAt(Random.nextInt(runePool.size))
            slotMap[slotId] = rune
            onRuneAssigned(rune)
            break
        }
    }
}

fun endGame(player: Player, gameState: GameState) {
    gameState.gameOver = true
    player.packets.setIFHidden(9, 5, true)
    player.packets.setIFHidden(9, 6, true)
}

enum class Rune(val id: Int, val itemName: String, val points: Int) {
    AIR_RUNE(556, "Air rune", 1),
    MIND_RUNE(558, "Mind rune", 2),
    WATER_RUNE(555, "Water rune", 3),
    EARTH_RUNE(557, "Earth rune", 4),
    FIRE_RUNE(554, "Fire rune", 5),
    BODY_RUNE(559, "Body rune", 6),
    COSMIC_RUNE(564, "Cosmic rune", 7),
    CHAOS_RUNE(562, "Chaos rune", 8),
    NATURE_RUNE(561, "Nature rune", 9),
    DEATH_RUNE(560, "Death rune", 0),
}
