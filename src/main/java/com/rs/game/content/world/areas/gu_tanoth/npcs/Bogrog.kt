package com.rs.game.content.world.areas.gu_tanoth.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.Options
import com.rs.engine.dialogue.startConversation
import com.rs.game.content.interfacehandlers.ItemSelectWindow.Mode
import com.rs.game.content.interfacehandlers.ItemSelectWindow.openItemSelectWindow
import com.rs.game.content.skills.summoning.PouchMaterialList.getItemsForPouchId
import com.rs.game.content.skills.summoning.Scroll
import com.rs.game.content.world.areas.gu_tanoth.npcs.Bogrog.Companion.openSwapInterface
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub.ALL_GAME_TABS
import com.rs.lib.game.Item
import com.rs.lib.net.ClientPacket
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler
import kotlin.math.ceil
import kotlin.math.floor

class Bogrog() {

    fun talk(player: Player, npc: NPC) {
        player.startConversation {
            npc(npc, CALM_TALK, "Hey, yooman, what you wanting?")
            label("initialOps")
            options {
                op("So, what's Summoning all about, then?") {
                    npc(npc, CALM_TALK, "Whatchoo talkin' about, yooman?")
                    label("summoningOps")
                    options {
                        op("Tell me about Summoning familiars.") {
                            npc(npc, CALM_TALK, "Summoned familiars are da main ting in Summonin', as if yooman didn't be guessing. There's all kinds of tings yooman can summon. Der bigger der yooman's summonyness, der bigger der tings yooman can summon.")
                            npc(npc, CALM_TALK, "Ting is, dem familiars not really, REALLY animals, if yer get me. Dey are really REALLY spirits dat are stronger than der real animal, wiv all kinds of powaz dat der rest don't.")
                            npc(npc, CALM_TALK, "Dat's why dey are not runnin' around all da time - it's cos der summoner puts dere Summonin' skill points inta summonin' em, and keeping em here.")
                            player(SKEPTICAL_THINKING, "So Summoning skill points are like food to dem - sorry, them?")
                            npc(npc, CALM_TALK, "Yah! Dat's right! Der bigger der ting, der more ya gotta feed it, and der more often it gets hungry. Dat's why only der big summony types, like me, have enough Summonin' skill points for der biggest familiars.")
                            npc(npc, CALM_TALK, "And der bigger der yooman's Summonin' level, der easier it is to be feedin' der littler tings for longer.")
                            player(CALM_TALK, "I'm starting to get a little hungry now.")
                            npc(npc, CALM_TALK, "It's like if yooman be holdin' bags of wheat. Der bigger der bags yooman be holdin', der bigger der yooman's arms get, so de bigger bags dey can hold next time.")
                            npc(npc, CALM_TALK, "If da youman feeds a little ting den dey will have lots of wheat left over too!")
                            player(HAPPY_TALKING, "Great! So, what can these familiars do?")
                            npc(npc, CALM_TALK, "What can dey not do, more like? Dere is a load of dem - tons, lots and lots - and each of dem is not like der uvvers.")
                            player(SKEPTICAL_THINKING, "Well, can you give me some hints, then?")
                            npc(npc, CALM_TALK, "Well, Bogrog only calls on der ones dat fight! Dey hang around until Bogrog be fightin' and den dey leap in! I suppose yooman like you will be wantin' the ones that just make yooman more skilful, or dem ones dat lug around tings dat yooman's arms too puny to lift.")
                            player(AMAZED, "Amazing!")
                            npc(npc, CALM_TALK, "Now, if yooman's brains isn't gonna pop, does it wanna know about someting else?")
                            goto("summoningOps")
                        }
                        op("Tell me about special moves.") {
                            npc(npc, CALM_TALK, "If you cuts up one of dem Summonin' pouches, over at der obelisk, den da energy gets mushed up real good, transformin' into a big stack of scrolls.")
                            npc(npc, CALM_TALK, "Dese scrolls can den be used to make da familiars do a speshial move! Hur, hur, hur! Der spirit wolfies, fer example, dey can make little tings run away real good if dey perform dere Howl speshial move.")
                            player(AMAZED_MILD, "Or longer, in the case of that giant wolpertinger!")
                            npc(npc, CALM_TALK, "Hur hur hur, me hearing about dat from da spirities! Yooman can't mix up der scrolls, though - spirit wolfies get real growly if ya try to make 'em perform dreadfowl speshial move!")
                            player(CONFUSED, "So, what sort of special moves are there?")
                            npc(npc, CALM_TALK, "Dere is lots of dem - like da familiars! If yooman's confoosed, den just think of dis; the attackin' familiars got a lot more of der attackin' speshial moves, and der soppy, non-attackin' ones gots da non-attackin' speshial moves.")
                            npc(npc, CALM_TALK, "Yooman will want da non-attackin' ones to heal dem, or get dem stuff, or make dem better at uvver tings.")
                            player(TALKING_ALOT, "Are the special moves and the familiar's normal abilities similar?")
                            npc(npc, CALM_TALK, "Naah, dem's mostly diff'rent. Da spirit wolfies' are sim'lar though. Dey's got da Howl speshial move, but dey's also got a howly normal ability. You can get der spirit wolfy to use Howl on anyt'ing you see, but it can only uses its howly normal ability on tings you is fightin'.")
                            npc(npc, CALM_TALK, "If der familiar with the normal ability is fighting with a yooman, den it makes der normal ability when it sees an openin'. Der speshial moves though - dey never use dem unless yooman tells 'em to wiv a scroll.")
                            player(HAPPY_TALKING, "I see. Thanks for the information!")
                            npc(npc, CALM_TALK, "Now, if yooman's brains isn't gonna pop, does it wanna know about somefin' else?")
                            goto("summoningOps")
                        }
                        op("Tell me about pets.") {
                            npc(npc, CALM_TALK, "Der petties? Well dey's not real summony stuffs, but if yooman trains hard, then yooman gets to be friends wiv dem. Der summonier der yooman gets, der more like nature der yooman gets, so da little petties like yooman better.")
                            npc(npc, CALM_TALK, "So, der petties dat would be runnin' away don't, and even der nasty petties get real quiet too. An' when dey not runnin' or bitin', den da yooman gets ta feed 'em and pet 'em and keep 'em.")
                            player(CONFUSED, "So what will I need to do to raise the animals?")
                            npc(npc, CALM_TALK, "Dey just walk behind da yooman, so yooman just gives dem food, more food and good foods. Da bestest summoners wants to pet themse'ves a dragon!")
                            player(HAPPY_TALKING, "Wow! Imagine riding around on a dragon, and fighting with it!")
                            npc(npc, CALM_TALK, "Yooman is too stoopid.")
                            player(CONFUSED, "What?")
                            npc(npc, CALM_TALK, "One, you too puny to make a dragon do as you sez. Two, when yooman gets a pet, yooman remembers it's still alive, an' yooman's friend.")
                            npc(npc, CALM_TALK, "Pets are not like der spirity familiars - dey die if you kills dem! What friend pokes his friend in der head and says 'We go fight now. I gets on your back and you fights for me'?")
                            player(CALM_TALK, "I didn't realise.")
                            npc(npc, CALM_TALK, "Dat's 'cos yooman's stoopid. Petties not toys or clubs, or ridy-tings. Dey is livin' tings dat yooman be raisin' from birthyness. If you a bad, petty yooman, I club you!")
                            player(HAPPY_TALKING, "I'll take good care of them.")
                            npc(npc, CALM_TALK, "Now, if yooman's brains isn't gonna pop, does it wanna know about somefin' else?")
                            goto("summoningOps")
                        }
                        op("Ask something else.") {
                            player(HAPPY_TALKING, "I'd like to ask about something else.")
                            goto("initialOps")
                        }
                    }
                }
                op("Can I buy some summoning supplies?") {
                    player(HAPPY_TALKING, "Can I buy some summoning supplies, please?")
                    npc(npc, CALM_TALK, "Hur, hur, hur! Yooman's gotta buy lotsa stuff if yooman wants ta train good!")
                    exec { ShopsHandler.openShop(player, "Summoning_Supplies_(Bogrog)") }
                }
                if (player.skills.getLevel(Skills.SUMMONING) >= 21)
                    op("Are you interested in buying pouches or scrolls?") {
                        player(CALM_TALK, "Are you interested in buying pouches or scrolls?")
                        npc(npc, CALM_TALK, "Des other ogres's stealin' Bogrog's stock. Gimmie pouches and scrolls and yooman gets da shardies.")
                        player(CALM_TALK, "Ok.")
                        exec { openSwapInterface(player) }
                    }
                op("Nevermind.")
            }
        }
    }

