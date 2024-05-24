package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.pathfinder.Direction
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

const val DORORAN_CRAFT_TASKS_KEY = "dororanCraftingTasks"
const val SWANKY_BOOTS_KEY = "dororanSwankyBoots"

const val DORORAN = 2648
const val ANIM_CHISEL = 14736
const val ANIM_TAKE_ITEM = 14738
val REPLACEMENT_GUNNARS_GROUND_POEM = Item(19773)
val REPLACEMENT_SWANKY_BOOTS = Item(19776)
val RUBY_RING = Item(1641)
val RUBY_BRACELET = Item(11085)
val DRAGONSTONE_NECKLACE = Item(1664)
val ONYX_AMULET = Item(6581)

@ServerStartupEvent
fun mapDororanTalk() {
    instantiateNpc(DORORAN) { npcId, tile -> Dororan(npcId, tile) }

    onNpcClick(DORORAN) { (player, npc) ->
        mapDororanDialogue(player, npc)
    }
}

class Dororan(id: Int, tile: Tile) : NPC(id, tile) {
    init {
        setRandomWalk(false)
        faceDir(Direction.EAST)
    }
}

fun mapDororanDialogue(player: Player, npc: NPC) {
    val dororanCraftingTasks: Int = player.getI(DORORAN_CRAFT_TASKS_KEY)
    val dororanSwankyBoots: Int = player.getI(SWANKY_BOOTS_KEY)

    player.startConversation {
        if (dororanCraftingTasks < 2)
            npc(npc, HAPPY_TALKING, "Come in, my friend, come in! There is another matter I could use your assistance with.")
        else
            npc(npc, HAPPY_TALKING, "Thanks so much for everything you've done for us!")
        label("initialOptions")
        options {
            if (dororanCraftingTasks < 2)
                op("What is it?") {
                    player(HAPPY_TALKING, "What is it?")
                    exec { mapDororanJewelleryTalk(player, npc, this) }
                }
            op("I want to talk about something else.") {
                label("talkAboutSomethingElse")
                npc(npc, HAPPY_TALKING, "What can I do for you?")
                label("somethingElseOptions")
                options {
                    op("How are things?") {
                        npc(npc, HAPPY_TALKING, "Every morning I wake to sunshine and birdsong! Life is marvellous!")
                        goto("somethingElseOptions")
                    }
                    op("This is a very large house.") {
                        npc(npc, HAPPY_TALKING, "I know! I don't know where Gunthor would have got such a thing. Maybe Gudrun has some idea.")
                        goto("somethingElseOptions")
                    }
                    op("I'd like to see the poem you wrote for Gunthor.") {
                        item(REPLACEMENT_GUNNARS_GROUND_POEM.id, "Dororan gives you a copy of the poem.") {
                            retrieveGunnarsGroundPoem(player)
                        }
                        npc(npc, HAPPY_TALKING, "There you go!")
                    }
                    op("I seem to have mislaid my swanky boots.") {
                        if (dororanSwankyBoots < 12) {
                            npc(npc, CALM_TALK, "Not to worry! There are some left. Here you go.")
                            item(REPLACEMENT_SWANKY_BOOTS.id, "Dororan gives you some more boots.") {
                                retrieveSwankyBoots(player)
                            }
                            npc(npc, CALM_TALK, "Be more careful with these ones! I don't have an infinite supply.")
                        } else {
                            npc(npc, SAD, "You've exhausted my supply of boots!")
                            npc(npc, HAPPY_TALKING, "I could rustle up another pair for you, but I'd have to trade for them.")
                            npc(npc, HAPPY_TALKING, "I have been thinking of giving Gudrun a ruby ring. If you bring me one, I'll trade it for some more boots.")
                            options {
                                if (!player.inventory.containsOneItem(RUBY_RING.id))
                                    op("I don't have one with me.")
                                else
                                    op("Here's a ruby ring.") {
                                        item(REPLACEMENT_SWANKY_BOOTS.id, "You trade Dororan a ruby ring for the boots.") {
                                            player.inventory.removeItems(RUBY_RING)
                                            retrieveSwankyBoots(player)
                                        }
                                    }
                                op("No thanks.")
                            }
                        }
                    }
                    op("Goodbye.")
                }
            }
            op("I don't have time right now.")
        }
    }
}

fun mapDororanJewelleryTalk(player: Player, npc: NPC, dialogue: DialogueBuilder) {
    when (player.getI(DORORAN_CRAFT_TASKS_KEY)) {
        -1 -> {
            mapEngraveRubyBracelet(player, npc, dialogue)
        }
        0 -> {
            mapEngraveDragonstoneNecklace(player, npc, dialogue)
        }
        1 -> {
            mapEngraveOnyxAmulet(player, npc, dialogue)
        }
    }
}

