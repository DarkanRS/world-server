package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.heroesquest.dialogues.KatrineHeroesQuestD;
import com.rs.game.content.quests.shieldofarrav.KatrineShieldOfArravD;
import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class Katrine {
    public static NPCClickHandler handleKatrine = new NPCClickHandler(new Object[] { 642 }, e -> {
        Player p = e.getPlayer();
        if(p.isQuestComplete(Quest.SHIELD_OF_ARRAV )
                && ShieldOfArrav.isBlackArmGang(p) && p.getQuestManager().getStage(Quest.HEROES_QUEST) > 0)
            p.startConversation(new KatrineHeroesQuestD(p).getStart());
        else
            p.startConversation(new KatrineShieldOfArravD(p).getStart());
    });
}
