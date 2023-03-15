package com.rs.game.content.quests.treegnomevillage.dialogues;

import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.FIRE_BALLISTA;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.GET_WOOD;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.KILL_WARLORD;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.NOT_STARTED;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.ORB1;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.QUEST_COMPLETE;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.TALK_TO_MONTAI_ABOUT_TRACKERS;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.TALK_TO_MONTAI_ABOUT_WOOD;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

@PluginEventHandler
public class Tracker2TreeGnomeVillageD extends Conversation {
	private static final int NPC = 482;
	public Tracker2TreeGnomeVillageD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE)) {
			case NOT_STARTED, TALK_TO_MONTAI_ABOUT_WOOD, GET_WOOD, TALK_TO_MONTAI_ABOUT_TRACKERS -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello");
				addNPC(NPC, HeadE.CALM_TALK, "I can't talk now. If the guards catch me i'll be dead gnome meat.");
			}
			case FIRE_BALLISTA -> {
				addPlayer(HeadE.HAPPY_TALKING, "Are you OK?");
				addNPC(NPC, HeadE.CALM_TALK, "They caught me spying on the stronghold. They beat and tortured me.");
				addNPC(NPC, HeadE.CALM_TALK, "But I didn't crack. I told them nothing. They can't break me!");
				addPlayer(HeadE.HAPPY_TALKING, "I'm sorry little man.");
				addNPC(NPC, HeadE.CALM_TALK, "Don't be. I have the position of the stronghold!");
				addPlayer(HeadE.HAPPY_TALKING, "Well done.", ()->{
					player.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).setB("tracker2found", true);
				});
				addNPC(NPC, HeadE.CALM_TALK, "Now leave before they find you and all is lost.");
				addPlayer(HeadE.HAPPY_TALKING, "Hang in there.");
				addNPC(NPC, HeadE.CALM_TALK, "Go!");
			}
			case ORB1, KILL_WARLORD -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Now we have the orb I'm much better. They won't stand a chance without it.");
			}
			case QUEST_COMPLETE ->  {
				addPlayer(HeadE.HAPPY_TALKING, "How are you tracker?");
				addNPC(NPC, HeadE.CALM_TALK, "Now we have the orb I'm much better. They won't stand a chance without it.");

			}
		}
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new Tracker2TreeGnomeVillageD(e.getPlayer()).getStart()));

	public static NPCInteractionDistanceHandler tracker2Distance = new NPCInteractionDistanceHandler(NPC, (player, npc) -> 1);
}
