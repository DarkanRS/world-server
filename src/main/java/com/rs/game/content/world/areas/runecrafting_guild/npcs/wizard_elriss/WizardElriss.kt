package com.rs.game.content.world.areas.runecrafting_guild.npcs.wizard_elriss

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.world.areas.runecrafting_guild.objects.Talismans
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

enum class OmniTalisman(
    val type: String,
    val talismanId: Int,
    val tiaraId: Int,
    val xp: Double
) {
    AIR("Air", 1438, 5527, 8.0),
    MIND("Mind", 1448, 5529, 9.0),
    WATER("Water", 1444, 5531, 11.0),
    EARTH("Earth", 1440, 5535, 17.0),
    FIRE("Fire", 1442, 5537, 28.0),
    BODY("Body", 1446, 5533, 49.0),
    COSMIC("Cosmic", 1454, 5539, 110.0),
    CHAOS("Chaos", 1452, 5543, 216.0),
    NATURE("Nature", 1462, 5541, 531.0),
    LAW("Law", 1458, 5545, 1428.0),
    DEATH("Death", 1456, 5547, 4241.0),
    BLOOD("Blood", 1450, 5549, 6958.0);

    companion object {
        private val BY_ITEM_ID = HashMap<Int, OmniTalisman>()
        init {
            for (talisman in entries) {
                BY_ITEM_ID[talisman.talismanId] = talisman
            }
        }
    }
}

class WizardElriss(var player: Player, var npc: NPC) {
    init {
        player.startConversation {
            npc(npc, HAPPY_TALKING, "Welcome to the Runecrafting Guild.")
            label("initialOps")
            options {
                op("What is this place?") {
                    player(CONFUSED, "What is this place?")
                    npc(npc, HAPPY_TALKING, "This is the Runecrafting Guild, as I said. After the secret of Runecrafting was re-discovered, I set up the guild as a place for the most advanced runecrafters to work together.")
                    goto("initialOps")
                }
                op("What can I do here?") {
                    player(CONFUSED, "What can I do here?")
                    npc(npc, CALM_TALK, "Wizard Acantha and Wizard Vief are running The Great Orb Project. It requires large numbers of runecrafters, so you should speak with them if you want something to do.")
                    npc(npc, CALM_TALK, "Wizard Korvak has visited the Abyss and can repair abyssal pouches, I, myself, am working on a new kind of talisman: the omni-talisman.")
                    label("whatCanIDoHereOptions")
                    options {
                        op("Tell me about Acantha and Vief's project.") { greatOrbProjectDialogue(this) }
                        op("Tell me about the omni-talisman.") {
                            player(CALM_TALK, "Tell me about the omni-talisman.")
                            npc(npc, CALM_TALK, "Ever since the Duke of Lumbridge sent the first air talisman to Sedridor, I have studied the talismans in great detail. I believe I can create a new form of talisman that combines the properties of all of them.")
                            npc(npc, CALM_TALK, "The omni-talisman will allow you to access any of the Runecrafting altars. It can be combined with a tiara or a staff, just like an ordinary talisman.")
                            npc(npc, CALM_TALK, "If you show me each type of known talisman, I will create an omni-talisman for you. For each talisman you show me, I will also teach you a bit about Runecrafting.")
                            options {
                                val hasUnshownTalisman = forEachTalismanOrTiara(
                                    predicate = { talisman, _ ->
                                        val shownTalismans = player.getO<List<String>>("omniTalisman_shownTypes")?.toSet() ?: emptySet()
                                        !shownTalismans.contains(talisman.type)
                                    }, action = { _, _ -> })
                                if (hasUnshownTalisman) {
                                    opExec("I have a talisman to show you.") { handleShowTalisman() }
                                } else {
                                    opExec("What talismans do I need to show you still?") { handleShowTalisman() }
                                }
                                op("Never mind.") {
                                    player(CALM_TALK, "Never mind.")
                                    goto("whatCanIDoHereOptions")
                                }
                            }
                        }
                        op("Tell me about Wizard Korvak's pouch repairs.") {
                            player(CALM_TALK, "Tell me about Wizard Korvak's pouch repairs.")
                            npc(npc, CALM_TALK, "Wizard Korvak is the only one of us to have visited the Abyss. He learned about rune pouches and how to repair them. None of us quite knows how he does it, and I'm not sure he does either, but it seems to work.")
                        }
                        op("Never mind.") { player(CALM_TALK, "Never mind.") }
                    }
                }
                op("Who is that wizard outside the guild portal?") {
                    npc(npc, CALM_TALK, "Ah you have met Wizard Finix; a promising young wizard. Even if he does have some rather controversial theories.")
                    player(CALM_TALK, "What theories has he come up with?")
                    npc(npc, CALM_TALK, "His boldest theory is that the altars are not endless pools of energy, as we though they were. His research notes seem to be in order, but I find myself seriously questioning the validity of that statement.")
                    player(CALM_TALK, "Why?")
                    npc(npc, CALM_TALK, "Many of the current theories on the altars are based on the long-held assumption that the altars are everlasting. If that assumption is proven to be untrue, it sets our work back by quite a margin.")
                    npc(npc, CALM_TALK, "The trouble is, the implications of him being correct are so great we cannot ignore his theories until we have either proven or disproven them.")
                    player(CALM_TALK, "What implications are these?")
                    npc(npc, CALM_TALK, "The human ability to perform magic is something that helps protect us from those that wish us harm. Without runes, we would not be able to perform magic, and that would be a rather dire situation.")
                    player(CALM_TALK, "Is there nothing we can do?")
                    npc(npc, CALM_TALK, "Wizard Finix has come up with a proposed solution. He has come up with a method of siphoning runic energy from sources other than altars.")
                    npc(npc, CALM_TALK, "Wizard Finix believes that the world around us is made up of runic energy, but his studies came to a halt when he failed to siphon runic energy from Gielinor.")
                    player(CALM_TALK, "Do we know why it didn't work?")
                    npc(npc, CALM_TALK, "Nobody knows for sure. Some believe that his theory is incorrect. Other believe that it could be correct, but that it would take a runecrafter of immense talent to siphon runic energy from Gielinor itself.")
                    npc(npc, CALM_TALK, "Whoever is right, I have found myself with no choice but to open the Runespan up for investigation into this siphoning method.")
                    player(CALM_TALK, "The Runespan?")
                    npc(npc, CALM_TALK, "The Runespan is a plane in which the runecrafting Guild is situated. It is a chaotic plane with a runic energy flying around, unbound to anything, which makes it perfect for siphoning training.")
                    npc(npc, CALM_TALK, "Hopefully, if we can train up enough runecrafters, one day we may be able to siphon energy from Gielinor.")
                    player(CALM_TALK, "I had better get out there and start training, then!")
                    npc(npc, CALM_TALK, "Your efforts would be very much appreciated, ${player.displayName}.")
                    goto("initialOps")
                }
                op("Never mind.") { player(CALM_TALK, "Never mind.") }
            }
        }
    }

