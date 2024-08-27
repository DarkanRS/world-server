package com.rs.game.content.quests.elderkiln

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.dialogue
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val TZHAAR_MEJ_JEH_BPOOL = 15161
const val TZHAAR_MEJ_JEH_PLAZA = 15162
const val TZHAAR_MEJ_JEH_LIBRARY = 15163
const val TZHAAR_MEJ_AK_BPOOL = 15164
const val TZHAAR_MEJ_JEH_AK_PLAZA = 15165

@ServerStartupEvent
fun mapMejJehDialogues() {
    onNpcClick(TZHAAR_MEJ_JEH_BPOOL, TZHAAR_MEJ_AK_BPOOL) { (player) -> mejJahBirthingPoolDialogue(player) }
    onNpcClick(TZHAAR_MEJ_JEH_AK_PLAZA, TZHAAR_MEJ_JEH_PLAZA) { (player, npc) -> mejDialogueCenterRing(player, npc) }
}

private fun mejJahBirthingPoolDialogue(player: Player) {
    player.startConversation {
        when(player.getQuestStage(Quest.ELDER_KILN)) {
            STAGE_UNSTARTED -> {
                npc(TZHAAR_MEJ_JEH_BPOOL, T_SAD, "JalYt-Hur-${player.displayName}, help. My egg is dying.")
                player(CONFUSED, "What's happened to it?")
                npc(TZHAAR_MEJ_JEH_BPOOL, T_CALM_TALK, "The egg is cold. Our Mej have tried to heat it, but they say it is too late to save it.")
                npc(TZHAAR_MEJ_JEH_BPOOL, T_CALM_TALK, "But they are wrong. Although many TzHaar eggs are dying now or being born...different, this is big strong egg. It will grow into good strong TzHaar. I just need help. Will you aid us?")
                questStart(Quest.ELDER_KILN)
                player(CALM_TALK, "I'll help.")
                npc(TZHAAR_MEJ_JEH_BPOOL, T_CALM_TALK, "You would make a good TzHaar, JalYt-Hur-${player.displayName}.")
                npc(TZHAAR_MEJ_JEH_BPOOL, T_CONFUSED, "TzHaar-Mej-Ak. Will you help us?")
                npc(TZHAAR_MEJ_AK_BPOOL, T_SAD, "TzHaar-Mej-Jeh, it is too late...")
                npc(TZHAAR_MEJ_AK_BPOOL, T_CALM_TALK, "...but I will help, even just to show you this.")
                npc(TZHAAR_MEJ_JEH_BPOOL, T_CALM_TALK, "We must work together to make the egg hatch. Use fire magic to heat the egg and water magic to cool it down. When egg is at right temperature, it will start to hatch.") {
                    player.setQuestStage(Quest.ELDER_KILN, STAGE_HATCH_EGG)
                }
                simple("Aim to keep the heat between the two pointers on the temperature gauge. When the egg's heat is within these two pointers, the hatching percentage will increase. When the egg's heat is outside of these two pointers, the hatching percentage will decrease.")
                item(554, "TzHaar-Mej-Jah hands you a pile of runes.")
                exec { hatchEggCutscene(player) }
            }
            STAGE_HATCH_EGG -> {
                simple("Aim to keep the heat between the two pointers on the temperature gauge. When the egg's heat is within these two pointers, the hatching percentage will increase. When the egg's heat is outside of these two pointers, the hatching percentage will decrease.")
                item(554, "TzHaar-Mej-Jah hands you a pile of runes.")
                exec { hatchEggCutscene(player) }
            }
        }
    }
}

private fun mejDialogueCenterRing(player: Player, npc: NPC) {
    when(player.getQuestStage(Quest.ELDER_KILN)) {
        STAGE_SAVE_GAAL_FIGHTPITS -> saveGaalFightPitsAkDialogue(player, npc)
        STAGE_WRAP_UP_FIGHT_PITS -> wrapUpFightPits(player, npc)
        STAGE_GO_TO_KILN -> escortGaalThroughKiln(player, npc)
        else -> mejJahDialoguePostQuest(player, npc)
    }
}

