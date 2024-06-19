package com.rs.game.content.quests.troll_stronghold.utils

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile

class TrollStrongholdUtils(val player: Player) {

    val stage = player.questManager.getStage(Quest.TROLL_STRONGHOLD)

    fun handleArenaDoors(obj: GameObject) {
        val dad: NPC? = World.getNPCsInChunkRange(player.chunkId, 2).firstOrNull { it.id == DAD }
        val spectators = World.getNPCsInChunkRange(player.chunkId, 2).filter { it.id in SPECTATORS }

        if (player.x == 2912) {

            when (stage) {

                STAGE_UNSTARTED -> {
                    player.sendMessage("You need to start the Troll Stronghold quest before passing through here.")
                }

                in STAGE_ACCEPTED_QUEST..STAGE_ENTERED_ARENA -> {
                    if (dad != null) {
                        if (dad.combatTarget != null) {
                            player.sendMessage("Someone else is currently fighting Dad right now.")
                            return
                        }
                        player.cutscene {
                            Doors.handleDoubleDoor(player, obj)
                            wait(0)
                            dialogue {
                                npc(1125, T_ANGRY, "No human pass through arena without defeating Dad!")
                            }
                            waitForDialogue()
                            endTile = player.tile
                            returnPlayerFromInstance()
                            if (stage < STAGE_ENTERED_ARENA) player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_ENTERED_ARENA)
                        }
                    } else {
                        player.sendMessage("Someone has very recently gone through here. I should wait a moment or so...")
                    }
                }

                STAGE_ENGAGED_DAD -> {
                    if (dad != null) {
                        if (dad.combatTarget != null) {
                            player.sendMessage("Someone else is currently fighting Dad right now.")
                            return
                        }
                        player.cutscene {
                            Doors.handleDoubleDoor(player, obj)
                            wait(0)
                            dialogue {
                                npc(1125, T_ANGRY, "No human pass through arena without defeating Dad!")
                            }
                            waitForDialogue()
                            endTile = player.tile
                            returnPlayerFromInstance()
                            player.hintIconsManager.addHintIcon(dad, 0, -1, false)
                            dad.combatTarget = player
                        }
                    } else {
                        player.sendMessage("Someone has very recently gone through here. I should wait a moment or so...")
                    }
                }

                else -> Doors.handleDoubleDoor(player, obj)

            }
        } else if (player.x == 2913) {
            if (dad != null) {
                player.hintIconsManager.removeUnsavedHintIcon()
                if (dad.combatTarget == player) {
                    dad.combatTarget = null
                    dad.reset()
                    dad.actionManager.forceStop()
                    dad.hitpoints = dad.maxHitpoints
                    spectators.forEach { spectator ->
                        if (spectator.combatTarget == player) {
                            spectator.combatTarget = null
                            spectator.setForceMultiArea(false)
                        }
                    }
                }
            }
            player.setForceMultiArea(false)
            Doors.handleDoubleDoor(player, obj)

        } else if (player.y == 3618 || player.y == 3619){

            if (stage >= STAGE_FINISHED_DAD) {
                Doors.handleDoubleDoor(player, obj)
                if (stage == STAGE_FINISHED_DAD) {
                    player.hintIconsManager.removeUnsavedHintIcon()
                    player.setForceMultiArea(false)
                    spectators.forEach { spectator ->
                        if (spectator.combatTarget == player) {
                            spectator.combatTarget = null
                            spectator.setForceMultiArea(false)
                        }
                    }
                }
            } else if (stage < STAGE_ENTERED_ARENA) {
                player.sendMessage("You need to start the Troll Stronghold quest before passing through here.")
            } else {
                player.cutscene {
                    dialogue {
                        npc(1125, T_ANGRY, "No human pass through arena without defeating Dad!")
                    }
                    waitForDialogue()
                    endTile = player.tile
                    returnPlayerFromInstance()
                }
            }

        }
    }

    private fun handlePrisonDoor(obj: GameObject) {
        val door = World.getObjectWithId(Tile.of(2848, 10107, 1), obj.id)
        if (door != null) {
            World.spawnObjectTemporary(GameObject(door, 83), 2, true)
            World.spawnObjectTemporary(GameObject(door.id + 1, door.type, door.getRotation(1), door.tile.transform(-1, 0, 0)), 2, true)
            var toTile: Tile = obj.tile
            toTile = when (obj.rotation) {
                0 -> toTile.transform(if (player.x < obj.x) 0 else -1, 0, 0)
                1 -> toTile.transform(0, if (player.y > obj.y) 0 else 1, 0)
                2 -> toTile.transform(if (player.x > obj.x) 0 else 1, 0, 0)
                3 -> toTile.transform(0, if (player.y < obj.y) 0 else -1, 0)
                else -> toTile
            }
            player.addWalkSteps(toTile, 3, false)
        }
    }

    private fun handleCellDoor(obj: GameObject) {
        val door = World.getObjectWithId(Tile.of(obj.tile), obj.id)
        if (door != null) {
            World.spawnObjectTemporary(GameObject(door, 83), 2, true)
            World.spawnObjectTemporary(GameObject(door.id + 1, door.type, door.getRotation(1), door.tile.transform(-1, 0, 0)), 2, true)
        }
    }

    fun handleUnlockPrisonDoor(obj: GameObject) {
        if (stage == STAGE_FINISHED_DAD) {
            if (player.inventory.containsOneItem(PRISON_KEY)) {
                player.inventory.deleteItem(PRISON_KEY, 1)
                handlePrisonDoor(obj)
                player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_UNLOCKED_PRISON_DOOR)
                player.sendMessage("You unlock the prison door.")
            } else {
                player.sendMessage("The prison door is locked.")
            }
        } else {
            handlePrisonDoor(obj)
        }
    }

    private fun Player.bothCellsUnlocked(): Boolean {
        return questManager.getAttribs(Quest.TROLL_STRONGHOLD).run {
            getB(UNLOCKED_EADGAR_CELL) && getB(UNLOCKED_GODRIC_CELL)
        }
    }

    fun handleUnlockGodricDoor(obj: GameObject) {
        if (!player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).getB(UNLOCKED_GODRIC_CELL)) {
            if (player.inventory.containsOneItem(CELL_KEY_GODRIC)) {
                val godric: NPC? = World.getNPCsInChunkRange(player.chunkId, 2).firstOrNull { it.id == GODRIC }
                if (godric != null && isInCell(godric.tile)) {
                    player.schedule {
                        player.walkToAndExecute(Tile.of(GODRIC_UNLOCK_CELL_TILE)) {
                            player.faceDir(Direction.WEST)
                            if (!godric.isForceWalking) {
                                player.lock()
                                player.sendMessage("You unlock the cell door.")
                                player.inventory.deleteItem(CELL_KEY_GODRIC, 1)
                                godric.setRandomWalk(false)
                                godric.setForceWalk(Tile.of(GODRIC_UNLOCK_CELL_TILE.transform(-2, 0)))
                                godric.walkToAndExecute(Tile.of(GODRIC_UNLOCK_CELL_TILE.transform(-2, 0))) {
                                    handleCellDoor(obj)
                                    godric.addWalkSteps(GODRIC_UNLOCK_CELL_TILE.x - 1, GODRIC_UNLOCK_CELL_TILE.y, 3, false, true)
                                    player.cutscene {
                                        dialogue {
                                            npc(godric, HAPPY_TALKING, "Thank you my friend.")
                                        }
                                        waitForDialogue()
                                        player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).setB(UNLOCKED_GODRIC_CELL, true)
                                        if (player.bothCellsUnlocked())
                                            player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_UNLOCKED_BOTH_CELLS)
                                        player.unlock()
                                        godric.walkToAndExecute(GODRIC_ESCAPE_ROUTE_TILE) {
                                            godric.walkToAndExecute(SHARED_ESCAPE_ROUTE_TILE) {
                                                godric.schedule {
                                                    wait(1)
                                                    godric.faceDir(Direction.SOUTH)
                                                    wait(1)
                                                    godric.tele(SHARED_DEATH_TELE_TILE)
                                                    wait(2)
                                                    godric.sendDeath(godric)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                player.sendMessage("Eadgar is busy right now.")
                            }
                        }
                    }
                } else {
                    player.sendMessage("Godric is busy right now.")
                }
            } else {
                player.sendMessage("The cell door is locked.")
            }
        } else {
            player.sendMessage("You've already rescued Godric.")
        }
    }

    fun handleUnlockEadgarDoor(obj: GameObject) {
        if (!player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).getB(UNLOCKED_EADGAR_CELL)) {
            if (player.inventory.containsOneItem(CELL_KEY_EADGAR)) {
                val eadgar: NPC? = World.getNPCsInChunkRange(player.chunkId, 2).firstOrNull { it.id == EADGAR }
                if (eadgar != null && isInCell(eadgar.tile)) {
                    player.schedule {
                        player.walkToAndExecute(Tile.of(EADGAR_UNLOCK_CELL_TILE)) {
                            player.faceDir(Direction.WEST)
                            if (!eadgar.isForceWalking) {
                                player.lock()
                                player.sendMessage("You unlock the cell door.")
                                player.inventory.deleteItem(CELL_KEY_EADGAR, 1)
                                eadgar.setRandomWalk(false)
                                eadgar.setForceWalk(Tile.of(EADGAR_UNLOCK_CELL_TILE.transform(-2, 0)))
                                eadgar.walkToAndExecute(Tile.of(EADGAR_UNLOCK_CELL_TILE.transform(-2, 0))) {
                                    handleCellDoor(obj)
                                    eadgar.addWalkSteps(EADGAR_UNLOCK_CELL_TILE.x - 1, EADGAR_UNLOCK_CELL_TILE.y, 3, false, true)
                                    player.cutscene {
                                        dialogue {
                                            npc(eadgar, HAPPY_TALKING, "Thanks! I'm off back home!")
                                        }
                                        waitForDialogue()
                                        player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).setB(UNLOCKED_EADGAR_CELL, true)
                                        if (player.bothCellsUnlocked())
                                            player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_UNLOCKED_BOTH_CELLS)
                                        player.unlock()
                                        eadgar.walkToAndExecute(EADGAR_ESCAPE_ROUTE_TILE) {
                                            eadgar.walkToAndExecute(SHARED_ESCAPE_ROUTE_TILE) {
                                                eadgar.schedule {
                                                    wait(1)
                                                    eadgar.faceDir(Direction.SOUTH)
                                                    wait(1)
                                                    eadgar.tele(SHARED_DEATH_TELE_TILE)
                                                    wait(2)
                                                    eadgar.sendDeath(eadgar)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                player.sendMessage("Eadgar is busy right now.")
                            }
                        }
                    }
                } else {
                    player.sendMessage("Eadgar is busy right now.")
                }
            } else {
                player.sendMessage("The cell door is locked.")
            }
        } else {
            player.sendMessage("You've already rescued Eadgar.")
        }
    }

    fun returnLostLamps(amount: Int) {
        player.inventory.addItem(com.rs.game.content.quests.troll_stronghold.utils.QUEST_REWARD_LAMP, amount)
        val updatedAmount = player.getI(TROLL_STRONGHOLD_QUEST_LAMPS_LOST) - amount
        if (updatedAmount == 0) player.delete(TROLL_STRONGHOLD_QUEST_LAMPS_LOST)
        else player.save(TROLL_STRONGHOLD_QUEST_LAMPS_LOST, updatedAmount)
    }

    fun isInCell(tile: Tile): Boolean {
        for (range in EADGAR_AND_GODRIC_CELL_CHECK) if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) return true
        return false
    }

}