    private fun forEachTalismanOrTiara(predicate: (OmniTalisman, Boolean) -> Boolean, action: (OmniTalisman, Boolean) -> Unit): Boolean {
        val typesEncountered = mutableSetOf<String>()
        var found = false

        for (talisman in OmniTalisman.entries) {
            val isTalisman = player.inventory.containsOneItem(talisman.talismanId)
            val isTiara = player.inventory.containsOneItem(talisman.tiaraId)

            if (isTalisman || isTiara) {
                val isEncountered = talisman.type in typesEncountered
                if (!isEncountered) {
                    if (predicate(talisman, isTalisman)) {
                        action(talisman, isTalisman)
                        typesEncountered.add(talisman.type)
                        found = true
                    }
                }
            }
        }
        return found
    }

    private fun handleShowTalisman() {
        val allTalismanTypes = OmniTalisman.entries.map { it.type }.toSet()
        val shownTalismans = player.getO<List<String>>("omniTalisman_shownTypes")?.toMutableSet() ?: mutableSetOf()

        val talismansToShow = mutableListOf<OmniTalisman>()
        forEachTalismanOrTiara(
            predicate = { talisman, _ -> !shownTalismans.contains(talisman.type) },
            action = { talisman, _ -> talismansToShow.add(talisman) }
        )

        player.startConversation {
            talismansToShow.forEach { talisman ->
                val itemName = if (player.inventory.containsOneItem(talisman.talismanId)) "talisman" else "tiara"
                item(if (itemName == "talisman") talisman.talismanId else talisman.tiaraId, "You show Elriss the ${talisman.type.lowercase()} $itemName.") {
                    shownTalismans.add(talisman.type)
                    player.skills.addXp(Skills.RUNECRAFTING, talisman.xp)
                    player.save("omniTalisman_shownTypes", shownTalismans.toList())
                }
            }
            exec {
                val remainingTalismans = allTalismanTypes - shownTalismans
                if (remainingTalismans.isNotEmpty()) {
                    val remainingTalismansList = when (remainingTalismans.size) {
                        1 -> remainingTalismans.first()
                        2 -> remainingTalismans.joinToString(" & ")
                        else -> {
                            val remainingList = remainingTalismans.toList()
                            val allButLast = remainingList.dropLast(1).joinToString(", ")
                            "$allButLast & ${remainingList.last()}"
                        }
                    }
                    player.startConversation {
                        npc(npc, CALM_TALK, "You're yet to show me these talismans:<br>$remainingTalismansList")
                        player.sendMessage("You're yet to show Wizard Elriss these talismans:<br>$remainingTalismansList")
                        npc(npc, HAPPY_TALKING, "Once you've shown me them all, I can give you an omni-talisman.")
                    }
                } else {
                    player.startConversation {
                        npc(npc, HAPPY_TALKING, "Excellent! You've shown me enough talismans. I can give you an omni-talisman now.")
                        if (player.inventory.hasFreeSlots()) {
                            item(Talismans.OMNI.talismanId, "Wizard Elriss gives you an omni-talisman.") {
                                player.inventory.addItem(Talismans.OMNI.talismanId)
                            }
                            npc(npc, CALM_TALK, "Take good care of it. It is a powerful artefact and may only be used by experienced runecrafters, such as yourself.")
                        } else {
                            item(Talismans.OMNI.talismanId, "Wizard Elriss shows you an omni-talisman, but you don't have enough room to take it.")
                        }
                    }
                }
            }
        }
    }

