package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class KalebParamaya {

	public static NPCClickHandler handleKalebParamaya = new NPCClickHandler(new Object[] { 512 }, e -> {
		Player player = e.getPlayer();
		int npcId = e.getNPC().getId();
		player.startConversation(new Dialogue()
				.addNPC(npcId, HeadE.CHEERFUL, "Hello, what are you after?")
				.addOptions("What would you like to say?", (ops) -> ops.option("About the Achievement System...", new AchievementSystemDialogue(player, npcId, SetReward.KARAMJA_GLOVES).getStart())));
	});

}
