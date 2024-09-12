package com.rs.game.content.world.areas.dig_site

import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.lib.Constants
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapDigsite() {
    onObjectClick(2353, tiles = arrayOf(Tile.of(3177, 5730, 0))) { e -> e.player.useStairs(828, Tile.of(3353, 3416, 0)) }
    onObjectClick(47120) { e ->

        if (e.player.prayer.points < e.player.skills.getLevelForXp(Constants.PRAYER) * 10) {
            e.player.lock(12)
            e.player.anim(12563)
            e.player.prayer.setPoints(((e.player.skills.getLevelForXp(Constants.PRAYER) * 10) * 1.15))
            e.player.prayer.refreshPoints()
        }
        if (e.player.questManager.isComplete(Quest.TEMPLE_AT_SENNTISTEN)) {
            e.player.startConversation {
                options {
                    op("Change from " + ((if (e.player.prayer.isCurses) "curses" else "prayers")) + "?") {
                        options {
                            op("Yes, replace my prayer book.") {
                                if (e.player.prayer.isCurses)
                                    simple("The altar eases its grip on your mid. The curses slip from your memory and you recall the prayers you used to know.")
                                else
                                    simple("The altar fills your head with dark thoughts, purging the prayers from your memory and leaving only curses in their place.")
                                e.player.prayer.setPrayerBook(!e.player.prayer.isCurses)
                            }
                            op("Nevermind.")
                        }
                    }
                }
            }
        }
    }
}
