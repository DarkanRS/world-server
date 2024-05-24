package com.rs.game.content.quests.gunnarsground

import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.achievements.Achievement
import com.rs.game.content.quests.gunnarsground.dialogues.ChieftainGunthorD
import com.rs.game.content.quests.gunnarsground.dialogues.DororanD
import com.rs.game.content.quests.gunnarsground.dialogues.GudrunD
import com.rs.game.content.quests.gunnarsground.dialogues.JefferyD
import com.rs.game.content.quests.gunnarsground.utils.*
import com.rs.game.content.world.areas.varrock.npcs.REPLACEMENT_GUNNARS_GROUND_POEM
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.GUNNARS_GROUND,
    startText = "Speak to Dororan near the bridge at the Barbarian Village.",
    itemsText = "A chisel.",
    combatText = "None.",
    rewardsText =
            "300 Crafting XP<br>" +
            "Antique Lamp<br>" +
            "Swanky boots<br>" +
            "Talk to Dororan for more Crafting tasks",
    completedStage = STAGE_COMPLETE
)

class GunnarsGround : QuestOutline() {
    override fun updateStage(player: Player, stage: Int) {
        setGunnarsGroundVarBits(player)
    }
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("To start this quest, I should speak to Dororan at the entrance to Barbarian Village, near the bridge.")
        STAGE_RECEIVED_LOVE_POEM -> listOf("Dororan has asked me to visit Jeffery in Edgeville to obtain a ring of 'purest gold'. He has given me a love poem to give to Jeffery to try and heal a rift between them both.")
        STAGE_RECEIVED_RING -> listOf("Jeffery accepted the love poem and gave me a gold ring. I should take this back to Dororan.")
        STAGE_NEED_TO_CHISEL_RING -> listOf("Dororan has asked me to engrave the following words onto gold ring; 'Gudrun the Fair, Gudrun the Fiery.'")
        STAGE_CHISELED_RING -> listOf("I have chiseled the words onto the gold ring. I should see if Dororan is happy with it!")
        STAGE_NEED_TO_DELIVER_RING -> listOf("Dororan has asked me to deliver the engraved ring to Gudrun in the Barbarian Village.")
        STAGE_NEED_TO_TALK_TO_GUNTHOR -> listOf("Gudrun told me that her father, Chieftain Gunthor, will never let an outerlander pursue her. I have agreed to try and talk to him to persuade him otherwise...")
        STAGE_RETURN_TO_GUDRUN_AFTER_GUNTHOR -> listOf("Chieftain Gunthor told me we are not friends, nor allies. He said I should tell Gudrun to remember her forefathers!", "I figured I had better do as he says before Haakon dismembers me.")
        STAGE_RETURN_TO_DORORAN_TO_WRITE_POEM -> listOf("I told Gudrun what her father said, and she suggested I speak to Dororan for some ideas on what to do next...")
        STAGE_WRITING_THE_POEM -> listOf("Dororan suggested writing a poem to 'touch the chieftain's soul'. I should see how he's getting on with writing the poem.")
        STAGE_FINISHED_WRITING_POEM -> listOf("I have helped Dororan write the poem by suggesting 3 words he was stuck on. I should see if he's ready to present the poem to Chieftain Gunthor.")
        STAGE_RECEIVED_GUNNARS_GROUND_POEM -> listOf("Dororan said he's a writer, not a performer. He's asked me to deliver the poem to Gudrun in the hope that she will present it to her father.")
        STAGE_POST_CUTSCENES -> listOf("Gudrun presented the poem to her father, Chieftain Gunthor. He seemed to have enjoyed it as he has made an announcement to his people, that Barbarian Village shall now be known, officially, as a settlement named Gunnar's Ground.",
            "Gudrun discovered it was Dororan who had been sending her the gifts and it looks as though they've fallen in love. I should speak to them, in the middle of Gunnar's Ground, to see how they're getting on!")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }
    override fun complete(player: Player) {
        sendQuestCompleteInterface(player, GUNNARS_GROUND_POEM.id)
        player.inventory.addItemDrop(SWANKY_BOOTS.id, 1)
        player.inventory.addItemDrop(ANTIQUE_LAMP.id, 1)
        player.skills.addXpQuest(Skills.CRAFTING, 300.0)
    }
}

