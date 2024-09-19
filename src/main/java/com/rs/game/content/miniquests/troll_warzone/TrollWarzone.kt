package com.rs.game.content.miniquests.troll_warzone

import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.Options
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.miniquest.MiniquestHandler
import com.rs.engine.miniquest.MiniquestOutline
import com.rs.game.World
import com.rs.game.content.combat.CombatStyle
import com.rs.game.content.pets.Pets
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.events.NPCClickEvent
import com.rs.plugin.events.ObjectClickEvent
import com.rs.plugin.handlers.NPCClickHandler
import com.rs.plugin.handlers.ObjectClickHandler
import com.rs.plugin.kts.npcCombat
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick
import java.util.function.Consumer

@MiniquestHandler(
    miniquest = Miniquest.TROLL_WARZONE,
    startText = "Speak to Major Nigel Corothers just outside the Warrior's Guild in Burthorpe.",
    itemsText = "None", combatText = "Must be able to defeat a level 12 Troll General.",
    rewardsText = "A baby troll!<br>110 Cooking XP<br>110 Mining XP<br>110 Woodcutting XP<br>Some teleport tablets<br>Some combat potions",
    completedStage = 6
)
class TrollWarzone : MiniquestOutline() {
    //9 - troll general comes down from the mountain
    //10 - ambushing trolls with archers
    //11 - intro to burthorpe tutorial
    //12 - player shoots cannon to close off the troll invasion
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        0 -> listOf("I can start this miniquest by speaking to Major Nigel Corothers in northern Burthorpe.")
        1 -> listOf("I can start this miniquest by speaking to Major Nigel Corothers in northern Burthorpe.")
        2 -> listOf("I can start this miniquest by speaking to Major Nigel Corothers in northern Burthorpe.")
        3 -> listOf("I can start this miniquest by speaking to Major Nigel Corothers in northern Burthorpe.")
        4 -> listOf("I can start this miniquest by speaking to Major Nigel Corothers in northern Burthorpe.")
        5 -> listOf("I can start this miniquest by speaking to Major Nigel Corothers in northern Burthorpe.")
        6 -> listOf("MINIQUEST COMPLETE!")
        else -> listOf("Invalid miniquest stage. Report this to an administrator.")
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.COOKING, 110.0)
        player.skills.addXpQuest(Skills.MINING, 110.0)
        player.skills.addXpQuest(Skills.WOODCUTTING, 110.0)
        player.inventory.addItemDrop(23030, 1)
        player.inventory.addItemDrop(8007, 5)
        player.inventory.addItemDrop(8009, 5)
        player.inventory.addItemDrop(2429, 5)
        player.inventory.addItemDrop(114, 5)
        player.inventory.addItemDrop(2433, 5)
        player.inventory.addItemDrop(2435, 5)
        sendQuestCompleteInterface(player, 23030)
    }

    override fun updateStage(player: Player) {
        if (player.miniquestManager.getStage(Miniquest.TROLL_WARZONE) >= 5) player.vars.setVarBit(10683, player.miniquestManager.getStage(Miniquest.TROLL_WARZONE))
        //varbit 10683 updates corporal keymans to claim the baby troll
    }

    companion object {
        @JvmStatic
        fun getCaptainJuteDialogue(player: Player, npc: NPC?): Dialogue {
            val dialogue = Dialogue()
            when (player.miniquestManager.getStage(Miniquest.TROLL_WARZONE)) {
                0 -> dialogue.addNPC(npc, HeadE.FRUSTRATED, "The trolls are overrunning us! Major Nigel has been trying to find recruits.")
                1 -> dialogue.addNPC(npc, HeadE.FRUSTRATED, "Get back in the cave over there to help Ozan and Keymans!")
                2 -> dialogue.addNPC(npc, HeadE.CALM_TALK, "Ozan tells me that you defeated one of the troll generals.")
                    .addNPC(npc, HeadE.CALM_TALK, "The trolls are getting into that cave through a back entrance high on Death Plateau. If we try to collapse this end, we could collapse the whole castle with it!")
                    .addNPC(npc, HeadE.CALM_TALK, "Death Plateau itself is too dangerous to assault. I need you to get to the top of the castle and direct cannon fire onto that back entrance!")
                    .addOptions { ops: Options ->
                        ops.add("I'll do it right away!") { player.miniquestManager.setStage(Miniquest.TROLL_WARZONE, 3) }
                        ops.add("I want to kill more trolls!")
                    }

                3 -> dialogue.addNPC(npc, HeadE.CALM_TALK, "What are you waiting for? Get up on top of the castle and fire a cannon into that cavern back entrance!")
                else -> dialogue.addNPC(npc, HeadE.CALM_TALK, "Excellent work bringing down the cavern entrance. You should go check in with Corothers.")
            }
            return dialogue
        }
    }
}

