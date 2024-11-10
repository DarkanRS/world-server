package com.rs.game.content.world.areas.gu_tanoth

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.wolfwhistle.WolfWhistle
import com.rs.game.content.skills.agility.Agility
import com.rs.game.content.skills.summoning.Summoning
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapGuTanoth() {
    onObjectClick(2832) { e ->
        if (!Agility.hasLevel(e.player, 20)) return@onObjectClick
        Agility.handleObstacle(e.player, 3303, 2, e.player.transform(if (e.player.x < e.getObject().x) 2 else -2, 0, 0), 1.0)
    }

    onObjectClick(28722) { (player, obj, option) ->
        when (option) {
            "Infuse-pouch" -> {
                if (player.questManager.getStage(Quest.WOLF_WHISTLE) == WolfWhistle.WOLPERTINGER_CREATION) {
                    if (WolfWhistle.wolfWhistleObeliskReadyToInfusePouch(player)) {
                        WolfWhistle.doWolpertingerPouchCreation(player, obj)
                        return@onObjectClick
                    }
                }
                Summoning.openInfusionInterface(player, false);
            }
            "Renew-points" -> {
                val summonLevel = player.skills.getLevelForXp(Skills.SUMMONING);
                if (player.skills.getLevel(Skills.SUMMONING) < summonLevel) {
                    player.lock(3)
                    player.anim(8502)
                    player.skills.set(Skills.SUMMONING, summonLevel)
                    player.sendMessage("You have recharged your Summoning points.", true)
                    return@onObjectClick
                }
                player.sendMessage("You already have full Summoning points.")
            }
        }
    }
}
