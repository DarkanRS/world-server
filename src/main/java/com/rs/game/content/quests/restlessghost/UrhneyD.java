package com.rs.game.content.quests.restlessghost;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.buyersandcellars.npcs.FatherUrhney;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class UrhneyD extends Conversation {
	private static int NPC = 458;

	public static NPCClickHandler Urhney = new NPCClickHandler(new Object[] { NPC }, new String[] {"Talk-to"}, e -> {
		e.getPlayer().startConversation(new UrhneyD(e.getPlayer(), e.getNPC()));
	});

	public UrhneyD(Player player, NPC npc) {
		super(player);

		if (player.getQuestManager().getStage(Quest.BUYERS_AND_CELLARS) == 4 || player.getQuestManager().getStage(Quest.BUYERS_AND_CELLARS) == 5) {
			FatherUrhney.stage4(player);
			return;
		}
		if (player.getQuestManager().getStage(Quest.BUYERS_AND_CELLARS) >= 6) {
			FatherUrhney.stage6(player, npc);
			return;
		}
		if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 0) {
			addNPC(NPC, HeadE.FRUSTRATED, "Get out of my house!");
			return;
		}
		if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 1) {
			addNPC(NPC, HeadE.FRUSTRATED, "Get out of my house!");
			if (!player.getInventory().containsItem(552, 1)) {//needs amulet
				addPlayer(HeadE.HAPPY_TALKING, "Father Aereck told me to come talk to you about a ghost haunting his graveyard.");
				addNPC(NPC, HeadE.FRUSTRATED, "Oh the silly old fool. Here, take this amulet and see if you can communicate with the spectre", () ->{
					player.getInventory().addItem(552, 1);
					player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 2);
				});
				addPlayer(HeadE.HAPPY_TALKING, "Thank you. I'll try.");
				return;
			}
			addNext(()->{
				player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 2);
			});
			return;
		}
		addNPC(NPC, HeadE.CALM_TALK, "What do you need now?");
		if (!player.getInventory().containsItem(552, 1)) { //needs amulet
			addPlayer(HeadE.HAPPY_TALKING, "I've lost my amulet of ghostspeak.");
			addNPC(NPC, HeadE.CALM_TALK, "Have another one then. But be more careful next time!", ()->{
				player.getInventory().addItem(552, 1);
			});
			addPlayer(HeadE.HAPPY_TALKING, "Thank you. I'll try.");
		}
		create();
	}

}