    private fun greatOrbProjectDialogue(dialogue: DialogueBuilder) {
        dialogue.player(CALM_TALK, "Tell me about Acantha and Vief's project.")
        dialogue.npc(npc, CALM_TALK, "The Orb Proj...I beg your pardon, The Great Orb Project? It's truly fascinating. Wizards Acantha and Vief have found that energy leaks out of some of the Runecrafting altars. They are recruiting teams of experienced")
        dialogue.npc(npc, CALM_TALK, "runecrafters such as yourself, to force the energy back in.")
        dialogue.label("greatOrbProjectInstructions")
        dialogue.npc(npc, CALM_TALK, "Join one of the teams by speaking to Wizard Acantha of Wizard Vief. When the wizards have enough helpers, I will open a portal to the Air Altar.")
        dialogue.npc(npc, CALM_TALK, "The energy appears in the form of floating orbs. These can be moved by means of wands that attract or repel them. Acantha or Vief will give you one of each wand.")
        dialogue.npc(npc, CALM_TALK, "Your goal is to move the correct colour orb to the altar stone, while keeping the other orbs away. Wizard Acantha favours green orbs, while Wizard Vief favours yellow ones.")
        dialogue.npc(npc, CALM_TALK, "You will also have a third magic wand, which allows you to create magical barriers to block the opposing team's orbs.")
        dialogue.npc(npc, CALM_TALK, "After two minutes the team that absorbed the most orbs wins that altar. I then open the portal to the next altar in the sequence. After you have visited all eight altars, you will be returned here.")
        dialogue.label("greatOrbProjectInitialOptions")
        dialogue.options {
            op("What's in it for me?") {
                player(CALM_TALK, "What's in it for me?")
                npc(npc, CALM_TALK, "A fair question. We have agreed on a token scheme that allows you to choose from several rewards. When you return from the last altar, your senior wizard will give you a number of tokens.")
                npc(npc, CALM_TALK, "You will get 50 tokens per altar that your team captured, provided that you contributed to the capture in some way. You will get an extra 100 tokens if your team captured more altars overall, or 50 extra if it is a draw.")
                npc(npc, CALM_TALK, "You can exchange the tokens for rewards by speaking to me.")
                npc(npc, CALM_TALK, "You may also find rune essence appearing in your inventory at the end of each round. This is a side-product of the absorption process and you are free to use it as you wish.")
                options {
                    op("What rewards are there?") {
                        player(CALM_TALK, "What rewards are there?")
                        npc(npc, CALM_TALK, "The rewards include runemaster robes, designed to protect you while Runecrafting. These robes also let you move orbs a little further - if you wear robes of the same colour as the orb.")
                        npc(npc, CALM_TALK, "Another reward is the Runecrafting staff. This cam be combined with a talisman, in the same way that a tiara can.")
                        npc(npc, CALM_TALK, "I also offer teleport tablets to the various altars.")
                        npc(npc, CALM_TALK, "You may also trade your tokens in for talismans and certificates you can exchange at a bank for rune essence.")
                        goto("greatOrbProjectInitialOptions")
                    }
                }
            }
            op("Could you go over the instructions again?") {
                player(CALM_TALK, "Could you go over the instructions again?")
                goto("greatOrbProjectInstructions")
            }
            op("Which colour orb is best?") {
                player(CALM_TALK, "Which colour orb is best?")
                npc(npc, CALM_TALK, "Wizard Acantha believes that the green orbs are best. Wizard Vief believe that the yellow ones are. You should help out the wizard whose team you join.")
                player(CALM_TALK, "But what do you think?")
                npc(npc, CALM_TALK, "Does it matter?")
                options {
                    op("Of course it matters.") {
                        player(CALM_TALK, "Of course it matters.")
                        npc(npc, CALM_TALK, "Of course it does, of course it does. Be careful which team you join, then. I'll accept your reward tokens, either way.")
                        goto("initialOps")
                    }
                    op("No, I suppose not.") {
                        player(CALM_TALK, "No, I suppose not.")
                        npc(npc, CALM_TALK, "No. The important thing is that the orbs get pushed back into the altars, whatever colour they are.")
                        goto("initialOps")
                    }
                    op("Never mind.") { player(CALM_TALK, "Never mind.") }
                }
            }
            op("Thanks.") { player(CALM_TALK, "Thanks.") }
        }
    }
}

@ServerStartupEvent
fun mapWizardElriss() {
    onNpcClick(8032) { (player, npc) -> WizardElriss(player, npc) }
}
