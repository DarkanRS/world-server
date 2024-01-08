package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.quest.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Gabooty {

	public static NPCClickHandler handleGabooty = new NPCClickHandler(new Object[] { 2520 }, new String[] { "Trade-Co-op", "Trade-Drinks" }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.JUNGLE_POTION))
			return;
		switch (e.getOption()) {
			case "Trade-Co-op" -> ShopsHandler.openShop(e.getPlayer(), "gabootys_tai_bwo_wannai_cooperative");
			case "Trade-Drinks" -> ShopsHandler.openShop(e.getPlayer(), "gabootys_tai_bwo_wannai_drinky_store");
		}
	});

}
