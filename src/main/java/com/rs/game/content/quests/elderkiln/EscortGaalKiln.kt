package com.rs.game.content.quests.elderkiln

import com.rs.engine.quest.Quest
import com.rs.game.content.minigames.fightkiln.FightKilnController
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapHandlers() {
    onObjectClick(68107) { (player, _, option) ->
        when(option) {
            "Enter" ->
                if (player.isQuestComplete(Quest.ELDER_KILN))
                    FightKilnController.enterFightKiln(player, false)
                else
                    enterQuestKiln(player)
            "Quick-Play Fight Kiln" ->
                if (player.isQuestComplete(Quest.ELDER_KILN))
                    FightKilnController.enterFightKiln(player, true)
        }
    }
}

private fun enterQuestKiln(player: Player) {

}