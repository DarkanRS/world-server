package com.rs.game.content.quests.fishingcontest;

import static com.rs.game.content.quests.fishingcontest.FishingContest.FISHING_PASS;
import static com.rs.game.content.quests.fishingcontest.FishingContest.NOT_STARTED;
import static com.rs.game.content.world.doors.Doors.handleGate;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MorrisFishingContestD extends Conversation {
	private static final int NPC = 227;
	public MorrisFishingContestD(Player player) {
		super(player);
	}

	public static NPCClickHandler handleAustriDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new MorrisFishingContestD(e.getPlayer()).getStart()));

	public static ObjectClickHandler handleFishingContestGate = new ObjectClickHandler(true, new Object[] { 47, 48 }, e -> {
		GameObject obj = e.getObject();
		if(e.getPlayer().getQuestManager().getStage(Quest.FISHING_CONTEST) == NOT_STARTED) {
			e.getPlayer().sendMessage("You have no reason to enter...");
			return;
		}
		if(e.getPlayer().getQuestManager().getAttribs(Quest.FISHING_CONTEST).getB("MORRIS_SAW_TICKET"))
			handleGate(e.getPlayer(), obj);
		else
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(NPC, HeadE.CALM_TALK, "Competition pass please.");
					if(e.getPlayer().getInventory().containsItem(FISHING_PASS, 1)) {
						addItem(FISHING_PASS, "You show Morris your pass");
						addNPC(NPC, HeadE.CALM_TALK, "Move on through. Talk to Bonzo to enter the competition", ()->{
							e.getPlayer().getQuestManager().getAttribs(Quest.FISHING_CONTEST).setB("MORRIS_SAW_TICKET", true);
						});
						addNext(()->{
							handleGate(e.getPlayer(), obj);
						});
					} else {
						addPlayer(HeadE.SAD, "I don't have one...");
						addNPC(NPC, HeadE.CALM_TALK, "...");
					}
					create();
				}
			});
	});
}
