package com.rs.game.content.quests.eadgars_ruse

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.achievements.Achievement
import com.rs.game.content.quests.eadgars_ruse.dialogues.npcs.ardougne.ParrotyPeteD
import com.rs.game.content.quests.eadgars_ruse.instances.npcs.GoutweedCrateGuard
import com.rs.game.content.quests.eadgars_ruse.instances.npcs.TrollThistle
import com.rs.game.content.quests.eadgars_ruse.utils.*
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.EADGARS_RUSE,
    startText = "Talk to Sanfew in Taverley.",
    itemsText = "(Rock) climbing boots, bottle of vodka, pineapple chunks, 10 wheat, 5 raw chickens, 2 logs, unfinished ranarr potion.",
    combatText = "None, but beware of level 77 thrower trolls.",
    rewardsText = "11,000 Herblore XP<br>" +
            "Ability to grow and steal goutweed<br>" +
            "Ability to use the Trollheim Teleport spell<br>" +
            "Burnt meat",
    completedStage = STAGE_COMPLETE
)

class EadgarsRuse : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("To start this quest, I should talk to Sanfew in Taverley.")
        STAGE_SPEAK_TO_EADGAR -> listOf("Sanfew said he needs assistance getting a herb called goutweed. He told me that only the trolls in the Troll Stronghold know where to find it.",
            "He suggested I speak to his friend Eadgar who lives near the top of Trollheim.")
        STAGE_SPEAK_TO_BURNTMEAT -> listOf("Eadgar suggested I speak to a Troll Cook in the Troll Stronghold.")
        STAGE_BRING_HUMAN -> listOf("The Troll Cook will tell me how to find goutweed if I bring him a tasty human.")
        STAGE_GET_PARROT -> listOf("I told Eadgar what the Troll Cook said. He came up with a plan.",
            "Eadgar wants to make a fake human to give the troll cook.",
            "He needs me to bring him a parrot from the Zoo first.")
        STAGE_NEED_TO_HIDE_PARROT -> listOf("I got the parrot needed for the plan. Eadgar suggested I hide it somewhere in the Troll Stronghold so it learns how to speak like a human.",
            if (!player.containsOneItem(DRUNK_PARROT) && !player.bank.containsItem(DRUNK_PARROT) && player.getQuestStage(Quest.EADGARS_RUSE) == STAGE_NEED_TO_HIDE_PARROT)
                "<col=EEEEEE>I have lost the parrot but I'm sure I can get another from Ardougne</col><br><col=EEEEEE>Zoo with some more alco-chunks.</col>" else "")
        STAGE_HIDDEN_PARROT -> listOf("I hid the parrot under the rack in the Troll prison.",
            "I should see what else Eadgar needs as part of his plan.")
        STAGE_NEED_TROLL_POTION -> listOf("I gave Eadgar everything he needed. He now needs me to make a Troll potion.")
        STAGE_FETCH_PARROT -> listOf("I made the Troll potion and gave it to Eadgar. He suggested I go and fetch the parrot.")
        STAGE_RETRIEVED_PARROT -> listOf("I have fetched the parrot back from the Troll prison. I should hand it to Eadgar now.",
            if (!player.containsOneItem(DRUNK_PARROT) && !player.bank.containsItem(DRUNK_PARROT) && player.getQuestStage(Quest.EADGARS_RUSE) == STAGE_RETRIEVED_PARROT)
                "<col=EEEEEE>I have lost the parrot. I wonder if Eadgar has seen it...</col>" else "")
        STAGE_RECEIVED_FAKE_MAN -> listOf("I fetched the parrot back from the Troll prison rack and gave it to Eadgar. Eadgar gave me his fake man to take to the troll cook, Burntmeat.",
            if (!player.containsOneItem(FAKE_MAN_ITEM) && !player.bank.containsItem(FAKE_MAN_ITEM) && player.getQuestStage(Quest.EADGARS_RUSE) == STAGE_RECEIVED_FAKE_MAN)
                "<col=EEEEEE>I have lost the fake man. I wonder if Eadgar has another...</col>" else "")
        STAGE_GAVE_FAKE_MAN_TO_BURNTMEAT -> listOf("I gave the fake man to the troll cook. I wonder if he'll tell me where the goutweed is now...")
        STAGE_DISCOVERED_KEY_LOCATION -> listOf("The troll cook told me the key to the storeroom is in a fake bottom in the kitchen drawers.")
        STAGE_UNLOCKED_STOREROOM -> listOf("I've unlocked the storeroom! I can sneak into the storeroom and get some goutweed now.")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun updateStage(player: Player, stage: Int) { }

    override fun complete(player: Player) {
        player.interfaceManager.sendAchievementComplete(Achievement.EADGARS_RUSE_449)
        player.packets.setIFGraphic(1244, 18, 9544)
        sendQuestCompleteInterface(player, GOUTWEED)
        player.skills.addXpQuest(Skills.HERBLORE, 11000.0)
    }
}

