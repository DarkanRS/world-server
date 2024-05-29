// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.holidayevents.easter.easter21

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.managers.EmotesManager
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*
import com.rs.utils.spawns.NPCSpawn
import com.rs.utils.spawns.NPCSpawns
import com.rs.utils.spawns.ObjectSpawn
import com.rs.utils.spawns.ObjectSpawns

private const val STAGE_KEY = "easter2024"
private const val ENABLED = false

private const val COG = 14719
private const val PISTON = 14720
private const val CHIMNEY = 14718

private const val CHARLIE = 9686
private const val EASTER_BUNNY = 9687
private const val EASTER_BUNNY_JR = 7411

private val COG_LOCATIONS = arrayOf(
    Tile.of(2469, 5328, 0),
    Tile.of(2469, 5321, 0),
    Tile.of(2454, 5334, 0),
    Tile.of(2448, 5341, 0)
)

private val PISTON_LOCATIONS = arrayOf(
    Tile.of(2468, 5324, 0),
    Tile.of(2467, 5319, 0),
    Tile.of(2454, 5335, 0)
)

private val CHIMNEY_LOCATIONS = arrayOf(
    Tile.of(2469, 5323, 0),
    Tile.of(2444, 5329, 0),
    Tile.of(2449, 5343, 0)
)

