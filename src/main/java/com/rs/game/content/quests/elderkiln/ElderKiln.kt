package com.rs.game.content.quests.elderkiln

import com.rs.engine.dialogue.sendOptionsDialogue
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.skills.magic.Magic
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onNpcClick

const val TZHAAR_MEJ_JEH_BPOOL = 15161
const val TZHAAR_MEJ_JEH = 15163
const val TZHAAR_MEJ_AK_BPOOL = 15164

const val TOKKUL = 6529
const val TOKKUL_ZO_CHARGED = 23643
const val TOKKUL_ZO_UNCHARGED = 23644
val TOKKUL_ZO_TELEPORTS = arrayOf(Tile.of(4744, 5156, 0), Tile.of(4599, 5062, 0), Tile.of(4613, 5128, 0), Tile.of(4744, 5170, 0))

const val STAGE_UNSTARTED = 0
const val STAGE_HATCH_EGG = 1
const val STAGE_GAAL_TO_LIBRARY = 2
const val STAGE_COMPLETE = 45

@QuestHandler(
    quest = Quest.ELDER_KILN,
    startText = "Speak to TzHaar-Mej-Jeh by the Birthing Pools in the TzHaar City.",
    itemsText = "Adamant pickaxe (or better), melee, magic, or ranged armor, weapons, potins, and food.",
    combatText = "Several waves of Tok-Haar creatures and a few high level players.",
    rewardsText =
            "100,000 XP lamp (for either Attack<br>" +
            "Strength, Defence, Magic, or Ranged)<br>" +
            "50,000 Magic XP<br>" +
            "30,000 Agility XP<br>" +
            "Tokkul-Zo (boosts 10% damage when<br>" +
            "fighting against lava or obsidian creatures)<br>" +
            "Access to the Fight Kiln",
    completedStage = STAGE_COMPLETE
)
class ElderKiln : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("I should speak to TzHaar-Mej-Jeh by the Birthing Pools in the TzHaar City.")
        STAGE_HATCH_EGG -> listOf("I need to help the TzHaar-Mej hatch their egg by regulating the temperature.")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.MAGIC, 50000.0)
        player.skills.addXpQuest(Skills.AGILITY, 30000.0)
        player.inventory.addItemDrop(Item(23643, 1).addMetaData("tzhaarCharges", 4000))
        player.inventory.addItemDrop(23645, 1)
        sendQuestCompleteInterface(player, 23643)
    }

    override fun updateStage(player: Player, stage: Int) {
        when(stage) {
            STAGE_GAAL_TO_LIBRARY -> {
                player.vars.setVarBit(10809, 25)
            }

        }
    }
}

@ServerStartupEvent
fun mapElderKiln() {
    onItemClick(TOKKUL_ZO_CHARGED, options = arrayOf("Check-charge", "Check-charges", "Teleport")) { e ->
        if (!e.player.isQuestComplete(Quest.ELDER_KILN, "to use the Tokkul-Zo.")) return@onItemClick
        if (e.option == "Teleport") {
            if (e.isEquipped) {
                Magic.sendNormalTeleportSpell(e.player, TOKKUL_ZO_TELEPORTS[2]) { depleteTokkulZo(e.player) }
            } else e.player.sendOptionsDialogue("Where would you like to teleport?") {
                opExec("Main Plaza") { Magic.sendNormalTeleportSpell(e.player, TOKKUL_ZO_TELEPORTS[0]) { depleteTokkulZo(e.player) } }
                opExec("Fight Pits") { Magic.sendNormalTeleportSpell(e.player, TOKKUL_ZO_TELEPORTS[1]) { depleteTokkulZo(e.player) } }
                opExec("Fight Caves") { Magic.sendNormalTeleportSpell(e.player, TOKKUL_ZO_TELEPORTS[2]) { depleteTokkulZo(e.player) } }
                opExec("Fight Kiln") { Magic.sendNormalTeleportSpell(e.player, TOKKUL_ZO_TELEPORTS[3]) { depleteTokkulZo(e.player) } }
            }
        } else e.player.sendMessage("Your Tokkul-Zo has " + e.item.getMetaDataI("tzhaarCharges") + " charges left.")
    }

    onNpcClick(TZHAAR_MEJ_JEH_BPOOL, TZHAAR_MEJ_AK_BPOOL) { (player) -> mejJahBirthingPoolDialogue(player) }
}

fun rechargeTokkulZo(player: Player) {
    var ring = player.getItemWithPlayer(TOKKUL_ZO_UNCHARGED)
    if (ring != null) {
        val chargesToAdd = Utils.clampI(player.inventory.getAmountOf(TOKKUL) / 16, 0, 4000)
        if (chargesToAdd > 0) {
            player.inventory.deleteItem(TOKKUL, chargesToAdd * 16)
            ring.id = TOKKUL_ZO_CHARGED
            ring.addMetaData("tzhaarCharges", chargesToAdd)
            player.inventory.refresh()
            player.sendMessage("TzHaar-Mej-Jeh adds " + chargesToAdd + " charges to your ring in exchange for " + Utils.formatNumber(chargesToAdd * 16) + " Tokkul.")
        }
        return
    }
    ring = player.getItemWithPlayer(TOKKUL_ZO_CHARGED)
    if (ring != null) {
        val charges = ring.getMetaDataI("tzhaarCharges", -1)
        val chargesToAdd = Utils.clampI(player.inventory.getAmountOf(TOKKUL) / 16, 0, 4000 - charges)
        if (chargesToAdd > 0) {
            player.inventory.deleteItem(TOKKUL, chargesToAdd * 16)
            ring.addMetaData("tzhaarCharges", charges + chargesToAdd)
        }
        player.sendMessage("TzHaar-Mej-Jeh adds " + chargesToAdd + " charges to your ring in exchange for " + Utils.formatNumber(chargesToAdd * 16) + " Tokkul.")
    }
}

fun depleteTokkulZo(player: Player): Boolean {
    val ring = player.getItemWithPlayer(TOKKUL_ZO_CHARGED)
    if (ring != null && ring.id == TOKKUL_ZO_CHARGED) {
        val charges = ring.getMetaDataI("tzhaarCharges", -1)
        if (charges <= 1) {
            ring.id = TOKKUL_ZO_UNCHARGED
            ring.deleteMetaData()
            player.equipment.refresh(Equipment.RING)
            player.inventory.refresh()
            player.sendMessage("<col=FF0000>Your Tokkul-Zo has degraded and requires recharging.")
            return false
        }
        ring.addMetaData("tzhaarCharges", charges - 1)
        return true
    }
    return false
}