@ServerStartupEvent
fun mapEadgarsRuseInteractions() {

    onNpcClick(PARROTY_PETE, THISTLE) { (player, npc) ->
        when (npc.id) {
            PARROTY_PETE -> ParrotyPeteD(player, npc)
            THISTLE -> EadgarsRuseUtils(player).pickThistle(npc)
        }
    }

    onObjectClick(STOREROOM_DOOR, *KITCHEN_DRAWERS.toTypedArray(), RACK, GOUTWEED_CRATE) { (player, obj, option) ->
        when(obj.id) {
            STOREROOM_DOOR -> EadgarsRuseUtils(player).handleStoreroomDoor(obj)
            GOUTWEED_CRATE -> EadgarsRuseUtils(player).handleGoutweedCrate()
            RACK -> EadgarsRuseUtils(player).searchRack()
            in KITCHEN_DRAWERS -> EadgarsRuseUtils(player).handleKitchenDrawers(obj, option)
        }
    }

    onItemOnItem(intArrayOf(VODKA, PINEAPPLE_CHUNKS), intArrayOf(VODKA, PINEAPPLE_CHUNKS)) { e ->
        val itemUsed = e.item1.id
        val itemWith = e.item2.id
        when {
            (itemUsed == VODKA && itemWith == PINEAPPLE_CHUNKS) || (itemUsed == PINEAPPLE_CHUNKS && itemWith == VODKA) -> {
                EadgarsRuseUtils(e.player).handleAlcoChunks()
            }
        }
    }

    onItemOnObject(arrayOf(AVIARY_HATCH, RACK)) { (player, obj, item) ->
        when (obj.id) {
            AVIARY_HATCH -> {
                if (item.id == ALCOCHUNKS) EadgarsRuseUtils(player).captureParrot()
                if (item.id == PINEAPPLE_CHUNKS || item.id == VODKA) player.sendMessage("Nothing interesting happens.")
            }
            RACK -> { if (item.id == DRUNK_PARROT) EadgarsRuseUtils(player).hideParrot() }
        }
    }

    onItemOnObject(arrayOf("Fire"), arrayOf(TROLL_THISTLE)) { (player, _, _) ->
        EadgarsRuseUtils(player).dryThistle()
    }

    onPickupItem(DRUNK_PARROT) { e ->
        if (e.player.containsOneItem(DRUNK_PARROT) || e.player.bank.containsItem(DRUNK_PARROT)) {
            e.cancelPickup()
            e.player.sendMessage("You already have a parrot.")
        }
    }

    onDropItem(DRUNK_PARROT, TROLL_THISTLE, DRIED_THISTLE) { e ->
        when (e.item.id) {
            TROLL_THISTLE, DRIED_THISTLE -> e.player.sendMessage("The Troll Thistle withers away as soon as it hits the ground.")
            DRUNK_PARROT -> e.player.sendMessage("You release the parrot, and it flies away.")
        }
        e.cancelDrop()
        e.player.inventory.deleteItem(e.item.slot, e.item)
    }

    instantiateNpc(GOUTWEED_GUARD) { npcId, tile -> GoutweedCrateGuard(npcId, tile) }
    instantiateNpc(THISTLE) { npcId, tile -> TrollThistle(npcId, tile) }

}