@ServerStartupEvent
fun mapEaster2021() {
    onObjectClick(23117, 30074) { e ->
        if (e.player.getI(STAGE_KEY) <= 0) {
            e.player.sendMessage("You don't see a need to go down there yet!")
            return@onObjectClick
        }
        e.player.useLadder(if (e.objectId == 23117) Tile.of(2483, 5258, 0) else Tile.of(3212, 3425, 0))
    }

    onObjectClick(30075, 30076) { e ->
        useBunnyHole(e.player, e.getObject(), e.player.transform(0, if (e.objectId == 30075) 7 else -7))
    }

    onItemClick(12645, options = arrayOf("Emote")) { e ->
        e.player.sync(8903, 1566)
    }

    onItemEquip(4565) { e ->
        e.player.appearance.setBAS(if (e.dequip()) -1 else 594)
    }

    onLogin { e ->
        if (!ENABLED) return@onLogin
        e.player.nsv.setI("easterBirdFood", Utils.random(4))
        e.player.nsv.setI("cogLocation", Utils.random(COG_LOCATIONS.size))
        e.player.nsv.setI("pistonLocation", Utils.random(PISTON_LOCATIONS.size))
        e.player.nsv.setI("chimneyLocation", Utils.random(CHIMNEY_LOCATIONS.size))
        e.player.vars.setVarBit(6014, if (e.player.getI(STAGE_KEY) >= 3) 1 else 0)
        e.player.vars.setVarBit(6016, if (e.player.getI(STAGE_KEY) >= 6) 3 else 0)
        if (e.player.getI(STAGE_KEY) >= 8) e.player.vars.setVarBit(6014, 85)
    }

    onObjectClick(30083) { e -> e.player.inventory.addItem(1929) }

    onObjectClick(30089, 30090, 30091, 30092) { e ->
        if (!e.player.inventory.hasFreeSlots()) {
            e.player.sendMessage("You don't have enough inventory space.")
            return@onObjectClick
        }
        val food = Item(14714 + (e.objectId - 30089))
        e.player.sendMessage("You grab some " + food.name + ".")
        e.player.inventory.addItem(food)
    }

    onItemOnObject(objectNamesOrIds = arrayOf(42731), itemNamesOrIds = arrayOf(1929)) { e ->
        if (e.player.getI(STAGE_KEY) >= 3) {
            e.player.sendMessage("You've already woken the bird up! It doesn't need any more water.")
            return@onItemOnObject
        }
        e.player.inventory.deleteItem(1929, 1)
        e.player.inventory.addItem(1925, 1)
        e.player.vars.setVarBit(6027, 1)
        e.player.sendMessage("You fill the bird's dish with water.")
        if (e.player.vars.getVarBit(6026) == e.player.nsv.getI("easterBirdFood")) {
            e.player.sendMessage("The bird wakes up and begins eating and drinking!")
            e.player.save(STAGE_KEY, 3)
            e.player.vars.setVarBit(6014, 1)
            WorldTasks.delay(10) {
                e.player.vars.setVarBit(6026, 0)
                e.player.vars.setVarBit(6027, 0)
            }
        } else e.player.sendMessage("The bird still needs the correct food it seems.")
    }

    onItemOnObject(objectNamesOrIds = arrayOf(42732), itemNamesOrIds = arrayOf(14714, 14715, 14716, 14717)) { e ->
        if (e.player.getI(STAGE_KEY) >= 3) {
            e.player.sendMessage("You've already woken the bird up! It doesn't need any more food.")
            return@onItemOnObject
        }
        val foodId = e.item.id - 14713
        e.player.inventory.deleteItem(e.item.id, 1)
        e.player.vars.setVarBit(6026, foodId)
        e.player.sendMessage("You fill the bird's dish with " + e.item.name + ".")
        if (e.player.vars.getVarBit(6027) == 0) {
            e.player.sendMessage("The bird still looks thirsty.")
            return@onItemOnObject
        }
        if (e.player.vars.getVarBit(6026) == (e.player.nsv.getI("easterBirdFood") + 1)) {
            e.player.save(STAGE_KEY, 3)
            e.player.vars.setVarBit(6014, 1)
            WorldTasks.delay(10) {
                e.player.vars.setVarBit(6026, 0)
                e.player.vars.setVarBit(6027, 0)
            }
        } else e.player.sendMessage("That doesn't seem to be the correct food." + e.player.nsv.getI("easterBirdFood"))
    }

    onObjectClick(30100, 30101, 30102, 30103, 30104) { e ->
        if (e.player.getI(STAGE_KEY) < 5) {
            e.player.sendMessage("You don't find anything that looks useful to you right now.")
            return@onObjectClick
        }
        if (COG_LOCATIONS[e.player.nsv.getI("cogLocation")].matches(e.getObject().tile) && !e.player.inventory.containsItem(COG)) {
            e.player.inventory.addItem(COG)
            e.player.startConversation(Conversation(e.player).addItem(COG, "You find a cog in the crate!"))
            return@onObjectClick
        }
        if (PISTON_LOCATIONS[e.player.nsv.getI("pistonLocation")].matches(e.getObject().tile) && !e.player.inventory.containsItem(PISTON)) {
            e.player.inventory.addItem(PISTON)
            e.player.startConversation(Conversation(e.player).addItem(PISTON, "You find some pistons in the crate!"))
            return@onObjectClick
        }
        if (CHIMNEY_LOCATIONS[e.player.nsv.getI("chimneyLocation")].matches(e.getObject().tile) && !e.player.inventory.containsItem(CHIMNEY)) {
            e.player.inventory.addItem(CHIMNEY)
            e.player.startConversation(Conversation(e.player).addItem(CHIMNEY, "You find a chimney in the crate!"))
            return@onObjectClick
        }
        e.player.sendMessage("You find nothing interesting.")
    }

    onItemOnObject(objectNamesOrIds = arrayOf(42733), itemNamesOrIds = arrayOf(COG, PISTON, CHIMNEY)) { e ->
        if (e.player.getI(STAGE_KEY) < 5) {
            e.player.sendMessage("It looks really broken.")
            return@onItemOnObject
        }
        when (e.item.id) {
            COG -> if (e.player.vars.getVarBit(6016) <= 0) {
                e.player.inventory.deleteItem(e.item)
                e.player.vars.setVarBit(6016, 1)
                e.player.sendMessage("You attach the cog back into place.")
            } else e.player.sendMessage("You already have attached the cog.")

            PISTON -> if (e.player.vars.getVarBit(6016) == 1) {
                e.player.inventory.deleteItem(e.item)
                e.player.vars.setVarBit(6016, 2)
                e.player.sendMessage("You attach the pistons back into place.")
            } else e.player.sendMessage("That part won't fit quite yet.")

            CHIMNEY -> if (e.player.vars.getVarBit(6016) == 2) {
                e.player.inventory.deleteItem(e.item)
                e.player.vars.setVarBit(6016, 4)
                e.player.sendMessage("You attach the chimney back into place.")
                e.player.sendMessage("You hear the machine whirr as it turns back on.")
                e.player.save(STAGE_KEY, 6)
            } else e.player.sendMessage("That part won't fit quite yet.")

            else -> {}
        }
    }

    onNpcClick(CHARLIE) { (player) ->
        player.startConversation {
            when(player.getI(STAGE_KEY, 0)) {
                7 -> {
                    player(HeadE.CONFUSED, "Hey, are you Charlie?")
                    npc(CHARLIE, HeadE.CAT_CHEERFUL, "Yeah, that's me! Can I help you?")
                    player(HeadE.HAPPY_TALKING, "Yes, the Easter Bunny sent me to ask you if you had some workers that can help operate his chocolate egg factory.")
                    npc(CHARLIE, HeadE.CAT_CHEERFUL, "Oh, definitely! We'd love to help out. Is this because of his son being useless again?")
                    player(HeadE.UPSET, "Yeah, it is. It's an unfortunate situation.")
                    npc(CHARLIE, HeadE.CAT_CHEERFUL, "It's fine, we've been picking up his slack for years. We'll head over as quick as we can.")
                    player(HeadE.CHEERFUL, "Thank you for your help!")
                    exec {
                        player.save(STAGE_KEY, 8)
                        player.vars.setVarBit(6014, 85)
                    }
                }
                8 -> npc(CHARLIE, HeadE.CAT_CHEERFUL, "You head on back and let the Easter Bunny know we're coming!")
                else -> {
                    player(HeadE.HAPPY_TALKING, "Hello!")
                    npc(CHARLIE, HeadE.CAT_CHEERFUL, "Oh, hello! How's it going?")
                    player(HeadE.HAPPY_TALKING, "It's going great, thanks!")
                }
            }
        }
    }

    onNpcClick(EASTER_BUNNY) { (player) ->
        player.startConversation {
            when (player.getI(STAGE_KEY, 0)) {
                0 -> {
                    player(HeadE.CHEERFUL, "Hello!")
                    npc(EASTER_BUNNY, HeadE.CAT_SAD, "...")
                    player(HeadE.CONFUSED, "Hello?")
                    npc(EASTER_BUNNY, HeadE.CAT_SAD, "Oh! Hello there. Sorry, I didn't really notice you.")
                    player(HeadE.CHEERFUL, "It's okay. I just hoped you'd have some chocolate for me.")
                    npc(EASTER_BUNNY, HeadE.CAT_SAD, "So did I, but it's all gone wrong. I only wanted a bit of a rest. Now no one will get their chocolate goodness.")
                    player(HeadE.MORTIFIED, "WHAT?")
                    npc(EASTER_BUNNY, HeadE.CAT_SAD, "I know, depressing, isn't it?")
                    player(HeadE.CONFUSED, "Why? What happened this year?")
                    npc(EASTER_BUNNY, HeadE.CAT_SAD, "I'm getting too old for this chocolate delivery job, so I went away on a little holiday, hopping that would refresh me. I left my son in charge of the Egg Plant...and now it's all in pieces because he's so lazy.")
                    player(HeadE.CONFUSED, "Can I help?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Oh, would you? I hope I'm not being too much truffle. Er...trouble.")
                    player(HeadE.CHEERFUL, "Of course, what shall I do?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You could fix up the Egg Plant so it's working; that would be a start.")
                    player(HeadE.CHEERFUL, "Okay!")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Now you've agreed to help, you'll need to get through the warrens to the Egg Plant, to speak to that lazy son of mine. For that you need to be bunny-sized!")
                    player(HeadE.CONFUSED, "How do I do that?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You simply go down the rabbit hole. My magic will sort the transformation, though you may feel a little itchy for a couple of weeks afterwards. I'll meet you down there!")
                    player(HeadE.CHEERFUL, "Off I go then!") { player.save(STAGE_KEY, 1) }
                }

                1 -> if (player.y > 5000) {
                    player(HeadE.CHEERFUL, "Wow it sure is dark down here.")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Yeah, 'tis the life of a bunny! Glad you made it down safely.")
                    player(HeadE.CHEERFUL, "What should I be fixing up first?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Well the first problem to deal with is the Easter Bird.. He hasn't been laying eggs due to being so hungry and thirsty. There's some food and water to the east of him. You'll have to figure out which food he likes.")
                    player(HeadE.CHEERFUL, "Alright, I'll get right on that!") { player.save(STAGE_KEY, 2) }
                } else npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Speak to me down in my hole. I'll meet you down there!")

                2 -> {
                    player(HeadE.CHEERFUL, "What am I supposed to be doing again?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You should wake up the bird with some food and water. I forget what his favorite food is, though. So you're going to have to figure it out.")
                }

                3 -> {
                    player(HeadE.CHEERFUL, "I woke up the bird!")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Great work! He should get back to laying the easter eggs again pretty quickly now.")
                    player(HeadE.CHEERFUL, "Awesome. What should I work on next?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "The incubator seems to be broken. I am not sure where the peices even went. My son probably had something to do with it. You should head over to his room to the west ask him about where they are.")
                    player(HeadE.CHEERFUL, "Alright, thanks. I'll get going.") { player.save(STAGE_KEY, 4) }
                }

                4, 5 -> {
                    player(HeadE.CHEERFUL, "What am I supposed to be doing again?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You should be trying to fix the incubator. Like I said before, I am clueless as to what happened to it. My son in his room to the west would be your best bet.")
                }

                6 -> {
                    player(HeadE.CHEERFUL, "Alright, the incubator is fixed. Your son is absolutely awful..")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Amazing work! Yes, I know he is quite awful. His mother left early on and he seems to have no respect for me and anyone older than him at all despite how much work I do to raise him!")
                    player(HeadE.CHEERFUL, "I'm sorry to hear about that.")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "It's quite alright, I hope I can get through to him someday.")
                    player(HeadE.CHEERFUL, "I sure hope so. Is everything done yet?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Not quite, I need some workers to operate the machines if my son is not going to contribute anything to the season.")
                    player(HeadE.CHEERFUL, "Where do you think I can find some workers?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Squirrels! One of my great old friends Charlie lives a little north of Falador and he certainly has some motivated and cheerful workers who'd love to help!")
                    player(HeadE.SKEPTICAL_THINKING, "Squirrels? If you say so. I'll head up to Falador and see if I can find Charlie then.") { player.save(STAGE_KEY, 7) }
                }

                7 -> {
                    player(HeadE.CHEERFUL, "What am I supposed to be doing again?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "I need some workers to operate the machinery! My friend Charlie lives up north of Falador, you should speak to him.")
                }

                8 -> {
                    player(HeadE.HAPPY_TALKING, "It looks like Charlie's workers made it here pretty quickly! The factory looks like it's up and running now. Is there anything else you need help with?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Oh how eggciting! I think that should be fine for this year. Thank you so much for your help.")
                    player(HeadE.HAPPY_TALKING, "No problem, is there any special chocolate you can give me now that everything is fixed?")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Oh of course! I almost forgot. Here's a magical ring for all the trouble you went through!")
                    player(HeadE.CHEERFUL, "Thank you!")
                    item(7927, "The Easter Bunny hands you a chicken costume and unlocks the Bunny Hop emote for you!")
                    exec {
                        player.save(STAGE_KEY, 9)
                        intArrayOf(11022, 11021, 11020, 11019).forEach {
                            player.inventory.addItemDrop(Item(it, 1))
                            player.addDiangoReclaimItem(it)
                        }
                        player.emotesManager.unlockEmote(EmotesManager.Emote.BUNNY_HOP)
                    }
                }

                9 -> {
                    player(HeadE.HAPPY_TALKING, "Happy Easter!")
                    npc(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Happy Easter to you too! I am very grateful for your help. He is risen!")
                    player(HeadE.HAPPY_TALKING, "It was nothing. He is risen, indeed!")
                }
            }
        }
    }

    onNpcClick(EASTER_BUNNY_JR) { (player) ->
        player.startConversation {
            when (player.getI(STAGE_KEY, 0)) {
                4 -> {
                    player(HeadE.CHEERFUL, "Hello!")
                    npc(EASTER_BUNNY_JR, HeadE.CAT_PURRING, "What do you want?..")
                    player(HeadE.CHEERFUL, "I need help fixing the incubator and your father told me you might know what happened to it.")
                    npc(EASTER_BUNNY_JR, HeadE.CAT_SAD, "Sure whatever.. The incubator exploded and they're somewhere around the factory.")
                    player(HeadE.CONFUSED, "Can you tell me where they are exactly?..")
                    npc(EASTER_BUNNY_JR, HeadE.CAT_SAD, "I don't really care and I don't remember.. All I know is that 3 parts went flying off when it exploded. Leave me alone now, I'm trying to sleep.")
                    player(HeadE.ANGRY, "That doesn't help much at all!")
                    npc(EASTER_BUNNY_JR, HeadE.CAT_DISAPPOINTED2, "Ok boomer.")
                    player(HeadE.ROLL_EYES, "*What a lazy sack of garbage.*") { player.save(STAGE_KEY, 5) }
                }

                5 -> {
                    player(HeadE.ANGRY, "Tell me where the incubator parts are.")
                    npc(EASTER_BUNNY_JR, HeadE.CAT_SAD, "As I said before, I don't really care and I don't remember.. All I know is that 3 parts went flying off around the factory when it exploded.")
                }

                else -> {
                    player(HeadE.CHEERFUL, "Hello!")
                    npc(EASTER_BUNNY_JR, HeadE.CAT_PURRING, "Zzzz...")
                }
            }
        }
    }
}

fun useBunnyHole(player: Player, obj: GameObject, toTile: Tile) {
    player.lock()
    player.faceObject(obj)
    player.schedule {
        wait(1)
        player.sync(8901, 1567)
        wait(12)
        player.tele(toTile)
        player.anim(8902)
        wait(9)
        player.anim(-1)
        player.unlock()
    }
}

@ServerStartupEvent(ServerStartupEvent.Priority.FILE_IO)
fun loadEaster2021Spawns() {
    if (!ENABLED) return
    ObjectSpawns.add(ObjectSpawn(23117, 10, 0, Tile.of(3210, 3424, 0), "Rabbit hole"))
    NPCSpawns.add(NPCSpawn(9687, Tile.of(3212, 3425, 0), "Easter Bunny"))
    NPCSpawns.add(NPCSpawn(9687, Tile.of(2463, 5355, 0), "Easter Bunny"))
    NPCSpawns.add(NPCSpawn(7411, Tile.of(2448, 5357, 0), "Easter Bunny Jr").setCustomName("Easter Bunny Jr (Trent with Easter 2024 Event)"))
    NPCSpawns.add(NPCSpawn(9686, Tile.of(2969, 3431, 0), "Charlie the Squirrel"))
    NPCSpawns.add(NPCSpawn(3283, Tile.of(2968, 3429, 0), "Squirrel"))
    NPCSpawns.add(NPCSpawn(3284, Tile.of(2970, 3429, 0), "Squirrel"))
    NPCSpawns.add(NPCSpawn(3285, Tile.of(2969, 3428, 0), "Squirrel"))
    NPCSpawns.add(NPCSpawn(3285, Tile.of(2968, 3432, 0), "Squirrel"))
}
