package com.rs.game.content.quests.dig_site

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.World
import com.rs.game.content.achievements.Achievement
import com.rs.game.content.quests.dig_site.dialogue.npcs.*
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.content.skills.mining.Pickaxe
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.DIG_SITE,
    startText = "Talk to an examiner in the Exam Centre.",
    itemsText = "Charcoal (can be obtained during the quest), cup of tea, empty vial, two ropes, uncut or cut opal (can be obtained during the quest).",
    combatText = "None.",
    rewardsText = "15,300 Mining XP<br>" +
            "2,000 Herblore XP,<br>" +
            "Two gold bars.",
    completedStage = STAGE_COMPLETE
)

class TheDigSite : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {

        STAGE_UNSTARTED -> listOf("To start this quest, I should talk to an examiner in the Exam Centre about taking the Earth Science exams.")

        STAGE_GET_LETTER_STAMPED -> listOf("<br>", "I should take the letter the examiner has given me to the Curator of Varrock's museum for his approval.")

        STAGE_RECEIVED_SEALED_LETTER -> listOf("<br>", "I need to return the letter of recommendation from the Curator of Varrock Museum to an examiner at the Exam Centre for inspection.")

        STAGE_BEGIN_EXAM_1 -> {
            val list = mutableListOf<String>()
            val spokeToGreen = player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_TALKED_TO)
            val spokeToPurple = player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_TALKED_TO)
            val spokeToBrown = player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_TALKED_TO)
            val gotAnimalSkull = player.inventory.containsOneItem(ANIMAL_SKULL)
            val gotTeddy = player.inventory.containsOneItem(TEDDY)
            val gotSpecialCup = player.inventory.containsOneItem(SPECIAL_CUP)
            val gotGreenAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_1_OBTAINED_ANSWER)
            val gotPurpleAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_1_OBTAINED_ANSWER)
            val gotBrownAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_1_OBTAINED_ANSWER)
            val gotAllAnswers = gotGreenAnswer && gotPurpleAnswer && gotBrownAnswer

            list.add("<br>")
            list.add(Utils.strikeThroughIf("I need to study for my first exam. Perhaps the students on the") { spokeToGreen || spokeToPurple || spokeToBrown })
            list.add(Utils.strikeThroughIf("site can help?") { spokeToGreen || spokeToPurple || spokeToBrown })

            // Green Student
            if (!gotGreenAnswer) list.add(Utils.strikeThroughIf("I need to speak to the student in the <col=008000>green</col> top about the exams.") { spokeToGreen })
            if (!gotGreenAnswer && spokeToGreen) {
                list.add(Utils.strikeThroughIf("I have agreed to help the student in the <col=008000>green</col> top. He has lost his") { gotAnimalSkull })
                list.add(Utils.strikeThroughIf("animal skull and thinks he may have dropped it around the site. I") { gotAnimalSkull })
                list.add(Utils.strikeThroughIf("need to find it and return it to him. Maybe one of the workmen has") { gotAnimalSkull })
                list.add(Utils.strikeThroughIf("picked it up?") { gotAnimalSkull })
            }
            if (gotAnimalSkull && spokeToGreen) {
                list.add("I should take the animal skull to the student with the <col=008000>green</col> top to see if he can help with my exams.")
            } else if (gotGreenAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=008000>green</col> top gave me an answer to one of the") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("questions on the first exam.") { gotAllAnswers })
            }

            // Purple Student
            if (!gotPurpleAnswer) list.add(Utils.strikeThroughIf("I need to speak to the student in the <col=800080>purple</col> top with pigtails about") { spokeToPurple })
            if (!gotPurpleAnswer) list.add(Utils.strikeThroughIf("the exams.") { spokeToPurple })
            if (!gotPurpleAnswer && spokeToPurple) {
                list.add(Utils.strikeThroughIf("I agreed to help the student in the <col=800080>purple</col> top with pigtails. She has") { gotTeddy })
                list.add(Utils.strikeThroughIf("lost her lucky teddy bear mascot and thinks she may have dropped") { gotTeddy })
                list.add(Utils.strikeThroughIf("it near a bush. I need to find it and return it to her.") { gotTeddy })
            }
            if (gotTeddy && spokeToPurple) {
                list.add("I should return the teddy to the student with the <col=800080>purple</col> top with pigtails to see if she can help with my exams.")
            } else if (gotPurpleAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=800080>purple</col> top with pigtails gave me an answer to") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("one of the questions on the first exam.") { gotAllAnswers })
            }

            // Brown Student
            if (!gotBrownAnswer) list.add(Utils.strikeThroughIf("I need to speak to the student in the <col=A52A2A>brown</col> top about the exams.") { spokeToBrown })
            if (!gotBrownAnswer && spokeToBrown) {
                list.add(Utils.strikeThroughIf("I agreed to help the student in the <col=A52A2A>brown</col> top. He has lost his") { gotSpecialCup })
                list.add(Utils.strikeThroughIf("special cup and thinks he may have dropped it while he was") { gotSpecialCup })
                list.add(Utils.strikeThroughIf("near the panning site, possibly in the water. I need to find") { gotSpecialCup })
                list.add(Utils.strikeThroughIf("it and return it to him.") { gotSpecialCup })
            }
            if (gotSpecialCup && spokeToBrown) {
                list.add("I should return the special cup to the student with the <col=A52A2A>brown</col> top to see if he can help with my exams.")
            } else if (gotBrownAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=A52A2A>brown</col> top gave me an answer to one of the") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("questions on the first exam.") { gotAllAnswers })
            }

            if (gotAllAnswers) list.add("I should talk to an examiner to take my first exam. If I have forgotten anything, I can always ask the students again.")

            list.toList()
        }

        STAGE_BEGIN_EXAM_2 -> {
            val list = mutableListOf<String>()
            val gotGreenAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_2_OBTAINED_ANSWER)
            val gotPurpleAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_2_OBTAINED_ANSWER)
            val gotBrownAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_2_OBTAINED_ANSWER)
            val gotAllAnswers = gotGreenAnswer && gotPurpleAnswer && gotBrownAnswer

            list.add("<br>")
            list.add(Utils.strikeThroughIf("I have passed my first Earth Science exam.") { gotAllAnswers })
            list.add(Utils.strikeThroughIf("Perhaps the three students on the site can help me again?") { gotAllAnswers })

            // Green Student
            if (gotGreenAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=008000>green</col> top gave me an answer to one of the") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("questions on the second exam.") { gotAllAnswers })
            } else {
                list.add("I need to speak to the student in the <col=008000>green</col> top about the exams.")
            }

            // Purple Student
            if (gotPurpleAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=800080>purple</col> top with pigtails gave me an answer to") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("one of the questions on the second exam.") { gotAllAnswers })
            } else {
                list.add("I need to speak to the student in the <col=800080>purple</col> top with pigtails about the exams.")
            }

            // Brown Student
            if (gotBrownAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=A52A2A>brown</col> top gave me an answer to one of the") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("questions on the second exam.") { gotAllAnswers })
            } else {
                list.add("I need to speak to the student in the <col=A52A2A>brown</col> top about the exams.")
            }

            if (gotAllAnswers) list.add("I should talk to an examiner to take my second exam. If I have forgotten anything, I can always ask the students again.")

            list.toList()
        }

        STAGE_BEGIN_EXAM_3 -> {
            val list = mutableListOf<String>()
            val spokeToPurple = player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_3_TALKED_TO)
            val gotGreenAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_3_OBTAINED_ANSWER)
            val gotPurpleAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_3_OBTAINED_ANSWER)
            val gotBrownAnswer = player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_3_OBTAINED_ANSWER)
            val gotOpal = player.inventory.containsOneItem(UNCUT_OPAL) || player.inventory.containsOneItem(OPAL)
            val gotAllAnswers = gotGreenAnswer && gotPurpleAnswer && gotBrownAnswer

            list.add("<br>")
            list.add(Utils.strikeThroughIf("I have passed my second Earth Science exam.") { gotAllAnswers })
            list.add(Utils.strikeThroughIf("I should research for my third exam. Perhaps the three students") { gotAllAnswers })
            list.add(Utils.strikeThroughIf("on the site can help me again?") { gotAllAnswers })

            // Green Student
            if (gotGreenAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=008000>green</col> top gave me an answer to one of the") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("questions on the third exam.") { gotAllAnswers })
            } else {
                list.add("I need to speak to the student in the <col=008000>green</col> top about the exams.")
            }
            // Purple Student
            if (!spokeToPurple) list.add("I need to speak to the student in the <col=800080>purple</col> top with pigtails about the exams.")
            if (!gotPurpleAnswer && spokeToPurple) {
                list.add(Utils.strikeThroughIf("I need to bring the student in the <col=800080>purple</col> top with pigtails an opal.") { gotOpal })
                list.add(Utils.strikeThroughIf("It can be uncut or cut - I don't think she minds.") { gotOpal })
            }
            if (gotOpal && spokeToPurple) {
                list.add("I should give the opal to the student with the <col=800080>purple</col> top with pigtails to see if she can help with my exams.")
            } else if (gotPurpleAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=800080>purple</col> top with pigtails gave me an answer to") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("one of the questions on the third exam.") { gotAllAnswers })
            }
            // Brown Student
            if (gotBrownAnswer) {
                list.add(Utils.strikeThroughIf("The student with the <col=A52A2A>brown</col> top gave me an answer to one of the") { gotAllAnswers })
                list.add(Utils.strikeThroughIf("questions on the third exam.") { gotAllAnswers })
            } else {
                list.add("I need to speak to the student in the <col=A52A2A>brown</col> top about the exams.")
            }
            if (gotAllAnswers) list.add("I should talk to an examiner to take my third exam. If I have forgotten anything, I can always ask the students again.")

            list.toList()
        }

        STAGE_COMPLETED_EXAMS -> listOf("<br>",
            "I have passed my third and final Earth Science exam.",
            "I need a find from the site to impress Terry Balando, the archaeological expert at the Exam Centre.")

        STAGE_RECEIVED_INVITATION -> listOf("<br>", "I need to take the invitation letter to a workman near a winch at the site.")

        STAGE_PERMISSION_GRANTED -> listOf("<br>", "I need to investigate the dig shafts.")

        STAGE_SPEAK_TO_DOUG -> listOf("<br>", "I found a secret passageway under the site.",
            "I need to find a way to move the rocks blocking the way in the shaft. Perhaps someone else in these dig shafts can help me.")

        STAGE_COVERED_IN_COMPOUND -> listOf("<br>", "I covered the rocks in the cave with an explosive compound.",
            "I need to ignite the explosive compound and blow up the rocks blocking the way.")

        STAGE_BLOWN_UP_BRICKS -> listOf("<br>", "I should look for something interesting in the secret room I found, and show it to the expert at the Exam Centre.")

        STAGE_COMPLETE -> listOf("<br>", "The expert was impressed with the Zarosian tablet that I found, and I also discovered an ancient altar!",
            "I was rewarded for my findings. My work here is done.")

        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun updateStage(player: Player, stage: Int) {
        if (stage < STAGE_BLOWN_UP_BRICKS)
            player.vars.setVarBit(TABLET_VB, 0)
    }

    override fun complete(player: Player) {
        player.interfaceManager.sendAchievementComplete(Achievement.THE_DIG_SITE_503)
        player.packets.setIFGraphic(1244, 18, 9524)
        sendQuestCompleteInterface(player, SPECIMEN_BRUSH)
        player.inventory.deleteItem(STONE_TABLET, 1)
        player.inventory.addItem(GOLD_BAR, 2)
        player.skills.addXpQuest(Skills.MINING, 15300.00)
        player.skills.addXpQuest(Skills.HERBLORE, 2000.00)
    }
}

