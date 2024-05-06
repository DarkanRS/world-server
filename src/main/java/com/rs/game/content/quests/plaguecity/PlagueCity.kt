package com.rs.game.content.quests.plaguecity

import com.rs.engine.dialogue.HeadE
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.quests.biohazard.utils.STAGE_RETURN_TO_JERICO
import com.rs.game.content.quests.plaguecity.dialogues.npcs.*
import com.rs.game.content.quests.plaguecity.dialogues.objects.*
import com.rs.game.content.quests.plaguecity.instances.npcs.PrisonHouseMourners
import com.rs.game.content.quests.plaguecity.utils.*
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.lib.util.Utils.*
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.PLAGUE_CITY,
    startText = "Speak to Edmond in East Ardougne. (North of the castle, next to the wall surrounding West Ardougne.)",
    itemsText = "Dwellberries<br>" +
            "Rope<br>" +
            "Ingredients to make a hangover cure:<br>" +
            "- Chocolate bar/dust<br>" +
            "- Bucket of milk<br>" +
            "- Snape grass",
    combatText = "None.",
    rewardsText = "2425 Mining XP<br>" +
            "Ardougne Teleport spell<br>" +
            "Access to West Ardougne<br>" +
            "Gas mask",
    completedStage = STAGE_COMPLETE
)

