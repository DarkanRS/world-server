package com.rs.game.content.miniquests.bar_crawl

import com.rs.engine.miniquest.Miniquest
import com.rs.engine.miniquest.MiniquestHandler
import com.rs.engine.miniquest.MiniquestOutline
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.allBarsVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.completedBarCrawl
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.isBarVisited
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.BARBARIAN_GUARD_ID
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.BARCRAWL_CARD_ID
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.Companion.displayBarcrawlInterface
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapBarCrawl() {
    onNpcClick(BARBARIAN_GUARD_ID) { (player, _, _) ->
        BarbarianGuardD(player)
    }
    onItemClick(BARCRAWL_CARD_ID, options = arrayOf("Read")) { e ->
        if (allBarsVisited(e.player)) {
            e.player.sendMessage("You are too drunk to be able to read the barcrawl card.")
        } else {
            displayBarcrawlInterface(e.player)
        }
    }
    onObjectClick(2115, 2116) { (player, obj) ->
        if (!completedBarCrawl(player)) {
            player.sendMessage("You should speak to one of the Barbarian guards first, before trying to go through these gates.")
            return@onObjectClick
        }
        Doors.handleDoubleDoor(player, obj)
    }
    onLogin { (player) ->
        if (player.inArea(2546, 3560, 2555, 3573) ||
            player.inArea(2529, 3542, 2552, 3556) ||
            player.inArea(2546, 3542, 2553, 3559) ||
            player.inArea(2545, 9947, 2556, 9956)) {
            if (!completedBarCrawl(player)) {
                player.fadeScreen {
                    player.tele(2543, 3569, 0)
                    player.sendMessage("You are escorted out of the Barbarian Outpost, because you have not yet completed the Alfred Grimhand's Barcrawl miniquest.")

                }
            }
        }
    }
}

@MiniquestHandler(
    miniquest = Miniquest.BAR_CRAWL,
    startText = "The Barbarian Guards outside the Barbarian Outpost in Kandarin.",
    itemsText = "208 gold coins.",
    combatText = "None.",
    rewardsText = "Access to the Barbarian Outpost.<br>Access to the Barbarian Agility Course.",
    completedStage = BarCrawl.COMPLETED
)

class BarCrawl : MiniquestOutline() {

    companion object {
        const val BARCRAWL_CARD_ID = 455
        const val BARBARIAN_GUARD_ID = 384

        const val NOT_STARTED = 0
        const val RECEIVED_CRAWL_CARD = 1
        const val COMPLETED = 2

        @JvmStatic
        fun hasCard(player: Player): Boolean {
            return player.inventory.containsItem(BARCRAWL_CARD_ID)
        }

        @JvmStatic
        fun displayBarcrawlInterface(player: Player) {
            player.packets.setIFText(220, 1, "<col=311b31>The Official Alfred Grimhand Barcrawl!</col>")
            for (i in 0 until BarCrawlBars.Bars.entries.size) {
                val bar = BarCrawlBars.Bars.entries[i]
                val complete = isBarVisited(player, bar)
                val text = ((if (complete) "<str><col=200A01>${bar.barName}</col></str>" else "<col=5a2a0c>${bar.barName}</col>"))
                player.packets.setIFText(220, 3 + i, text)
            }
            player.interfaceManager.sendInterface(220)
        }
    }

    override fun getJournalLines(player: Player, stage: Int): List<String> {
        val lines = ArrayList<String>()
        when (stage) {
            NOT_STARTED -> {
                lines.add("I can start this miniquest by speaking to the Barbarian guards")
                lines.add("outside the Barbarian Outpost.")
            }

            RECEIVED_CRAWL_CARD -> {
                lines.add("The Barbarian Guard said I couldn't enter the Outpost unless")
                lines.add("I completed their challenge - a barcrawl!")
                lines.add("")
                lines.add("I must visit each of the bars on my barcrawl card and")
                lines.add("try their strongest drinks.")
                lines.add("")
                lines.add("Once done, I should let the Barbarian Guard know.")
            }

            COMPLETED -> {
                lines.add("")
                lines.add("")
                lines.add("MINIQUEST COMPLETE!")
            }

            else -> lines.add("Invalid miniquest stage. Report this to an administrator.")
        }
        return lines
    }

    override fun complete(player: Player) {
        sendQuestCompleteInterface(player, BARCRAWL_CARD_ID)
    }

    override fun updateStage(player: Player) {
    }

    interface Effects {
        fun effect(player: Player)
        fun message(player: Player, start: Boolean)
    }

    object BarCrawlBars {

        enum class Bars(
            val barName: String,
            val price: Int,
            val effect: Effects,
            val drinkName: String
        ) {
            BLUE_MOON_INN("Blue Moon Inn (Varrock)", 50, BlueMoonInnEffect(), "Uncle Humphrey's Gutrot"),
            BLURBERRYS_BAR("Blurberry's Bar (Tree Gnome Stronghold)", 10, BlurberrysBarEffect(), "Fire Toad Blast"),
            DEAD_MANS_CHEST("Dead Man's Chest (Brimhaven)", 15, DeadMansChestEffect(), "Supergrog"),
            DRAGON_INN("Dragon Inn (Yanille)", 12, DragonInnEffect(), "Fire Brandy"),
            FLYING_HORSE_INN("Flying Horse Inn (Ardougne)", 8, FlyingHorseInnEffect(), "Heart Stopper"),
            FORESTERS_ARMS("Forester's Arms (Seers' Village)", 18, ForestersArmsEffect(), "Liverbane Ale"),
            JOLLY_BOAR_INN("Jolly Boar Inn (north-east of Varrock)", 10, JollyBoarInnEffect(), "Olde Suspiciouse"),
            KARAMJA_SPIRITS("Karamja Spirits Bar (Musa Point)", 7, KaramjaSpiritsEffect(), "Ape Bite Liqueur"),
            RISING_SUN("Rising Sun (Falador)", 70, RisingSunEffect(), "Hand of Death Cocktail"),
            RUSTY_ANCHOR("Rusty Anchor (Port Sarim)", 8, RustyAnchorEffect(), "Black Skull Ale")
        }

        private val visitedBars = mutableMapOf<String, Boolean>()

        @JvmStatic
        fun resetAllBars(player: Player) {
            player.miniquestManager.getAttribs(Miniquest.BAR_CRAWL).clear()
        }

        @JvmStatic
        fun isBarVisited(player: Player, bars: Bars): Boolean {
            return player.miniquestManager.getAttribs(Miniquest.BAR_CRAWL).getB(bars.barName)
        }

        @JvmStatic
        fun setBarVisited(player: Player, bars: Bars, visited: Boolean) {
            visitedBars[bars.barName] = visited
            player.miniquestManager.getAttribs(Miniquest.BAR_CRAWL).setB(bars.barName, visited)
        }

        @JvmStatic
        fun allBarsVisited(player: Player): Boolean {
            for (bars in Bars.entries) {
                if (!isBarVisited(player, bars)) {
                    return false
                }
            }
            return true
        }

        fun onBarCrawl(player: Player): Boolean {
            return player.miniquestManager.getStage(Miniquest.BAR_CRAWL) == RECEIVED_CRAWL_CARD
        }

        fun completedBarCrawl(player: Player): Boolean {
            return player.miniquestManager.getStage(Miniquest.BAR_CRAWL) == COMPLETED
        }
    }
}
