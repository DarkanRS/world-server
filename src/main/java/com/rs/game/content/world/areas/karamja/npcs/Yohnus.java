package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Yohnus {

	public static NPCClickHandler handleYohnus = new NPCClickHandler(new Object[] { 513 }, new String[]{ "Talk-to" }, e -> {
		Player player = e.getPlayer();
		NPC npc = e.getNPC();
		if (!player.isQuestComplete(Quest.SHILO_VILLAGE))
			return;

		int blacksmithPays = player.getI("shilo_blacksmith_pay");
		player.startConversation(new Dialogue()
				.addPlayer(HeadE.CALM_TALK, "Hello.")
				.addNPC(npc, HeadE.CALM_TALK, "Sorry but the blacksmiths is closed. But I can let you use the furnace at the cost of 20 gold pieces.")
				.addOptions((ops) -> {
					ops.add("Use Furnace - 20 Gold")
							.addNext(() -> {
								if (!player.getInventory().hasCoins(20)) {
									player.startConversation(new Dialogue()
											.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "Sorry Bwana, you do not have enough gold!"));
									return;
								}
								player.getInventory().removeCoins(20);
								player.save("shilo_blacksmith_pay", blacksmithPays + 1);
							})
							.addNPC(npc, HeadE.HAPPY_TALKING, "Thanks Bwana! Enjoy the facilities!");
					ops.add("No thanks!")
							.addPlayer(HeadE.CALM_TALK, "No thanks!")
							.addNPC(npc, HeadE.CALM_TALK, "Very well Bwana, have a nice day.");
				}));
	});

}