class PlagueCity : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("To start this quest, I can speak to Edmond. He lives next to the wall surrounding West Ardougne, north of Ardougne Castle.")
        STAGE_SPEAK_TO_ALRENA -> listOf("Edmond told me his daughter, Elena, has been missing for three weeks, since she managed to cross over the Ardougne wall.",
            "I have offered to help Edmond, who said he had a plan to get into West Ardougne. He said I would need protection from the plague.",
            "His wife, Alrena, has made a special gasmask but requires some dwellberries to finish it off. I said I would get the dwellberries and give them to Alrena to finish the mask off.")
        STAGE_RECEIVED_GAS_MASK -> listOf("I have given the dwellberries to Alrena and she handed me the finished gasmask in return.",
            "She said she'd make and hide a spare gasmask in her wardrobe in case I lose mine or the mourners come in.",
            "I should talk to Edmond about the next stage of the plan to get into West Ardougne.")
        STAGE_PREPARE_TO_DIG -> {
            val waterUsedSoFar = player.questManager.getAttribs(Quest.PLAGUE_CITY).getI(WATER_USED_ON_MUD)
            val remainingWaterNeeded = 4 - waterUsedSoFar
            val bucketsString = if (remainingWaterNeeded == 1) "bucket" else "buckets"
            val remainingWaterMessage = if (remainingWaterNeeded > 0) "I still need to pour more $bucketsString of water on the soil. $remainingWaterNeeded more should do it..."
            else "I have poured 4 buckets of water onto the soil to soften it up."
            listOf("Edmond said we need to soften the soil up, by pouring several buckets of water on it. We need to do this before we can dig down into the sewers.",
                remainingWaterMessage)
        }
        STAGE_CAN_DIG -> listOf("I should dig the softened soil to get through to the Ardougne Sewers.")
        STAGE_UNCOVERED_SEWER_ENTRANCE -> listOf("I have dug through to the Ardougne Sewers from Edmond's garden.",
            "I should talk to him for the next stage of the plan.")
        STAGE_NEED_HELP_WITH_GRILL -> {
            val edmondSuggestedRope = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(EDMOND_SUGGESTED_ROPE)
            val message = if (edmondSuggestedRope) "Edmond suggested I tie some rope to the iron grill and we can both pull it at the same time."
            else "I wonder if Edmond can help..."
            listOf("I have tried to get through the pipe in the Ardougne Sewers, but there is an iron grill blocking the way. I have tried to pull it off, but it's too secure.",
                message)
        }
        STAGE_ROPE_TIED_TO_GRILL -> listOf("I have tied some rope to the iron grill in the Ardougne Sewers. I should ask Edmond to help me pull the rope at the same time...")
        STAGE_GRILL_REMOVED -> {
            val enteredCity = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ENTERED_CITY)
            listOf(
                strikeThroughIf("With Edmond's help, I have managed to pull the iron grill from the") { enteredCity },
                strikeThroughIf("sewer pipe, which was blocking my way to West Ardougne.") { enteredCity },
                strikeThroughIf("I should make sure I equip my gasmask before proceeding to climb") { enteredCity },
                strikeThroughIf("up the pipe.") { enteredCity },
                "Edmond suggested I find his old friend Jethick in West Ardougne, as he may be able to help find Elena.")
        }
        STAGE_SPOKEN_TO_JETHICK -> {
            val jethickNeedsPicture = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(JETHICK_NEEDS_PICTURE)
            val jethickReturnBook = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(JETHICK_RETURN_BOOK)

            val messageJethickNeedsPicture1 = if (jethickNeedsPicture) "Jethick doesn't know what Elena looks like. He suggested I bring him" else ""
            val messageJethickNeedsPicture2 = if (jethickNeedsPicture) "a picture of her to help jog his memory..." else ""
            val messageReturnBook = if (jethickReturnBook) "After showing him a picture of Elena, Jethick said he thinks Elena is staying with the Rehnison family in a small timbered building at the far north side of town. He asked me to return a book he borrowed from the Rehnison family whilst I'm over there."
            else ""
            listOf(
                strikeThroughIf(messageJethickNeedsPicture1) { jethickReturnBook },
                strikeThroughIf(messageJethickNeedsPicture2) { jethickReturnBook },
                messageReturnBook)
        }
        STAGE_GAVE_BOOK_TO_TED -> listOf("I have returned the book Jethick borrowed to Ted Rehnison. Perhaps I should ask the Rehnisons where Elena is...")
        STAGE_SPEAK_TO_MILLI -> listOf("Ted & Martha Rehnison suggested I speak with their daughter Milli upstairs in their home. Milli claims to have seen 'shadowy figures' jump out and grab Elena as she was getting ready to leave West Ardougne.")
        STAGE_SPOKEN_TO_MILLI -> {
            val attemptedPrisonHouseDoors = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ATTEMPTED_PRISON_HOUSE_DOORS)
            val message = if (attemptedPrisonHouseDoors) "I have attempted to open the doors to the building Milli told me about, but the Mourners there told me it was too risky with plague. They said if I want to enter, I'd need permission from either the head mourner or Bravek, the city warder." else ""
            listOf(
                strikeThroughIf("Milli told me she saw Elena walking past her whilst she was out") { attemptedPrisonHouseDoors },
                strikeThroughIf("playing by the south east corner of West Ardougne. She saw some") { attemptedPrisonHouseDoors },
                strikeThroughIf("men jump out,shove a sack over her head and drag her into the") { attemptedPrisonHouseDoors },
                strikeThroughIf("boarded up building with no windows in the south east corner of") { attemptedPrisonHouseDoors },
                strikeThroughIf("West Ardougne.") { attemptedPrisonHouseDoors },
                message)
        }
        STAGE_PERMISSION_TO_BRAVEK -> listOf("I have spoken to the Clerk of West Ardougne who told me Bravek was too busy to see me. I have persuaded the Clerk to let me speak to Bravek in his room on the first floor.")
        STAGE_GET_HANGOVER_CURE -> listOf("Bravek appeared to be very drunk. He said his herbalist, who used to make hangover cures for him, caught the plague. He suggested I make him a hangover cure using the recipe he gave me on a scruffy note. I can just about make out what it says:",
            "Got a bncket of nnilk",
            "Tlen qrind sorne lhoculate",
            "vnith a pestal and rnortar",
            "ald the grourd dlocolate to tho milt",
            "fnales add 5cme snape gras5")
        STAGE_GAVE_HANGOVER_CURE -> {
            val enteredPrisonHouse = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ENTERED_PRISON_HOUSE)
            val attemptedToFreeElena = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ATTEMPTED_TO_FREE_ELENA)
            val foundKeyInBarrel = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(FOUND_KEY_IN_BARREL)
            val enteredPrisonCell = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ENTERED_PRISON_CELL)
            val messageEnteredPrisonHouse = if (!enteredPrisonHouse) "I wonder if the Mourners guarding the house will let me in now..." else "I showed the Mourners the search warrant, but they didn't seem too sure on letting me in so I sneaked in when their back was turned."
            val messageAttemptedToFreeElena = if (attemptedToFreeElena && !foundKeyInBarrel) "The door to Elena's prison cell is locked. She said she was sure she heard the kidnappers stashing the key somewhere, shortly after they locked her up." else ""
            val messageFoundKeyInBarrel = if (enteredPrisonCell) { "Using the small key I found in the barrel, I was able to get into the prison cell which Elena is being held captive in. I should let her know she can leave freely now."
            } else if (foundKeyInBarrel && !attemptedToFreeElena) { "I found a key in a barrel on the ground floor of the house the mourners were guarding. I wonder what it's for..."
            } else if (attemptedToFreeElena && foundKeyInBarrel) { "I found a key in a barrel on the ground floor of the house Elena is being held captive in. Hopefully this unlocks the cell door..."
            } else { "" }
            listOf("After giving Bravek the hangover cure, he gave me a search warrant for the boarded up house in the south east corner of West Ardougne.",
                messageEnteredPrisonHouse,
                messageAttemptedToFreeElena,
                messageFoundKeyInBarrel)
        }
        STAGE_FREED_ELENA -> listOf("I have freed Elena from the prison cell her kidnappers locked her up in. She suggested I speak to her father, Edmond, for a reward!")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun updateStage(player: Player, stage: Int) {
        setPlagueCityVarBits(player)
    }

    override fun complete(player: Player) {
        sendQuestCompleteInterface(player, GAS_MASK)
        player.inventory.addItemDrop(A_MAGIC_SCROLL, 1)
        player.skills.addXpQuest(Skills.MINING, 2425.0)
    }
}

