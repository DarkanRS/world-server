package com.rs.game.content.miniquests.bar_crawl

import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.BLUE_MOON_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.BLURBERRYS_BAR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.DEAD_MANS_CHEST
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.DRAGON_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.FLYING_HORSE_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.FORESTERS_ARMS
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.JOLLY_BOAR_INN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.KARAMJA_SPIRITS
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.RISING_SUN
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.Bars.RUSTY_ANCHOR
import com.rs.game.content.miniquests.bar_crawl.BarCrawl.BarCrawlBars.setBarVisited
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.lib.Constants
import com.rs.lib.util.Utils
import com.rs.utils.Ticks

class BlueMoonInnEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.forceTalk("Blearrgh!")
        player.applyHit(Hit(null, Utils.random(200), Hit.HitLook.TRUE_DAMAGE))
        player.skills.lowerStat(Constants.ATTACK, 8)
        player.skills.lowerStat(Constants.STRENGTH, 7)
        player.skills.lowerStat(Constants.DEFENSE, 7)
        player.skills.lowerStat(Constants.SMITHING, 6)
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("Your insides feel terrible.", "The bartender signs your card.")
            setBarVisited(player, BLUE_MOON_INN, true)
            player.unlock()
        } else {
            player.sendMessage("You buy some ${BLUE_MOON_INN.drinkName}.", "You drink the ${BLUE_MOON_INN.drinkName}.")
            player.inventory.removeCoins(BLUE_MOON_INN.price)
        }
    }
}

class BlurberrysBarEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.applyHit(Hit(null, Utils.random(47), Hit.HitLook.TRUE_DAMAGE))
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("Blurberry signs your card.")
            setBarVisited(player, BLURBERRYS_BAR, true)
            player.unlock()
        } else {
            player.sendMessage("Your mouth and throat burns as you gulp it down.")
            player.inventory.removeCoins(BLURBERRYS_BAR.price)
        }
    }
}

class DeadMansChestEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.skills.lowerStat(Constants.ATTACK, 8)
        player.skills.lowerStat(Constants.DEFENSE, 7)
        player.skills.lowerStat(Constants.HERBLORE, 6)
        player.skills.lowerStat(Constants.COOKING, 7)
        player.prayer.drainPrayer(60.0)
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("You stagger backwards.", "You think you see 2 bartenders signing 2 barcrawl cards.")
            setBarVisited(player, DEAD_MANS_CHEST, true)
            player.unlock()
        } else {
            player.sendMessage("The bartender serves you a glass of strange thick dark liquid.", "You wince and drink it.")
            player.inventory.removeCoins(DEAD_MANS_CHEST.price)
        }
    }
}

class DragonInnEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.skills.lowerStat(Constants.ATTACK, 11)
        player.skills.lowerStat(Constants.DEFENSE, 10)
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("Your vision blurs and you stagger slightly.", "You can just about make out the bartender signing your barcrawl card.")
            setBarVisited(player, DRAGON_INN, true)
            player.unlock()
        } else {
            player.sendMessage("The bartender hands you a small glass and sets light to the contents.", "You blow out the flame and drink it.")
            player.inventory.removeCoins(DRAGON_INN.price)
        }
    }
}

class FlyingHorseInnEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        val damagePercentage = Utils.random(20, 30)
        val damage = (player.skills.getLevel(Constants.HITPOINTS) * damagePercentage / 100)
        player.applyHit(Hit(null, damage, Hit.HitLook.TRUE_DAMAGE))
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("You clutch your chest.", "Through your tears you see the bartender...", "signing your barcrawl card.")
            setBarVisited(player, FLYING_HORSE_INN, true)
            player.unlock()
        } else {
            player.sendMessage("The bartender hands you a shot of ${FLYING_HORSE_INN.drinkName}.", "You grimace and drink it.")
            player.inventory.removeCoins(FLYING_HORSE_INN.price)
        }
    }
}

class ForestersArmsEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.skills.lowerStat(Constants.ATTACK, 8)
        player.skills.lowerStat(Constants.DEFENSE, 6)
        player.skills.lowerStat(Constants.FLETCHING, 5)
        player.skills.lowerStat(Constants.FIREMAKING, 5)
        player.skills.lowerStat(Constants.WOODCUTTING, 5)
        player.schedule {
            player.packets.sendCameraShake(0, 0, 60, 2, 50)
            wait(Ticks.fromSeconds(Utils.random(4, 8)))
            player.packets.sendStopCameraShake()
            player.unlock()
        }
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("The room seems to be swaying.", "The bartender scrawls his signature on your card.")
            setBarVisited(player, FORESTERS_ARMS, true)
        } else {
            player.sendMessage("The bartender gives you a glass of ${FORESTERS_ARMS.drinkName}.", "You gulp it down.")
            player.inventory.removeCoins(FORESTERS_ARMS.price)
        }
    }
}

class JollyBoarInnEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.skills.lowerStat(Constants.ATTACK, 8)
        player.skills.lowerStat(Constants.DEFENSE, 7)
        player.skills.lowerStat(Constants.STRENGTH, 6)
        player.skills.lowerStat(Constants.MINING, 6)
        player.skills.lowerStat(Constants.CRAFTING, 6)
        player.skills.lowerStat(Constants.MAGIC, 6)
        player.applyHit(Hit(null, 100, Hit.HitLook.TRUE_DAMAGE))
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("Your head is spinning.", "The bartender signs your card.")
            setBarVisited(player, JOLLY_BOAR_INN, true)
            player.forceTalk("Thanksh very mush...")
            player.unlock()
        } else {
            player.sendMessage("You buy a pint of ${JOLLY_BOAR_INN.drinkName}.", "You gulp it down.")
            player.inventory.removeCoins(JOLLY_BOAR_INN.price)
        }
    }
}

class KaramjaSpiritsEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.skills.lowerStat(Constants.ATTACK, 7)
        player.skills.lowerStat(Constants.DEFENSE, 6)
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("Zambo signs your card.")
            setBarVisited(player, KARAMJA_SPIRITS, true)
            player.forceTalk("Mmmmm, dat was luverly...")
            player.unlock()
        } else {
            player.sendMessage("You buy some ${KARAMJA_SPIRITS.drinkName}.", "You swirl it around and swallow it.")
            player.inventory.removeCoins(KARAMJA_SPIRITS.price)
        }
    }
}

class RisingSunEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
        player.skills.lowerStat(Constants.ATTACK, 8)
        player.skills.lowerStat(Constants.DEFENSE, 6)
        player.skills.lowerStat(Constants.RANGE, 6)
        player.skills.lowerStat(Constants.FISHING, 6)
        player.applyHit(Hit(null, Utils.random(200), Hit.HitLook.TRUE_DAMAGE))
        player.schedule {
            player.packets.sendCameraShake(0, 0, 60, 2, 50)
            wait(Ticks.fromSeconds(Utils.random(4, 8)))
            player.packets.sendStopCameraShake()
            player.unlock()
        }
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("The barmaid giggles.", "The barmaid signs your card.")
            setBarVisited(player, RISING_SUN, true)
        } else {
            player.sendMessage("You buy a ${RISING_SUN.drinkName}.", "You drink the cocktail.", "You stumble around the room.")
            player.inventory.removeCoins(RISING_SUN.price)
        }
    }
}

class RustyAnchorEffect : BarCrawl.Effects {
    override fun effect(player: Player) {
        player.anim(829)
    }

    override fun message(player: Player, start: Boolean) {
        if (!start) {
            player.sendMessage("The bartender signs your card.")
            setBarVisited(player, RUSTY_ANCHOR, true)
            player.forceTalk("Hiccup!")
            player.unlock()
        } else {
            player.sendMessage("You buy a ${RUSTY_ANCHOR.drinkName}...", "Your vision blurs.")
            player.inventory.removeCoins(RUSTY_ANCHOR.price)
        }
    }
}
