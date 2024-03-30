package com.rs.game.content.world.areas.varrock.npcs

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.World
import com.rs.game.content.miniquests.from_tiny_acorns.PickPocketUrist
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.GroundItem
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.Ticks

private const val talisman = 18649
private const val bankNote = 18652
private const val babyDragon = 18651
private const val waitTime = 5

private val uristTile: Tile = Tile.of(3222, 3424, 0)

@ServerStartupEvent
fun mapUristPlugins() {
    onNpcClick(11270, options = arrayOf("Talk-to")) { (player, npc) ->
        if (npc.tile != uristTile) {
            player.sendMessage("He looks busy.")
            return@onNpcClick
        }
        if (!player.isMiniquestStarted(Miniquest.FROM_TINY_ACORNS)) {
            preQuest(player)
            return@onNpcClick
        }
        if (player.isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
            postQuest(player, npc)
            return@onNpcClick
        }
        when (player.getMiniquestStage(Miniquest.FROM_TINY_ACORNS)) {
            1 -> stage1(player, npc)
            2 -> stage2(player, npc)
            3 -> stage3(player, npc)
            else -> postQuest(player, npc)
        }
    }

    onNpcClick(11270, options = arrayOf("Pickpocket")) { (p, npc) ->
        if (!p.isMiniquestStarted(Miniquest.FROM_TINY_ACORNS)) {
            p.startConversation {
                npc(npc, HeadE.ANGRY, "Oi! Leave that alone.")
            }
            return@onNpcClick
        }
        if (p.inventory.containsItem(talisman)) {
            p.sendMessage("You've stolen his talisman already.")
            return@onNpcClick
        }
        if (getFloorDrop(p) != null) {
            p.sendMessage("His talisman isn't in his pocket, it's on the ground nearby.")
            return@onNpcClick
        }
        if (p.inventory.containsItem(babyDragon) || p.miniquestManager.isComplete(Miniquest.FROM_TINY_ACORNS)) {
            p.sendMessage("You don't need this; you've already got the dragon.")
            return@onNpcClick
        }
        p.actionManager.setAction(PickPocketUrist(npc))
    }
}

private fun getFloorDrop(player: Player): GroundItem? {
    for (groundItem in World.getAllGroundItemsInChunkRange(823724, 1)) {
        if (groundItem.definitions == null || groundItem.id != talisman) {
            continue
        }
        if (!groundItem.tile.withinArea(3220, 3427, 3228, 3432)) {
            player.sendMessage("I should find a suitable spot to put this. Maybe just north of him...")
            continue
        }
        return groundItem
    }
    return null
}

private fun preQuest(player: Player) {
    player.sendMessage("The Dwarf is hard at work on some sort of highly delicate construction, and doesn't pay you any notice.")
}

private fun stage1(player: Player, npc: NPC) {
    getFloorDrop(player)?.let { floorDrop ->
        player.startConversation {
            player(HeadE.CALM_TALK, "That thing on the ground... Is it yours?")
            npc(npc, HeadE.SCARED, "What thing? Oh! Thank you kindly, I'd have been sad if I'd lost that.")
            exec {
                npc.walkToAndExecute(floorDrop.tile) {
                    player.schedule {
                        for (i in 0..3) {
                            when (i) {
                                0 -> {
                                    player.miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).setB("UristDistracted", true)
                                    npc.forceTalk("Hmm, how'd this get over here?")
                                    World.removeGroundItem(floorDrop)
                                    wait(Ticks.fromSeconds(waitTime))
                                }
                                1 -> {
                                    npc.forceTalk("Ugh it's all dirty. Lucky I've got my blue silk handkerchief on me.")
                                    wait(Ticks.fromSeconds(waitTime))
                                }
                                2 -> {
                                    npc.forceTalk("There, that's better.")
                                    wait(Ticks.fromSeconds(waitTime))
                                }
                                3 -> {
                                    npc.forceTalk("Well, back to work.")
                                    player.miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).setB("UristDistracted", false)
                                    wait(Ticks.fromSeconds(waitTime))
                                }
                            }
                        }
                        npc.walkToAndExecute(uristTile) { npc.resetDirection() }
                        player.unlock()
                    }
                }
            }
        }
    } ?: run {
        // No floor drop present, proceed with normal conversation
        player.startConversation {
            player(HeadE.CALM_TALK, "Are you the master craftsman working on a commission for Darren Lightfinger?")
            npc(npc, HeadE.HAPPY_TALKING, "That I am. What can I do for you?")
            player(HeadE.CALM_TALK, "How's it coming along?")
            npc(npc, HeadE.HAPPY_TALKING, "It's very nearly done. Just needs a bath in preserving oil to protect the mechanisms, and then a good polish.")
            player(HeadE.CALM_TALK, "Is this it here on your stall?")
            npc(npc, HeadE.HAPPY_TALKING, "Aye, the baby red dragon there. As ordered, its scales are perfect rubies and it's capable of walking and breathing fire.")
            npc(npc, HeadE.HAPPY_TALKING, "Could have made it fly too, given another six months or so.")
            player(HeadE.CALM_TALK, "Very nice. Well, see you later.")
            npc(npc, HeadE.HAPPY_TALKING, "Aye, that you will.")
        }
    }
}