@ServerStartupEvent
fun mapTrollWarzone() {
    //Troll General combat script
    npcCombat(14991, 14992) { npc, target ->
        if (npc.inMeleeRange(target)) {
            npc.anim(1932)
            CombatScript.delayHit(npc, 1, target, Hit.melee(npc, CombatScript.getMaxHit(npc, 2, CombatStyle.MELEE, target)))
        } else {
            npc.sync(1933, 262)
            CombatScript.delayHit(npc, World.sendProjectile(npc, target, 295, 34 to 16, 60, 3, 16).taskDelay, target, Hit.range(npc, CombatScript.getMaxHit(npc, 2, CombatStyle.RANGE, target)))
        }
        return@npcCombat npc.attackSpeed
    }

    onObjectClick(66533, 66534) { e ->
        when (e.objectId) {
            66533 -> {
                if (e.player.miniquestManager.getStage(Miniquest.TROLL_WARZONE) < 1) {
                    e.player.simpleDialogue("You should speak with Major Nigel Corothers before going in here. He's only just south of here.")
                    return@onObjectClick
                }
                if (e.player.miniquestManager.getStage(Miniquest.TROLL_WARZONE) == 1) {
                    e.player.sendOptionDialogue("Would you like to continue the Troll Warzone miniquest?") { ops: Options ->
                        ops.add("Yes.") { e.player.controllerManager.startController(TrollGeneralAttackController()) }
                        ops.add("Not right now.")
                    }
                    return@onObjectClick
                }
                e.player.useStairs(-1, Tile.of(2208, 4364, 0), 0, 1)
            }

            66534 -> e.player.useStairs(-1, Tile.of(2878, 3573, 0), 0, 1)
        }
    }

    onObjectClick(66981) { e ->
        if (e.player.miniquestManager.getStage(Miniquest.TROLL_WARZONE) == 3) {
            e.player.miniquestManager.setStage(Miniquest.TROLL_WARZONE, 4)
            e.player.playPacketCutscene(12) { e.player.playerDialogue(HeadE.HAPPY_TALKING, "That just about does it. I should check in with Corothers.") }
            return@onObjectClick
        }
        if (e.player.miniquestManager.getStage(Miniquest.TROLL_WARZONE) < 3) e.player.sendMessage("You haven't been given approval to fire off any cannons here.")
        else e.player.sendMessage("I've collapsed the entrance with the cannon. I should check in with Corothers.")
    }

    onNpcClick(14994) { (player, npc, option) ->
        when (option) {
            "Talk-to" -> {
                if (!player.miniquestManager.isComplete(Miniquest.TROLL_WARZONE) && player.miniquestManager.getStage(Miniquest.TROLL_WARZONE) >= 5) {
                    player.startConversation {
                        npc(14994, HeadE.CALM_TALK, "Ozan dumped that baby troll on me. I don't know what to do with him.")
                        player(HeadE.HAPPY_TALKING, "I could look after him.")
                        exec { player.miniquestManager.complete(Miniquest.TROLL_WARZONE) }
                    }
                    return@onNpcClick
                }
                if (player.containsItem(23030) || (player.pet != null && player.pet.id == Pets.TROLL_BABY.babyNpcId))
                    player.npcDialogue(npc, HeadE.CALM_TALK, "Thanks for all your help in the cave")
                else player.startConversation {
                    npc(npc.id, HeadE.FRUSTRATED, "I found this little guy wandering around up here. Thought you might want him back. Try not to lose him again.")
                    addItemToInv(player, Item(23030), "You reclaim the baby troll.")
                }
            }
        }
    }

    onNpcClick(14850) { (player, npc, option) ->
        when (option) {
            "Get-recommendation" -> player.startConversation(Dialogue().addNPC(14850, HeadE.FRUSTRATED, "If you're looking to train combat, soldier, I'd recommend ridding the local area of as many trolls as possible. Or there are some cows to the south."))
            "Talk-to" -> player.startConversation {
                when (player.miniquestManager.getStage(Miniquest.TROLL_WARZONE)) {
                    0 -> options {
                        opExec("Yes.") {
                            player.playPacketCutscene(11) { player.controllerManager.startController(TrollGeneralAttackController()) }
                            player.miniquestManager.setStage(Miniquest.TROLL_WARZONE, 1)
                        }
                        op("Not right now.")
                    }

                    1 -> npc(npc, HeadE.FRUSTRATED, "You need to get back up to the cave to the north and assist in stopping the trolls!")
                    2, 3 -> npc(npc, HeadE.FRUSTRATED, "Excellent work in holding back the trolls in the cave. Captain Jute up north said he wanted to talk to you.")
                    4 -> {
                        npc(npc, HeadE.FRUSTRATED, "So you've thwarted the recent attack by defeating the general. Good work. Looks like the trolls have let up their attacks for now.")
                        voiceEffect(12434)
                        npc(npc, HeadE.FRUSTRATED, "Here in Burthorpe we've been hit pretty hard. Taverly, the town to the south, has been sending us aid and they're in bad shape too.")
                        voiceEffect(11121)
                        npc(npc, HeadE.FRUSTRATED, "What we need to do now is recover in time for the next attack. I need you to work your way around Burthorpe and Taverly lending your help where you can.")
                        voiceEffect(12488)
                        npc(npc, HeadE.FRUSTRATED, "We have a lot of experts here, helping with the war effort. Check in with them to see what you can do.")
                        voiceEffect(11208)
                        player(HeadE.CHEERFUL, "I'm on it.")
                        exec { player.miniquestManager.setStage(Miniquest.TROLL_WARZONE, 5) }
                    }

                    5 -> npc(npc, HeadE.FRUSTRATED, "Go check in with Keymans. He said he has something for you.")
                    else -> {
                        npc(npc, HeadE.FRUSTRATED, "Burthorpe is still under dire threat. We need every hero we can get in top shape.")
                        voiceEffect(12443)
                    }
                }
            }
        }
    }
}