@ServerStartupEvent
fun mapTheDigSiteInteractions() {

    onNpcClick(*EXAMINER.toTypedArray(), GREEN_STUDENT, BROWN_STUDENT, PURPLE_STUDENT, ARCHAEOLOGICAL_EXPERT, *DIGSITE_WORKMEN.toTypedArray(), DOUG_DEEPING, options = arrayOf("Talk-to")) { (player, npc) ->
        when(npc.id) {
            in EXAMINER -> ExaminerD(player, npc)
            GREEN_STUDENT -> GreenStudentD(player, npc)
            BROWN_STUDENT -> BrownStudentD(player, npc)
            PURPLE_STUDENT -> PurpleStudentD(player, npc)
            ARCHAEOLOGICAL_EXPERT -> ArchaeologicalExpertD(player, npc)
            in DIGSITE_WORKMEN -> DigsiteWorkmanD(player, npc)
            DOUG_DEEPING -> DougDeepingD(player, npc)
        }
    }
    onNpcClick(*DIGSITE_WORKMEN.toTypedArray(), DOUG_DEEPING, options = arrayOf("Steal-from")) { (player, npc) ->
        when (npc.id) {
            in DIGSITE_WORKMEN -> player.actionManager.setAction(PickpocketWorkmanAction(npc))
            DOUG_DEEPING -> {
                player.startConversation {
                    npc(npc, FRUSTRATED, "Hey! Trying to steal from me, are you? What do you think I am, stupid or something?!")
                    player(SAD, "Err... Sorry.")
                }
            }
        }
    }
    onNpcClick(PURPLE_STUDENT, options = arrayOf("Pickpocket")) { (player, npc) ->
        player.startConversation {
            player(CALM_TALK, "I don't think I should try to steal from this poor student.")
        }
    }
    onNpcClick(ARCHAEOLOGICAL_EXPERT, options = arrayOf("Identify-liquid")) { (player, npc) ->
        val unidentifiedLiquid = Item(UNIDENTIFIED_LIQUID)
        if (player.inventory.containsItem(UNIDENTIFIED_LIQUID)) {
            ArchaeologicalExpertD(player, npc).identifyItem(unidentifiedLiquid)
        } else {
            player.sendMessage("You don't have any unidentified liquid in your inventory.")
        }
    }

    onObjectClick(WRONG_BUSH, CORRECT_BUSH, PANNING_POINT, TRAINING_DIG_SIGNPOST, LVL1_DIG_SIGNPOST, LVL2_DIG_SIGNPOST, LVL3_DIG_SIGNPOST, PRIVATE_DIG_SIGNPOST, DIG_EDUCATIONAL_SIGNPOST,
        *SPECIMEN_JAR_CUPBOARD.toTypedArray(), *ROCK_PICK_CUPBOARD.toTypedArray(), SPECIMEN_TRAY, ALTAR_WINCH, DOUG_WINCH, ALTAR_ROPE, DOUG_ROPE, *CHEST.toTypedArray(), BARREL, BRICK,
        STONE_TABLET_OBJ, *MUSEUM_GUARD_GATE.toTypedArray(), *SACKS.toTypedArray(), BURIED_SKELETON, *BOOKCASES.toTypedArray()) { (player, obj, option) ->
        when (obj.id) {
            WRONG_BUSH, CORRECT_BUSH -> TheDigSiteUtils(player).searchBush(obj)
            PANNING_POINT -> TheDigSiteUtils(player).panPoint(obj)
            TRAINING_DIG_SIGNPOST -> player.sendMessage("This site is for training purposes only.")
            LVL1_DIG_SIGNPOST -> player.sendMessage("Level 1 digs only.")
            LVL2_DIG_SIGNPOST -> player.sendMessage("Level 2 digs only.")
            LVL3_DIG_SIGNPOST -> player.sendMessage("Level 3 digs only.")
            PRIVATE_DIG_SIGNPOST -> player.sendMessage("Private digs only.")
            DIG_EDUCATIONAL_SIGNPOST -> player.sendMessage("Digsite educational centre.")
            in SPECIMEN_JAR_CUPBOARD -> TheDigSiteUtils(player).handleSpecimenJarCupboard(obj, option)
            in ROCK_PICK_CUPBOARD -> TheDigSiteUtils(player).handleRockPickCupboard(obj, option)
            SPECIMEN_TRAY -> TheDigSiteUtils(player).searchSpecimenTray(obj)
            ALTAR_WINCH -> TheDigSiteUtils(player).useAltarWinch()
            DOUG_WINCH -> TheDigSiteUtils(player).useDougWinch()
            ALTAR_ROPE -> player.useStairs(CLIMB_ANIM, ALTAR_GROUND_LOCATION)
            DOUG_ROPE -> player.useStairs(CLIMB_ANIM, DOUG_GROUND_LOCATION)
            in CHEST -> TheDigSiteUtils(player).handleChest(obj, option)
            BARREL -> TheDigSiteUtils(player).openAndSearchBarrel(option)
            BRICK -> TheDigSiteUtils(player).searchBrick()
            STONE_TABLET_OBJ -> TheDigSiteUtils(player).takeStoneTablet()
            in MUSEUM_GUARD_GATE -> {
                val museumGuard: NPC? = World.getNPCsInChunkRange(player.chunkId, 2).firstOrNull { it.id == MUSEUM_GUARD }
                player.faceEntityTile(museumGuard)
                museumGuard?.forceTalk("Sorry - workman's gate only.")
                player.npcDialogue(museumGuard, CALM_TALK, "Hello there! Sorry, I can't stop to talk. I'm guarding this workman's gate. I'm afraid you can't come through here - you'll need to find another way around.")
            }
            in SACKS -> TheDigSiteUtils(player).searchSacks()
            BURIED_SKELETON -> player.sendMessage("It looks as if some poor unfortunate soul died here.")
            in BOOKCASES -> TheDigSiteUtils(player).searchBookcase(obj)
        }
    }

    onItemOnObject(arrayOf(PANNING_POINT, *SOILS.toTypedArray(), ALTAR_WINCH, DOUG_WINCH, BARREL, BRICK)) { (player, obj, item) ->
        when (obj.id) {
            PANNING_POINT -> if (item.id == EMPTY_PANNING_TRAY) TheDigSiteUtils(player).panPoint(obj)
            in SOILS -> if (item.id == TROWEL || item.id == ROCK_PICK) TheDigSiteUtils(player).searchSoil(obj)
            ALTAR_WINCH -> {
                if (item.id == ROPE) {
                    if (player.getQuestStage(Quest.DIG_SITE) == STAGE_PERMISSION_GRANTED || player.getQuestStage(Quest.DIG_SITE) == STAGE_SPEAK_TO_DOUG) {
                        if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(ATTACHED_ROPE_ALTAR_WINCH)) {
                            player.inventory.deleteItem(item)
                            player.anim(SEARCH_ANIM)
                            player.questManager.getAttribs(Quest.DIG_SITE).setB(ATTACHED_ROPE_ALTAR_WINCH, true)
                            player.sendMessage("You tie the rope to the bucket.")
                        } else {
                            player.sendMessage("There is already a rope attached to the bucket.")
                        }
                    } else {
                        player.startConversation {
                            npc(DIGSITE_WORKMEN[0], CALM_TALK, "Sorry; this area is private. The only way you'll get to use these is by impressing the archaeological expert up at the Exam Centre.")
                            npc(DIGSITE_WORKMEN[0], CALM_TALK, "Find something worthwhile and he might let you use the winches. Until then, get lost!")
                        }
                    }
                }
            }
            DOUG_WINCH -> {
                if (item.id == ROPE) {
                    if (player.getQuestStage(Quest.DIG_SITE) == STAGE_PERMISSION_GRANTED || player.getQuestStage(Quest.DIG_SITE) == STAGE_SPEAK_TO_DOUG) {
                        if (!player.questManager.getAttribs(Quest.DIG_SITE).getB(ATTACHED_ROPE_DOUG_WINCH)) {
                            player.inventory.deleteItem(item)
                            player.anim(SEARCH_ANIM)
                            player.questManager.getAttribs(Quest.DIG_SITE).setB(ATTACHED_ROPE_DOUG_WINCH, true)
                            player.sendMessage("You tie the rope to the bucket.")
                        } else {
                            player.sendMessage("There is already a rope attached to the bucket.")
                        }
                    } else {
                        player.startConversation {
                            npc(DIGSITE_WORKMEN[2], CALM_TALK, "Sorry; this area is private. The only way you'll get to use these is by impressing the archaeological expert up at the Exam Centre.")
                            npc(DIGSITE_WORKMEN[2], CALM_TALK, "Find something worthwhile and he might let you use the winches. Until then, get lost!")
                        }
                    }
                }
            }
            BARREL -> {
                if (player.getQuestStage(Quest.DIG_SITE) > STAGE_PERMISSION_GRANTED) {
                    if (player.vars.getVarBit(BARREL_VB) == 0) {
                        when (item.id) {
                            HAMMER -> player.playerDialogue(CALM_TALK, "The hammer just bounced off the wood.")
                            KNIFE -> player.playerDialogue(CALM_TALK, "Looks like the knife is too bendy; it might snap!")
                            CHISEL -> player.playerDialogue(CALM_TALK, "The chisel is too small; it might break!")
                            SPADE -> player.playerDialogue(CALM_TALK, "The spade is far too big to fit.")
                            ROCK_PICK -> player.playerDialogue(CALM_TALK, "The rock pick is too fat to fit in the gap.")
                            TROWEL -> {
                                player.playerDialogue(CALM_TALK, "Great! It's opened it.")
                                player.vars.saveVarBit(BARREL_VB, 1)
                            }

                            else -> player.sendMessage("Nothing interesting happens.")
                        }
                    }
                    if (player.vars.getVarBit(BARREL_VB) == 1) {
                        when (item.id) {
                            VIAL -> TheDigSiteUtils(player).collectLiquid()
                            SPECIMEN_JAR -> player.playerDialogue(CALM_TALK, "Perhaps not; it might contaminate the samples.")
                            EMPTY_PANNING_TRAY -> player.playerDialogue(CALM_TALK, "Not the best idea I've had; it's likely to spill everywhere in that!")
                            HAMMER, KNIFE, CHISEL, SPADE, ROCK_PICK, TROWEL -> player.sendMessage("The barrel is already open.")
                            else -> player.sendMessage("Nothing interesting happens.")
                        }
                    }
                } else {
                    player.playerDialogue(CALM_TALK, "I'm not sure why I'd want to open this potentially dangerous looking barrel.")
                }
            }
            BRICK -> {
                when (item.id) {
                    in Pickaxe.values().map { it.itemId } -> {
                        player.playerDialogue(CALM_TALK, "That was a good idea, but these blocks are huge. I'm going to need something much more powerful to shift these...")
                    }
                    ROCK_PICK -> {
                        player.playerDialogue(CALM_TALK, "That would be like cutting the lawn with nail scissors! It would take a year to chip away these rocks!")
                    }
                    CHEMICAL_COMPOUND -> {
                        if (player.getQuestStage(Quest.DIG_SITE) == STAGE_SPEAK_TO_DOUG) {
                            player.inventory.replaceItem(VIAL, 1, item.slot)
                            player.sendMessage("You pour the compound over the bricks...")
                            player.playerDialogue(CALM_TALK, "Ok, the mixture is all over the bricks. I need some way to ignite this compound.")
                            player.setQuestStage(Quest.DIG_SITE, STAGE_COVERED_IN_COMPOUND)
                        }
                    }
                    TINDERBOX -> {
                        if (player.getQuestStage(Quest.DIG_SITE) == STAGE_COVERED_IN_COMPOUND) {
                            TheDigSiteUtils(player).brickExplosionCutscene()
                        } else {
                            player.forceTalk("Now, what am I trying to achieve here?")
                        }
                    }
                }
            }
        }
    }

    onItemClick(MUD_PANNING_TRAY, GOLD_PANNING_TRAY, options = arrayOf("Search")) { (player, item) -> TheDigSiteUtils(player).searchPanningTray(item) }
    onItemClick(EMPTY_PANNING_TRAY, options = arrayOf("Search")) { (player) -> player.itemDialogue(EMPTY_PANNING_TRAY, "The panning tray is empty.") }
    onItemClick(EXAM_1_CERT, EXAM_2_CERT, EXAM_3_CERT, options = arrayOf("Look-at")) { (player, item) -> TheDigSiteUtils(player).openCertInterface(item.id) }
    onItemClick(INVITATION_LETTER, options = arrayOf("Read")) { (player) -> player.itemDialogue(INVITATION_LETTER, "I give permission for the bearer... to use the mine shafts on site.<br><br>- signed Terrance Balando, Archaeological Expert, City of Varrock.") }
    onItemClick(UNIDENTIFIED_LIQUID, options = arrayOf("Empty")) { (player, item) ->
        player.inventory.replaceItem(VIAL, 1, item.slot)
        player.sendMessage("You very carefully empty out the liquid.")
    }
    onItemClick(STONE_TABLET, options = arrayOf("Read")) { (player) -> player.playerDialogue(CALM_TALK, "It says: 'Tremble mortal, before the altar of our dread lord Zaros.'") }
    onItemClick(BOOK_ON_CHEMICALS, options = arrayOf("Read")) { (player) -> player.openBook(BookOnChemicals()) }

    onDropItem(UNIDENTIFIED_LIQUID, NITROGLYCERIN, MIXED_CHEMICALS, MIXED_CHEMICALS_CHARCOAL, CHEMICAL_COMPOUND) { e ->
        when (e.item.id) {
            UNIDENTIFIED_LIQUID -> TheDigSiteUtils(e.player).dropLiquid(e.item, 25)
            NITROGLYCERIN -> TheDigSiteUtils(e.player).dropLiquid(e.item, 35)
            MIXED_CHEMICALS -> TheDigSiteUtils(e.player).dropLiquid(e.item, 45)
            MIXED_CHEMICALS_CHARCOAL -> TheDigSiteUtils(e.player).dropLiquid(e.item, 55)
            CHEMICAL_COMPOUND -> TheDigSiteUtils(e.player).dropLiquid(e.item, 65)
        }
        e.cancelDrop()
    }
    onDropItem(STONE_TABLET) { e ->
        e.player.vars.setVarBit(TABLET_VB, 0)
    }

    onItemOnNpc(*DIGSITE_WORKMEN.toTypedArray()) { (player, item, npc) -> if (item.id == INVITATION_LETTER) DigsiteWorkmanD(player, npc).showInvitation() }
    onItemOnNpc(ARCHAEOLOGICAL_EXPERT) { (player, item, npc) -> ArchaeologicalExpertD(player, npc).identifyItem(item) }
    onItemOnNpc(*EXAMINER.toTypedArray()) { (player, item, npc) -> if (item.id == ANCIENT_TALISMAN) player.npcDialogue(npc, CALM_TALK, "Wow - that's unusual! You should show it to Terry, the archaeological expert, in the next room.") }
    onItemOnNpc(PANNING_GUIDE) { (player, item, npc) ->
        if (item.id == CUP_OF_TEA) {
            if (!player.isQuestComplete(Quest.DIG_SITE) && !player.questManager.getAttribs(Quest.DIG_SITE).getB(PANNING_GUIDE_GIVEN_TEA))
                PanningGuideD(player, npc)
            else
                player.sendMessage("You've already quenched the panning guide's thirst.")
        }
    }

    onItemOnItem(intArrayOf(AMMONIUM_NITRATE, NITROGLYCERIN, MIXED_CHEMICALS, GROUND_CHARCOAL, MIXED_CHEMICALS_CHARCOAL, ARCENIA_ROOT), intArrayOf(AMMONIUM_NITRATE, NITROGLYCERIN, MIXED_CHEMICALS, GROUND_CHARCOAL, MIXED_CHEMICALS_CHARCOAL, ARCENIA_ROOT)) { e ->
        if (e.player.skills.getLevel(Skills.HERBLORE) < 10) {
            e.player.sendMessage("You need at least level 10 Herblore to combine the chemicals.")
            return@onItemOnItem
        }
        val itemUsed = e.item1.id
        val itemWith = e.item2.id
        when {
            (itemUsed == AMMONIUM_NITRATE && itemWith == NITROGLYCERIN) || (itemUsed == NITROGLYCERIN && itemWith == AMMONIUM_NITRATE) -> {
                e.player.sendMessage("You mix the nitrate powder into the liquid.")
                e.player.sendMessage("It has produced a foul mixture.")
                e.player.inventory.deleteItem(itemUsed, 1)
                e.player.inventory.deleteItem(itemWith, 1)
                e.player.inventory.addItem(MIXED_CHEMICALS)
                e.player.skills.addXp(Skills.HERBLORE, 2.0)
            }
            (itemUsed == MIXED_CHEMICALS && itemWith == GROUND_CHARCOAL) || (itemUsed == GROUND_CHARCOAL && itemWith == MIXED_CHEMICALS) -> {
                e.player.sendMessage("You mix the charcoal into the liquid.")
                e.player.sendMessage("It has produced an even fouler mixture.")
                e.player.inventory.deleteItem(itemUsed, 1)
                e.player.inventory.deleteItem(itemWith, 1)
                e.player.inventory.addItem(MIXED_CHEMICALS_CHARCOAL)
                e.player.skills.addXp(Skills.HERBLORE, 2.0)
            }
            (itemUsed == MIXED_CHEMICALS_CHARCOAL && itemWith == ARCENIA_ROOT) || (itemUsed == ARCENIA_ROOT && itemWith == MIXED_CHEMICALS_CHARCOAL) -> {
                e.player.sendMessage("You mix the root into the liquid.")
                e.player.sendMessage("You produce a potentially explosive compound.")
                e.player.inventory.deleteItem(itemUsed, 1)
                e.player.inventory.deleteItem(itemWith, 1)
                e.player.inventory.addItem(CHEMICAL_COMPOUND)
                e.player.skills.addXp(Skills.HERBLORE, 2.0)
            }
            else -> e.player.sendMessage("Nothing interesting happens.")
        }
    }

    onLogin { e ->
        if (e.player.prayer.isCurses && !e.player.isQuestComplete(Quest.TEMPLE_AT_SENNTISTEN)) {
            e.player.sendMessage("<col=FF0000>You feel as though your prayers have been ignored.</col>")
            e.player.sendMessage("<col=FF0000>You do not meet the requirements to use the Ancient Curses prayer book.</col>")
            e.player.prayer.setPrayerBook(false)
        }
    }

}