@ServerStartupEvent
fun mapGunnarsGround() {
    onNpcClick(QUESTING_DORORAN, DORORAN_POST_CUTSCENE, JEFFERY, QUESTING_GUDRUN, GUDRUN_POST_CUTSCENE, QUESTING_CHIEFTAIN_GUNTHOR) { (player, npc) ->
        when(npc.id) {
            JEFFERY -> {
                JefferyD(player, npc)
            }
            QUESTING_DORORAN, DORORAN_POST_CUTSCENE-> {
                DororanD(player, npc)
            }
            QUESTING_GUDRUN, GUDRUN_POST_CUTSCENE -> {
                GudrunD(player, npc)
            }
            QUESTING_CHIEFTAIN_GUNTHOR -> {
                ChieftainGunthorD(player, npc)
            }
        }
    }

    onDestroyItem(LOVE_POEM.id, RING_FROM_JEFFERY.id, DORORANS_ENGRAVED_RING.id, GUNNARS_GROUND_POEM.id) { (player, item) ->
        setHasItem(player, item, false)
        when(item.id) {
            LOVE_POEM.id -> {
                player.sendMessage("Hopefully, Dororan has a copy of that love poem...")
            }
            RING_FROM_JEFFERY.id -> {
                player.sendMessage("You hope Jeffery had more than one of those rings...")
            }
            DORORANS_ENGRAVED_RING.id -> {
                player.sendMessage("I hope Jeffery had more of those rings, so I can engrave another!")
            }
            GUNNARS_GROUND_POEM.id -> {
                player.sendMessage("With any luck, Dororan made a copy of the Gunnar's Ground poem.")
            }
        }
    }

    onItemOnItem(intArrayOf(CHISEL.id), intArrayOf(RING_FROM_JEFFERY.id)) { e ->
        engraveRing(e.player)
    }

    onItemClick(RING_FROM_JEFFERY.id, GUNNARS_GROUND_POEM.id, REPLACEMENT_GUNNARS_GROUND_POEM.id, options = arrayOf("Engrave", "Read")) { e ->
        when (e.option) {
            "Engrave" -> { engraveRing(e.player) }
            "Read" -> { GunnarsGroundPoem(e.player) }
        }
    }

    onLogin { (player) ->
        setGunnarsGroundVarBits(player)
    }
}

/*
 * Function called to engrave ring.
 */
fun engraveRing(player: Player) {
    if (player.questManager.getStage(Quest.GUNNARS_GROUND) == STAGE_NEED_TO_CHISEL_RING) {
        player.schedule {
            player.lock()
            player.anim(ANIM_CHISEL_RING)
            wait(2)
            player.startConversation {
                item(DORORANS_ENGRAVED_RING.id, "You engrave 'Gudrun the Fair, Gudrun the Fiery' onto the ring.") {
                    player.inventory.deleteItem(RING_FROM_JEFFERY)
                    setHasItem(player, RING_FROM_JEFFERY, false)
                    player.inventory.addItem(DORORANS_ENGRAVED_RING)
                    setHasItem(player, DORORANS_ENGRAVED_RING, true)
                    player.questManager.setStage(Quest.GUNNARS_GROUND, STAGE_CHISELED_RING)
                    player.unlock()
                }
            }
        }
    } else {
        player.sendMessage("Nothing interesting happens.")
    }
}

/*
 * Functions used to determine if player has item, in inventory or bank, or not at all.
 */
fun setHasItem(player: Player, item: Item, hasOrNot: Boolean) {
    if (!hasOrNot)
        player.questManager.getAttribs(Quest.GUNNARS_GROUND).removeB("has"+item.name)
    else
        player.questManager.getAttribs(Quest.GUNNARS_GROUND).setB("has"+item.name, true)
}

fun getHasItem(player: Player, item: Item): Boolean {
    return player.questManager.getAttribs(Quest.GUNNARS_GROUND).getB("has"+item.name)
}

/*
 * Functions used to determine what phase of writing the Gunnar's Ground poem the player is on.
 * 0 - 3 is writing.
 * 4 is complete, so remove attribute.
 */
fun setPoemStage(player: Player, stage: Int) {
    if (stage == 4)
        player.questManager.getAttribs(Quest.GUNNARS_GROUND).removeI("poemStage")
    else
        player.questManager.getAttribs(Quest.GUNNARS_GROUND).setI("poemStage", stage)
}

fun getPoemStage(player: Player): Int {
    return player.questManager.getAttribs(Quest.GUNNARS_GROUND).getI("poemStage")
}

/*
 * Sets various VarBits relating to Gunnar's Ground quest.
 */
fun setGunnarsGroundVarBits(player: Player) {
    val isPostCutscenes = player.questManager.getStage(Quest.GUNNARS_GROUND) == STAGE_POST_CUTSCENES
    val isQuestComplete = player.questManager.isComplete(Quest.GUNNARS_GROUND)
    val barbVillageVarBit = if (isPostCutscenes || isQuestComplete) 1 else 0
    val dororanBarbVillageVarBit = if (isPostCutscenes || isQuestComplete) 1 else 0
    val gudrunBarbVillageVarBit = if (isPostCutscenes || isQuestComplete) 1 else 0

    player.vars.apply {
        setVarBit(BARB_VILLAGE_VARBIT, barbVillageVarBit)
        setVarBit(DORORAN_BARB_VILLAGE_VARBIT, dororanBarbVillageVarBit)
        setVarBit(GUDRUN_BARB_VILLAGE_VARBIT, gudrunBarbVillageVarBit)
        setVarBit(DORORAN_VARROCK_VARBIT, if (isQuestComplete) 1 else 0)
        setVarBit(GUDRUN_VARROCK_VARBIT, if (isQuestComplete) 1 else 0)
        setVarBit(DORORAN_PRE_COMPLETE_BARB_VILLAGE_VARBIT, if (isPostCutscenes) 1 else 0)
        setVarBit(GUDRUN_PRE_COMPLETE_BARB_VILLAGE_VARBIT, if (isPostCutscenes) 1 else 0)
    }
}

/*
 * Fades screen & completes quest
 */
fun completeGunnarsGround(player: Player) {
    player.fadeScreen {
        player.packets.setIFGraphic(1244, 18, 3797)
        player.questManager.completeQuest(Quest.GUNNARS_GROUND)
        player.interfaceManager.sendAchievementComplete(Achievement.GUNNARS_GROUND_502)
    }
}
