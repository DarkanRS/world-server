package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Obli {

	public static NPCClickHandler handleObli = new NPCClickHandler(new Object[] { 516 }, new String[] { "Talk-to" }, e -> {
		Player player = e.getPlayer();
		NPC npc = e.getNPC();
		if (!player.isQuestComplete(Quest.SHILO_VILLAGE))
			return;

		player.startConversation(new Dialogue()
				.addNPC(npc, HeadE.CALM_TALK, "Welcome to Obli's General Store Bwana! Would you like to see my items?")
				.addOptions((ops) -> {
					ops.add("Yes please!")
							.addPlayer(HeadE.CALM_TALK, "Yes please!")
							.addNext(() -> ShopsHandler.openShop(player, "oblis_general_store"));
					ops.add("No, but thanks for the offer.")
							.addPlayer(HeadE.CALM_TALK, "No, but thanks for the offer.");
				}));
	});

}
