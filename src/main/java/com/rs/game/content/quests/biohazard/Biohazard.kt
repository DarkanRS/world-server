package com.rs.game.content.quests.biohazard

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.quests.biohazard.dialogue.npcs.east_ardougne.*
import com.rs.game.content.quests.biohazard.dialogue.npcs.west_ardougne.KilronD
import com.rs.game.content.quests.biohazard.instances.npcs.MournerWithKey
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.content.quests.plaguecity.setPlagueCityVarBits
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.BIOHAZARD,
    startText = "Talk to Elena in her house in East Ardougne.)",
    itemsText = "Priest gown top and bottom.",
    combatText = "You will need to defeat a level 13 mourner.",
    rewardsText = "1,250 Thieving XP<br>" +
            "Ability to travel freely through the West Ardougne gate<br>" +
            "Use of the Combat Training Camp north of Ardougne",
    completedStage = STAGE_COMPLETE
)

class Biohazard : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("To start this quest, I should talk to Elena in her house in East Ardougne.")
        STAGE_SPEAK_TO_JERICO -> listOf("Elena told me that if she's got any chance of curing the plague, she needs her equipment back. The mourners took it when they kidnapped her in West Ardougne.",
            "I agreed to collect it for her.",
            "As the hole I dug into the Ardougne Sewers has been sealed up, she suggested I speak to her father's friend, Jerico, who lives near the Ardougne Chapel, for help getting back into West Ardougne.")
        STAGE_SPEAK_TO_OMART -> listOf("Jerico said he uses his messenger pigeons to communicate with friends in West Ardougne. He has arranged for two friends to aid me with a rope ladder.",
            "He told me to meet Omart by the south end of the wall.")
        STAGE_RETURN_TO_JERICO -> listOf("I met with Omart by the south end of the wall who suggested he can help me with a rope ladder, but he needs the mourners in the watchtower distracted first.",
            "He suggested I ask Jerico for ideas on how I might distract the mourners.")
        STAGE_JERICO_SUGGESTS_PIGEONS -> listOf("I asked Jerico for ideas, and we agreed that I could use his messenger pigeons.",
            "He keeps them round the back of his house.")
        STAGE_MOURNERS_DISTRACTED -> listOf("I have distracted the mourners using Jerico's messenger pigeons.",
            "I should seek Omart's assistance in getting over the wall now.")
        STAGE_COMPLETED_WALL_CROSSING -> listOf("With the help of Omart & Kilron, I have managed to cross the wall into West Ardougne.",
            "I should go get Elena's equipment back from the Mourner's HQ building, in the north-east corner of the city.")
        STAGE_APPLE_IN_CAULDRON -> listOf("I have thrown a rotten apple into the cauldron at the Mourner's HQ. Hopefully that'll have the desired effect, and I can now get into the building to recover ELena's equipment.",
            "Perhaps they need a doctor from the city...")
        STAGE_FOUND_DISTILLATOR -> listOf("After dressing as a doctor, I was able to get into the Mourner's HQ, where they all seem to have food poisoning.",
            "I made my way up to the roof, and recovered Elena's equipment where mourners were guarding it.",
            "I should take it back to Elena at once...")
        STAGE_RECEIVED_VIALS -> listOf("I returned Elena's equipment to her. She tested the plague sample, but it doesn't sound as though it went as she had planned.",
            "She's asked me to take some vials and a plague sample to a man called Guidor, who lives in Varrock.",
            "Elena said I would need to get some more touch paper first, from the chemist in Rimmington.")
        STAGE_RECEIVED_TOUCH_PAPER -> {
            val hopsItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_HOPS_VIAL_OF)
            val daVinciItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_DA_VINCI_VIAL_OF)
            val chancyItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_CHANCY_VIAL_OF)
            val lostItemToGuard = player.questManager.getAttribs(Quest.BIOHAZARD).getB(LOST_ITEM_TO_GUARD)
            val allVialsHandedOver = hopsItem > 0 && daVinciItem > 0 && chancyItem > 0
            val anyItemLost = hopsItem == 1 || daVinciItem == 1 || chancyItem == 1
            val collectedAllItems = hopsItem == 2 && daVinciItem == 2 && chancyItem == 2

            if (!player.questManager.getAttribs(Quest.BIOHAZARD).getB(GAVE_ITEMS_TO_GUIDOR)) {
                val list = mutableListOf<String>()
                if (!allVialsHandedOver) {
                        list.add("The chemist gave me touch paper and told me I could use his errand boys to smuggle the samples into Varrock. If I lose the touch paper, I'm sure the chemist will give me another.")
                        list.add(" ")
                        if (lostItemToGuard) {
                            list.add("The guard in Varrock searched me and took at least one of the vials.")
                            list.add(" ")
                        }
                        list.add("I still need to give a vial to:")
                        list.add(Utils.strikeThroughIf("Hops") { hopsItem > 0 })
                        list.add(Utils.strikeThroughIf("Da Vinci") { daVinciItem > 0 })
                        list.add(Utils.strikeThroughIf("Chancy") { chancyItem > 0 })
                        list.add(" ")
                }
                if (!allVialsHandedOver) {
                    list.add("Once I've given each of the chemist's errand boys a vial, I should meet them in Varrock, at the Dancing Donkey Inn, to collect them.")
                    list.add("If I lose any of the vials, I am sure Elena will have more for me.")
                } else {
                    if (!collectedAllItems) {
                        if (anyItemLost) {
                            list.add("At least one of the errand boys lost a vial on the way to Varrock. I should go ask Elena for more vials...")
                        } else {
                            list.add("I've given each of the chemist's errand boys a vial. I should meet them at the Dancing Donkey Inn, in Varrock, to collect them.")
                        }
                    } else {
                        list.add("I've collected all items from the errand boys in Varrock. I should take them to Guidor now, for analysis.")
                        if (!player.inventory.containsOneItem(TOUCH_PAPER))
                            list.add("I don't have the touch paper with me. I'm sure the chemist will give me another.")
                        if (!player.inventory.containsItems(ETHENEA, LIQUID_HONEY, SULPHURIC_BROLINE, PLAGUE_SAMPLE))
                            list.add("Looks like I am missing at least one of the vials. I'm sure Elena will have spares.")
                    }
                }

                list.toList()
            } else {
                listOf(
                    "I have given the vials, touch paper and plague sample to Guidor in Varrock.",
                    "I wonder what he's discovered from testing the plague sample."
                )
            }
        }
        STAGE_RETURN_TO_ELENA -> listOf("Guidor tested the samples using the vials from Elena, and the touch paper from the chemist. The result was... nothing!",
            "Guidor reckons there is no plague, and that it's all been a big hoax. I should relay this to Elena...")
        STAGE_SPEAK_TO_KING -> listOf("Elena was shocked to hear that it sounds as though the plague is a hoax.",
            "She suggested I speak to King Lathas immediately!")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun updateStage(player: Player, stage: Int) {
        BiohazardUtils(player).handleTeleportHook()
        setPlagueCityVarBits(player)
    }

    override fun complete(player: Player) {
        sendQuestCompleteInterface(player, DISTILLATOR)
        player.skills.addXpQuest(Skills.THIEVING, 1250.0)
    }
}

