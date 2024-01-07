package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class PirateJackieTheFruit {

	public static NPCClickHandler handlePirateJackieFruit = new NPCClickHandler(new Object[] { 1055 }, e -> {
		Player player = e.getPlayer();
		NPC npc = e.getNPC();
		player.startConversation(new Dialogue()
				.addNPC(npc, HeadE.CHEERFUL, "Hello, what are you after?")
				.addOptions("What would you like to say?", (ops) -> {
					ops.option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
				}));
	});

}