private fun wrapUpFightPits(player: Player, npc: NPC) {
    val instructKiln = dialogue {
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "This Ga'al that survived the Fight Pit – it can go with you. It will meet you at entrance.")
        player(CONFUSED, "Shouldn't you ask the Ga'al if he wants to go?")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "It only Ga'al.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "No, ${player.displayName} is right. Ga'al must have its say.")
        npc(GAAL_XOX, T_CALM_TALK, "If this make Ga'al-Xox real TzHaar, Ga'al-Xox go.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "Here is TzHaar-Ket-Yit'tal's TokKul. Take it with you to the Kiln. We shall speak to the other TzHaar-Mej about our plan whilst you are away.")
        player(CONFUSED, "What should we do when we arrive at the Kiln?")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "Kiln is very old. The lava inside is powerful and ancient. That we know. Lava will be able to join Ga'al and memories inside TokKul as one.")
        player(CONFUSED, "Is there anything I should know about the Kiln?")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "It is where TzHaar came from in the beginning. We protect it, but do not go inside. It is sacred ground. TzHaar have not even walked down the tunnels leading to Kiln for very long time. They may have become dangerous.") {
            player.setQuestStage(Quest.ELDER_KILN, STAGE_GO_TO_KILN)
            if (!player.containsItem(23647))
                player.inventory.addItem(23647)
        }
    }
    player.startConversation {
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "Now, champion TzHaar-Ket-Yit'tal is dead, TzHaar-Mej-Jeh. Like you, his memories are lost. He did not lay an egg.")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "You and your Jal'Yt have murdered a good TzHaar. His memories die with him!")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "He died in the Pit. Death in the Pits is not murder, and, if you listened to us, this would not have happened. I have a plan, TzHaar-Mej-Ak...")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "Enough of this madness! Your Ga'al is dead! These plans, these schemes... they are not work of good TzHaar. Our Champion is dead, because of your plans.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "There is a way to bring the Champion back! ${player.displayName}, what do you know about Tokkul?")
        cosmeticOptions(
            "It's the remains of your dead that you use as currency.",
            "It's a type of money, right?",
            "Not that much."
        )
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "When TzHaar die we turn into Tokkul; small rocks with our memories trapped inside.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "Our memories make it valuable, so TzHaar trade it with each other as currency.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "It has never been possible to get memories from TokKul. And TzHaar who have not laid eggs before they turn to TokKul... their memories are lost forever.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "What would you say if I told you there was a way of recovering these memories?")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "It – it is not possible.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "It is! Our Kiln has that power, TzHaar-Mej-Ak. The power to forge TokKul, fusing them with the body of another. The Ga'al are empty bodies that we can use.")
        player(CONFUSED, "Hold on - your Kiln?")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "The Kiln is where TzHaar were first made. Its lava is able to give life and melt down even the hardest of metals.")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "And we are not permitted to visit it! Kiln is sacred to TzHaar. It is out of bounds!")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "But it has the power to join TokKul with Ga'al! To bring back lost memories! The JalYt – it would not have to break our rules – it could go.")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "This is too drastic, TzHaar-Mej-Jeh, we must think on this.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "Think of all the TzHaar born without eggs – all the Ket-Champions, the Mej-Elders whose memories we have in TokKul. We can bring them back.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "What else can we do? More and more Ga'al keep being born. If we cannot pass on our memories then it is the end of the TzHaar.")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "You are... right, TzHaar-Mej-Jeh, we must take action, but only if the JalYt will help. TzHaar will not step foot in there.")
        options {
            op("This only helps the TzHaar, not the Ga'al!") {
                npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "Once Ga'als have memories of TzHaar, they will be able to work and be real TzHaar. This will help both the Ga'al and the TzHaar.")
                player(CALM_TALK, "Fine, I'll help, but only for the good of the Ga'al.")
                jump(instructKiln)
            }

            op("Of course I'll help the TzHaar.") {
                npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "You good ally of TzHaar, JalYt.")
                npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "You must journey to the Kiln. You will need to travel through many tunnels, east of Main Plaza. Just south of Birthing Pool there is passage heading to the Kiln entrance. Guards will let you pass.")
                jump(instructKiln)
            }

            op("The kiln sounds powerful – I'm there.") {
                npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "Be careful - Kiln is sacred. Do not think you can use it for your own means, JalYt. Do just what we say or regret it.")
                jump(instructKiln)
            }

            op("I can't be part of this right now.") {
                npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "That is your choice, JalYt.")
            }
        }
    }
}