@ServerStartupEvent
fun mapPlagueCityInteractions() {
    onNpcClick(EDMOND_ABOVE_GROUND, EDMOND_BELOW_GROUND, ALRENA, JETHICK, TED_REHNISON, MARTHA_REHNISON, BILLY_REHNISON,
        MILLI_REHNISON, NON_COMBAT_MOURNER, MOURNER_EAST_PRISON_DOOR, MOURNER_WEST_PRISON_DOOR, CLERK, BRAVEK,
        ELENA_PRISON, EAST_ARDOUGNE_MOURNERS) { (player, npc) ->
        when(npc.id) {
            EDMOND_ABOVE_GROUND, EDMOND_BELOW_GROUND -> { EdmondD(player, npc) }
            ALRENA -> { AlrenaD(player, npc) }
            JETHICK -> { JethickD(player, npc) }
            TED_REHNISON -> { TedRehnisonD(player, npc) }
            MARTHA_REHNISON -> { MarthaRehnisonD(player, npc) }
            BILLY_REHNISON -> { player.sendMessage("Billy isn't interested in talking.") }
            MILLI_REHNISON -> { MilliRehnisonD(player, npc) }
            NON_COMBAT_MOURNER, MOURNER_EAST_PRISON_DOOR, MOURNER_WEST_PRISON_DOOR -> { NonCombatMournersD(player, npc) }
            CLERK -> { ClerkD(player, npc) }
            BRAVEK -> { BravekD(player, npc) }
            ELENA_PRISON -> { ElenaD(player, npc) }
            EAST_ARDOUGNE_MOURNERS -> { EastArdougneMournersD(player, npc) }
        }
    }

    onObjectClick(*GAS_MASK_WARDROBE.toTypedArray(), MUD_PATCH_UNDIGGABLE, MUD_PATCH_DIGGABLE, MUD_PILE_SEWERS, SEWER_PIPE,
        SEWER_GRILL, *MANHOLES.toTypedArray(), TED_REHNISON_DOOR, HEAD_MOURNER_DOORS, BRAVEK_DOOR, KEY_BARREL,
        PRISON_STAIRS_UP, PRISON_STAIRS_DOWN, ELENA_PRISON_DOOR) { (player, obj, option) ->
        when(obj.id) {
            in GAS_MASK_WARDROBE -> { PlagueCityUtils().handleGasmaskWardrobe(player, obj, option) }
            MUD_PATCH_UNDIGGABLE -> { PlagueCityUtils().digAtMudPatch(player) }
            MUD_PATCH_DIGGABLE -> { player.useStairs(-1, EDMOND_SEWER_TELE_LOC, 0, 0) }
            MUD_PILE_SEWERS -> { player.useLadder(CLIMB_LADDER_ANIM, EDMOND_HOUSE_TELE_LOC) }
            SEWER_PIPE -> { PlagueCityUtils().handleSewerPipe(player) }
            SEWER_GRILL -> { PlagueCityUtils().handleSewerGrill(player, false) }
            in MANHOLES -> { PlagueCityUtils().handleManhole(player, obj, option) }
            TED_REHNISON_DOOR -> { RehnisonDoorD(player, obj) }
            HEAD_MOURNER_DOORS -> { PrisonHouseDoorsD(player, obj) }
            BRAVEK_DOOR -> { BravekDoorD(player, obj) }
            KEY_BARREL -> { PlagueCityUtils().searchKeyBarrel(player) }
            PRISON_STAIRS_UP, PRISON_STAIRS_DOWN -> { PlagueCityUtils().handlePrisonStairs(player) }
            ELENA_PRISON_DOOR -> { PlagueCityUtils().handleElenaPrisonDoor(player, obj) }
        }
    }

    onItemOnObject(arrayOf(MUD_PATCH_DIGGABLE, SEWER_GRILL, ELENA_PRISON_DOOR)) { (player, obj, item) ->
        when (obj.id) {
            MUD_PATCH_DIGGABLE -> { if (item.id == BUCKET_OF_WATER) PlagueCityUtils().waterOnMudPatch(player) else if (item.id == SPADE) PlagueCityUtils().digAtMudPatch(player) }
            SEWER_GRILL -> { if (item.id == ROPE) PlagueCityUtils().handleSewerGrill(player, true) }
            ELENA_PRISON_DOOR -> { if (item.id == SMALL_KEY) PlagueCityUtils().handleElenaPrisonDoor(player, obj) }
        }
    }

    onItemEquip(GAS_MASK) { e ->
        e.apply {
            if (e.dequip() && PlagueCityUtils().isInWestArdougne(player.tile)) {
                player.playerDialogue(HeadE.WORRIED, "I should probably keep the gas mask on whilst I'm in West Ardougne.")
                cancel()
            }
        }
    }

    onItemOnItem(intArrayOf(CHOCOLATE_DUST, BUCKET_OF_MILK, SNAPE_GRASS, CHOCOLATEY_MILK), intArrayOf(CHOCOLATE_DUST, BUCKET_OF_MILK, SNAPE_GRASS, CHOCOLATEY_MILK)) { e ->
        val itemUsed = e.item1.id
        val itemWith = e.item2.id
        when {
            (itemUsed == CHOCOLATE_DUST && itemWith == BUCKET_OF_MILK) || (itemUsed == BUCKET_OF_MILK && itemWith == CHOCOLATE_DUST) -> {
                e.player.inventory.removeItems(Item(CHOCOLATE_DUST), Item(BUCKET_OF_MILK))
                e.player.inventory.addItemDrop(CHOCOLATEY_MILK, 1)
                e.player.itemDialogue(CHOCOLATEY_MILK, "You mix the chocolate into the bucket.")
            }
            (itemUsed == SNAPE_GRASS && itemWith == CHOCOLATEY_MILK) || (itemUsed == CHOCOLATEY_MILK && itemWith == SNAPE_GRASS) -> {
                if (e.player.questManager.getStage(Quest.PLAGUE_CITY) >= STAGE_GET_HANGOVER_CURE) {
                    e.player.inventory.removeItems(Item(CHOCOLATEY_MILK), Item(SNAPE_GRASS))
                    e.player.inventory.addItemDrop(HANGOVER_CURE, 1)
                    e.player.itemDialogue(HANGOVER_CURE, "You mix the snape grass into the bucket.")
                }
            }
        }
    }

    onItemClick(SCRUFFY_NOTE, A_MAGIC_SCROLL, options = arrayOf("Read")) { e ->
        when (e.item.id) {
            SCRUFFY_NOTE -> { PlagueCityUtils().handleScruffyNote(e.player) }
            A_MAGIC_SCROLL -> { PlagueCityUtils().handleMagicScroll(e.player) }
        }
    }

    instantiateNpc(MOURNER_EAST_PRISON_DOOR, MOURNER_WEST_PRISON_DOOR) { id, tile -> PrisonHouseMourners(id, tile) }

    onLogin { (p) ->
        setPlagueCityVarBits(p)
    }
}