@ServerStartupEvent
fun mapBiohazardInteractions() {
    onNpcClick(JERICO, OMART, KILRON) { (player, npc) ->
        when(npc.id) {
            JERICO -> JericoD(player, npc)
            OMART -> OmartD(player, npc)
            KILRON -> KilronD(player, npc)
        }
    }

    onObjectClick(*JERICO_CUPBOARD.toTypedArray(), WATCHTOWER_FENCE, *DOCTORS_GOWN_BOX.toTypedArray(), *DISTILLATOR_CAGE_GATE.toTypedArray(),
        DISTILLATOR_CRATE) { (player, obj, option) ->
        when(obj.id) {
            in JERICO_CUPBOARD -> BiohazardUtils(player).handleJericoCupboard(obj, option)
            WATCHTOWER_FENCE -> BiohazardUtils(player).handleWatchtowerFence()
            in DOCTORS_GOWN_BOX -> BiohazardUtils(player).handleDoctorsGownBox(obj, option)
            in DISTILLATOR_CAGE_GATE -> BiohazardUtils(player).handleDistillatorCageGate(obj)
            DISTILLATOR_CRATE -> BiohazardUtils(player).handleDistillatorCrate()
        }
    }

    onItemOnObject(arrayOf(WATCHTOWER_FENCE, CAULDRON)) { (player, obj, item) ->
        when (obj.id) {
            WATCHTOWER_FENCE -> if (item.id == BIRD_FEED) BiohazardUtils(player).handleBirdfeedOnWatchtowerFence()
            CAULDRON -> if (item.id == ROTTEN_APPLE) BiohazardUtils(player).handleUseAppleOnCauldron()
        }
    }

    onItemClick(PIGEON_CAGE, options = arrayOf("Open")) { (player, _) -> BiohazardUtils(player).handleOpeningPigeonCage() }

    instantiateNpc(MOURNER_WITH_KEY) { id, tile -> MournerWithKey(id, tile) }

    onItemAddedToInventory(DISTILLATOR) { (player, _) -> player.questManager.getAttribs(Quest.BIOHAZARD).setB(GOT_DISTILLATOR, true) }

    onDropItem(DISTILLATOR) { (player, _) -> player.questManager.getAttribs(Quest.BIOHAZARD).setB(GOT_DISTILLATOR, false) }

    onLogin { (player) ->
        BiohazardUtils(player).handleTeleportHook()
    }
}