private fun escortGaalThroughKiln(player: Player, npc: NPC) {
    player.startConversation {
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "This Ga'al that survived the Fight Pit – it can go with you. It will meet you at entrance.")
        player(CONFUSED, "Shouldn't you ask the Ga'al if he wants to go?")
        npc(TZHAAR_MEJ_JEH_AK_PLAZA, T_CALM_TALK, "It only Ga'al.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "No, ${player.displayName} is right. Ga'al must have its say.")
        npc(GAAL_XOX, T_CALM_TALK, "If this make Ga'al-Xox real TzHaar, Ga'al-Xox go.")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "Here is TzHaar-Ket-Yit'tal's TokKul. Take it with you to the Kiln. We shall speak to the other TzHaar-Mej about our plan whilst you are away.") {
            if (!player.containsItem(23647))
                player.inventory.addItem(23647)
        }
        player(CONFUSED, "What should we do when we arrive at the Kiln?")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "Kiln is very old. The lava inside is powerful and ancient. That we know. Lava will be able to join Ga'al and memories inside TokKul as one.")
        player(CONFUSED, "Is there anything I should know about the Kiln?")
        npc(TZHAAR_MEJ_JEH_PLAZA, T_CALM_TALK, "It is where TzHaar came from in the beginning. We protect it, but do not go inside. It is sacred ground. TzHaar have not even walked down the tunnels leading to Kiln for very long time. They may have become dangerous.")
    }
}

private fun mejJahDialoguePostQuest(player: Player, npc: NPC) {
    player.startConversation {
        npc(npc.id, T_CONFUSED, "What do you need from me?")
        options {
            val recTZ = player.getBool("recTokkulZo")
            if (!player.containsAnyItems(TOKKUL_ZO_UNCHARGED, TOKKUL_ZO_CHARGED)) if (player.isQuestComplete(Quest.ELDER_KILN, "to obtain a Tokkul-Zo.")) {
                op("Can I have a Tokkul-Zo?" + (if (recTZ) " I've lost mine." else "")) {
                    player(CONFUSED, "Can I have a Tokkul-Zo?" + (if (player.getBool("recTokkulZo")) " I've lost mine." else ""))
                    npc(npc.id, T_CALM_TALK, "Alright, you have proven yourself. Try not to lose it." + (if (recTZ) "" else " As this is your first time receiving the ring, I have fully charged it for you for free."))
                    player(CHEERFUL, "Thank you!")
                    item(TOKKUL_ZO_CHARGED, "TzHaar-Mej-Jeh hands you a ring. It is extremely hot.") {
                        if (!player.inventory.hasFreeSlots()) return@item player.sendMessage("You don't have enough inventory space.")
                        if (!recTZ) {
                            player.inventory.addItem(Item(TOKKUL_ZO_CHARGED).addMetaData("tzhaarCharges", 4000))
                            player.save("recTokkulZo", true)
                        } else player.inventory.addItem(TOKKUL_ZO_UNCHARGED)
                    }
                }
            }

            op("About the Tokkul-Zo") {
                npc(npc.id, T_CONFUSED, "You want to know more about Tokkul-Zo?")
                player(CONFUSED, "Yes, what does it do?")
                npc(npc.id, T_CALM_TALK, "This ring has a piece of Tokkul in it. When worn, it will guide your hand and make you better when fighting TzHaar, fire creatures, and maybe even TokHaar.")
                player(CONFUSED, "How does it do that?")
                npc(npc.id, T_CALM_TALK, "My magic taps into the memories in the ring, so you better at fighting like a TzHaar. The magic will fade after time. When this happens, return to me and I will recharge it for you... for a price.")
                player(CONFUSED, "What's your price?")
                npc(npc.id, T_CALM_TALK, "48,000 Tokkul for a full recharge. Normally I would do it for free, but we need all the Tokkul we can get, so they we can melt it down in the sacred lave, and release our ancestors from their suffering.")
            }

            if (player.getItemWithPlayer(TOKKUL_ZO_UNCHARGED) != null || player.getItemWithPlayer(TOKKUL_ZO_CHARGED) != null) {
                op("Recharging the Tokkul-Zo") {
                    player(CONFUSED, "Could you please recharge my ring?")
                    npc(npc.id, T_CALM_TALK, if (player.inventory.containsItem(TOKKUL, 16)) "Of course. Here you go." else "You don't have enough Tokkul with you.") { rechargeTokkulZo(player) }
                }
            }
        }
    }
}