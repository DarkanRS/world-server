package com.rs.game.content.world.areas.gamers_grotto.npcs;

import com.rs.game.content.minigames.creations.StealingCreationShop;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class RewardsMystic {
    public static NPCClickHandler RewardsMysticExchange = new NPCClickHandler(new Object[] { 8228 }, new String[] {"Exchange"}, e -> StealingCreationShop.openInterface(e.getPlayer()));
}