    companion object {
        @JvmStatic
        fun handleBogrogActions(player: Player, item: Item, packet: ClientPacket) {
            when (packet) {
                ClientPacket.IF_OP1 -> getValue(player, item, 1)
                ClientPacket.IF_OP2 -> doSwap(player, 1, item.id)
                ClientPacket.IF_OP3 -> doSwap(player, 5, item.id)
                ClientPacket.IF_OP4 -> doSwap(player, 10, item.id)
                ClientPacket.IF_OP5 -> {
                    if (getValue(player, item, 1))
                        player.sendInputInteger("How many would you like to swap?") { num: Int ->
                            if (num > 0) {
                                val itemType = if (item.name.contains("scroll", true)) "scroll" else "pouch"
                                val pluralItemName = pluraliseItemName(itemType, num)
                                player.sendOptionDialogue("Swap ${Utils.formatNumber(num)} $pluralItemName?") { conf: Options ->
                                    conf.add("Yes, I'm sure.") {
                                        doSwap(player, num, item.id)
                                    }
                                    conf.add("No, I've changed my mind.")
                                }
                            } else {
                                player.simpleDialogue("You must specify an amount to swap with Bogrog.")
                            }
                        }
                }
                ClientPacket.IF_OP6 -> player.inventory.sendExamine(item.slot)
                else -> {}
            }
        }

        fun getValue(player: Player, item: Item, itemCount: Int): Boolean {
            val value = getValue(item.id)
            if (value > 0) {
                val shardWord = if (value.toInt() == 1) "shard" else "shards"
                val itemType = if (item.name.contains("scroll", true)) "scroll" else "pouch"
                val itemWord = if (itemCount == 1) "one $itemType" else "$itemCount ${pluraliseItemName(itemType, itemCount)}"
                player.sendMessage("You'll receive ${floor(value).toInt()} $shardWord in exchange for $itemWord.")
                return true
            } else {
                player.sendMessage("You cannot exchange that item.")
                return false
            }
        }

        fun getValue(itemID: Int): Double {
            val scroll = Scroll.entries.find { it.id == itemID }
            if (scroll != null) {
                val requiredPouch = scroll.fromPouches().firstOrNull()
                if (requiredPouch != null) {
                    val items = getItemsForPouchId(requiredPouch.id)
                    if (items.isEmpty()) return 0.0
                    val requiredShards = items.first().amount
                    val scrollShardValue = ceil((requiredShards * 0.7) / 15)
                    return scrollShardValue
                }
            }
            var items = getItemsForPouchId(itemID)
            if (items.isEmpty()) items = getItemsForPouchId(itemID - 1)
            if (items.isEmpty()) return 0.0
            return ceil(items.first().amount * 0.7)
        }

        fun doSwap(player: Player, amount: Int, itemID: Int): Boolean {
            var amt = amount
            val value = getValue(itemID)
            if (value == 0.0) {
                player.sendMessage("You cannot exchange that item.")
                return false
            }
            val inInventory = player.inventory.getAmountOf(itemID)
            if (amount > inInventory) {
                amt = inInventory
            }
            player.inventory.deleteItem(Item(itemID, amt))
            val shardAmt = floor(value * amt).toInt()
            player.inventory.addItemDrop(Item(12183, shardAmt))
            val shardWord = if (shardAmt == 1) "shard" else "shards"
            val itemType = if (Item(itemID).name.contains("scroll", true)) "scroll" else "pouch"
            player.sendMessage("You swapped $amt ${pluraliseItemName(itemType, amt)} and received $shardAmt $shardWord.")
            return true
        }

        fun openSwapInterface(player: Player) {
            player.interfaceManager.removeSubs(*ALL_GAME_TABS)
            openItemSelectWindow(player, 0, Mode.BOGROG)
            player.setCloseInterfacesEvent {
                player.interfaceManager.sendSubDefaults(*ALL_GAME_TABS)
                player.interfaceManager.openTab(Sub.TAB_INVENTORY)
            }
        }

        fun pluraliseItemName(itemType: String, count: Int): String {
            return when {
                count == 1 -> itemType
                itemType == "pouch" -> "pouches"
                itemType == "scroll" -> "scrolls"
                else -> itemType + "s"
            }
        }
    }
}

@ServerStartupEvent
fun mapBogrog() {
    onNpcClick(4472, options=arrayOf("Talk-To")) { (player, npc) -> Bogrog().talk(player, npc) }
    onNpcClick(4472, options=arrayOf("Swap")) { (player) ->
        if (player.skills.getLevel(Skills.SUMMONING) >= 21) {
            player.sendMessage("Pick the pouches and scrolls you wish to trade for shards.")
            openSwapInterface(player)
        } else {
            player.sendMessage("You need a Summoning level of at least 21 in order to do that.")
        }
    }
}