private fun stage2(player: Player, npc: NPC) {
    if (player.inventory.containsItem(babyDragon) || player.bank.containsItem(babyDragon, 1)) {
        player.startConversation {
            player(HeadE.CALM_TALK, "I'm just checking up on progress for Mr. Lightfinger. You said it would be ready in a couple of days?")
            npc(npc, HeadE.HAPPY_TALKING, "Aye, it's nearly done. It's right he-")
            player(HeadE.CONFUSED, "...")
            npc(npc, HeadE.SCARED, "By all the gods! Where's it gone? Where's it got to?")
            player(HeadE.CONFUSED, "It's not walked off, has it?")
            npc(npc, HeadE.FRUSTRATED, "It couldn't walk that far on one turn of the spring.")
            player(HeadE.CALM_TALK, "I hope it turns up, then; it's due very soon. Could it have been stolen?")
            npc(npc, HeadE.FRUSTRATED, "In Varrock? With that guard watching like a hawk?")
            player(HeadE.CALM_TALK, "He doesn't seem all that hawklike.")
            npc(npc, HeadE.SKEPTICAL, "Now that you mention it...")
            player(HeadE.CALM_TALK, "Will you be able to finish by the deadline?")
            npc(npc, HeadE.SHAKING_HEAD, "Are you serious? Not a chance!")
            player(HeadE.CALM_TALK, "Then what will you tell Mr. Lightfinger?")
            npc(npc, HeadE.VERY_FRUSTRATED, "I don't see that I have much choice; I shall have to give him his money back.")
            player(HeadE.CALM_TALK, "That sounds fair.")
            npc(npc, HeadE.HAPPY_TALKING, "Since you're working for him, can you take this banker's note to him with my sincerest apologies?")
            simple("Urist hands you a banker's note. The figure on it is astronomical.") {
                player.inventory.addItem(18652, 1)
                player.miniquestManager.setStage(Miniquest.FROM_TINY_ACORNS, 3)
            }
            player(HeadE.CALM_TALK, "I should think so.")
            npc(npc, HeadE.CALM_TALK, "I appreciate your understanding.")
            player(HeadE.CALM_TALK, "I appreciate your cooperation.")
        }
    } else {
        player.startConversation {
            player(HeadE.CALM_TALK, "Oh, nice dragon.")
            npc(npc, HeadE.HAPPY_TALKING, "I thought I'd lost it for a while. Lucky I found it, eh?") {
                player.miniquestManager.setStage(Miniquest.FROM_TINY_ACORNS, 1)
            }
        }
    }
}

private fun stage3(player: Player, npc: NPC) {
    player.startConversation{
        if (!player.inventory.containsItem(babyDragon) && !player.bank.containsItem(babyDragon, 1)) {
                player(HeadE.CALM_TALK, "Oh, nice dragon.")
                npc(npc, HeadE.HAPPY_TALKING, "I thought I'd lost it for a while. Lucky I found it, eh?") {
                    player.miniquestManager.setStage(Miniquest.FROM_TINY_ACORNS, 1)
                    if (player.inventory.containsItem(bankNote)) {
                        player.inventory.deleteItem(bankNote, 1)
                        player.sendMessage("The banker's note is now useless to you; you crumple it and throw it away.")
                    }
                    if (player.bank.containsItem(bankNote, 1)) {
                        player.bank.deleteItem(bankNote, 1)
                        player.sendMessage("The banker's note is now useless, I'm sure the bank will dispose of it.")
                    }
                }
                player(HeadE.FRUSTRATED, "How... fortunate.")
            return@startConversation
        }
        if (!player.inventory.containsItem(bankNote) && !player.bank.containsItem(bankNote, 1)) {
            player(HeadE.CALM_TALK, "I'm afraid I can't find the banker's note you made out.")
            npc(npc, HeadE.FRUSTRATED, "Then I'll have to write another.") {
                player.inventory.addItem(bankNote)
                player.sendMessage("Urist hands you another banker's note. The figure on it is still astronomical.")
            }
            return@startConversation
        }
        npc(npc, HeadE.SAD_CRYING, "Ruined! Ruined! It makes me want to throw a tantrum!")
    }
}

fun postQuest(player: Player, npc: NPC) {
    player.startConversation {
        npc(npc, HeadE.SAD_CRYING, "Ruined! Ruined! It makes me want to throw a tantrum!")
    }
}