fun mapEngraveRubyBracelet(player: Player, npc: NPC, dialogue: DialogueBuilder) {
    val craftLevel = player.skills.getLevel(Skills.CRAFTING)
    dialogue.npc(npc, HAPPY_TALKING, "I have some more jewellery for Gudrun and I need your help to engrave them.")
    dialogue.options {
        op("What's the first piece?") {
            player(HAPPY_TALKING, "What's the first piece?")
            npc(npc, HAPPY_TALKING, "A magnificent ruby bracelet.")
            npc(npc, HAPPY_TALKING, "'With beauty blessed.'")
            options {
                op("Engrave the bracelet.") {
                    if (craftLevel < 42) {
                        item(RUBY_BRACELET.id, "You need a Crafting level of at least 42 to engrave the ruby bracelet.")
                        npc(npc, SAD, "That's a shame. Maybe you can try again another time.")
                        goto("initialOptions")
                    } else {
                        item(RUBY_BRACELET.id, "You carefully engrave 'With beauty blessed' onto the ruby bracelet.") {
                            completeDororanCraftTask(player, 2000.0)
                            player.sendMessage("You gained 2000 Crafting XP.")
                        }
                        npc(npc, AMAZED, "Magnificent! Outstanding! I will give this to her immediately. Please, come back when you have time.")
                    }
                }
                op("Don't engrave the bracelet.") {
                    npc(npc, SAD, "That's a shame. Maybe you can try again another time.")
                    goto("initialOptions")
                }
            }
        }
    }
}

fun mapEngraveDragonstoneNecklace(player: Player, npc: NPC, dialogue: DialogueBuilder) {
    val craftLevel = player.skills.getLevel(Skills.CRAFTING)
    dialogue.npc(npc, HAPPY_TALKING, "I have another piece of jewellery to engrave.")
    dialogue.options {
        op("What's this one?") {
            player(HAPPY_TALKING, "What's this one?")
            npc(npc, HAPPY_TALKING, "A fine dragonstone necklace.")
            npc(npc, HAPPY_TALKING, "There's not much room...how about just 'Gudrun'?")
            options {
                op("Engrave the necklace.") {
                    if (craftLevel < 72) {
                        item(DRAGONSTONE_NECKLACE.id, "You need a Crafting level of at least 72 to engrave the dragonstone necklace.")
                        npc(npc, SAD, "That's a shame. Maybe you can try again another time.")
                        goto("initialOptions")
                    } else {
                        item(DRAGONSTONE_NECKLACE.id, "You skilfully engrave 'Gudrun' onto the dragonstone necklace.") {
                            completeDororanCraftTask(player, 10000.0)
                            player.sendMessage("You gained 10,000 Crafting XP.")
                        }
                        npc(npc, AMAZED, "Another astonishing piece of work! Please, come back later to see if I have other crafting tasks.")
                    }
                }
                op("Don't engrave the bracelet.") {
                    npc(npc, SAD, "That's a shame. Maybe you can try again another time.")
                    goto("initialOptions")
                }
            }
        }
    }
}

fun mapEngraveOnyxAmulet(player: Player, npc: NPC, dialogue: DialogueBuilder) {
    val craftLevel = player.skills.getLevel(Skills.CRAFTING)
    dialogue.npc(npc, HAPPY_TALKING, "I have one last piece of jewellery to engrave.")
    dialogue.options {
        op("What is it?") {
            player(HAPPY_TALKING, "What is it?")
            npc(npc, HAPPY_TALKING, "An onyx amulet!")
            npc(npc, HAPPY_TALKING, "'The most beautiful girl in the room.'")
            options {
                op("Engrave the necklace.") {
                    if (craftLevel < 90) {
                        item(ONYX_AMULET.id, "You need a Crafting level of at least 90 to engrave the onyx amulet.")
                        npc(npc, SAD, "That's a shame. Maybe you can try again another time.")
                        goto("initialOptions")
                    } else {
                        item(ONYX_AMULET.id, "You expertly engrave 'The most beautiful girl in the room' onto the onyx amulet.") {
                            completeDororanCraftTask(player, 20000.0)
                            player.sendMessage("You gained 20,000 Crafting XP.")
                        }
                        npc(npc, AMAZED, "That's fantastic! Excellent work.")
                    }
                }
                op("Don't engrave the bracelet.") {
                    npc(npc, SAD, "That's a shame. Maybe you can try again another time.")
                    goto("initialOptions")
                }
            }
        }
    }
}

fun completeDororanCraftTask(player: Player, xpToAward: Double) {
    player.schedule {
        player.lock()
        player.anim(ANIM_CHISEL)
        player.skills.addXp(Skills.CRAFTING, xpToAward)
        player.save(DORORAN_CRAFT_TASKS_KEY, (player.getI(DORORAN_CRAFT_TASKS_KEY) + 1))
        wait(4)
        player.unlock()
    }
}
fun retrieveGunnarsGroundPoem(player: Player) {
    player.anim(ANIM_TAKE_ITEM)
    player.inventory.addItem(REPLACEMENT_GUNNARS_GROUND_POEM, true)
}
fun retrieveSwankyBoots(player: Player) {
    player.anim(ANIM_TAKE_ITEM)
    player.inventory.addItem(REPLACEMENT_SWANKY_BOOTS, true)
    if (player.getI(SWANKY_BOOTS_KEY) < 0) player.save(SWANKY_BOOTS_KEY, 1)
    else player.save(SWANKY_BOOTS_KEY, (player.getI(SWANKY_BOOTS_KEY) + 1))
}
