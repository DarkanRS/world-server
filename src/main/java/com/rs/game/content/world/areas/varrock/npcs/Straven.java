package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.heroesquest.dialogues.StravenHeroesQuestD;
import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.game.content.quests.shieldofarrav.StravenShieldOfArravD;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Straven {
    public static NPCClickHandler handleStraven = new NPCClickHandler(new Object[] { 644 }, e -> {
        Player p = e.getPlayer();
        if(p.isQuestComplete(Quest.SHIELD_OF_ARRAV )
                && ShieldOfArrav.isPhoenixGang(p) && p.getQuestManager().getStage(Quest.HEROES_QUEST) > 0) //started
            p.startConversation(new StravenHeroesQuestD(p).getStart());
        else
            p.startConversation(new StravenShieldOfArravD(p).getStart());
    });
}
