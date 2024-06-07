package com.rs.game.content.quests.elderkiln

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val TZHAAR_MEJ_JEH_BPOOL = 15161
const val TZHAAR_MEJ_JEH_LIBRARY = 15163
const val TZHAAR_MEJ_AK_BPOOL = 15164
const val TZHAAR_MEJ_JEH_AK_PLAZA = 15165

@ServerStartupEvent
fun mapMejJehDialogues() {
    onNpcClick(TZHAAR_MEJ_JEH_BPOOL, TZHAAR_MEJ_AK_BPOOL) { (player) -> mejJahBirthingPoolDialogue(player) }
    onNpcClick(TZHAAR_MEJ_JEH_AK_PLAZA) { (player, npc) -> mejDialogueCenterRing(player, npc) }
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
        else -> mejJahDialoguePostQuest(player, npc)
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