/*
 * Sets various VarBits relating to Plague City quest.
 */
fun setPlagueCityVarBits(player: Player) {
    val plagueCityStage = player.questManager.getStage(Quest.PLAGUE_CITY)
    val biohazardStarted = player.isQuestStarted(Quest.BIOHAZARD)

    val hasOpenedSewerEntrance = plagueCityStage >= STAGE_UNCOVERED_SEWER_ENTRANCE
    val hasAttachedRopeToGrill = plagueCityStage >= STAGE_ROPE_TIED_TO_GRILL
    val hasPulledGrillOff = plagueCityStage >= STAGE_GRILL_REMOVED
    val hasFreedElena = plagueCityStage >= STAGE_FREED_ELENA
    val isQuestComplete = plagueCityStage >= STAGE_COMPLETE

    player.vars.apply {
        setVarBit(EDMOND_VB, if (isQuestComplete) 0 else if (hasOpenedSewerEntrance) 1 else 0)
        setVarBit(ELENA_VB, if (hasFreedElena) 1 else 0)
        setVarBit(GRILL_VB, if (hasAttachedRopeToGrill) 1 else 0)
        setVarBit(GRILL_VB, if (isQuestComplete) 2 else if (hasPulledGrillOff) 2 else if (hasAttachedRopeToGrill) 1 else 0)

        if (biohazardStarted) {
            setVarBit(MUD_PATCH_VB, 0)
        } else {
            setVarBit(MUD_PATCH_VB, if (isQuestComplete) 1 else if (hasOpenedSewerEntrance) 1 else 0)
        }
    }
}
