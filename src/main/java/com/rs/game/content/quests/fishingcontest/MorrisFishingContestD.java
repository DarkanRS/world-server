package com.rs.game.content.quests.fishingcontest;

import static com.rs.game.content.quests.fishingcontest.FishingContest.FISHING_PASS;
import static com.rs.game.content.quests.fishingcontest.FishingContest.NOT_STARTED;
import static com.rs.game.content.world.doors.Doors.handleGate;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MorrisFishingContestD extends Conversation {
	private static final int NPC = 227;
	private static final String MORRIS_SAW_TICKET = "MORRIS_SAW_TICKET";
	public MorrisFishingContestD(Player p) {
		super(p);
	}

	public static NPCClickHandler handleAustriDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new MorrisFishingContestD(e.getPlayer()).getStart()));

	public static ObjectClickHandler handleFishingContestGate = new ObjectClickHandler(true, new Object[] { 47, 48 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(p.getQuestManager().getStage(Quest.FISHING_CONTEST) == NOT_STARTED) {
			p.sendMessage("You have no reason to enter...");
			return;
		}
		if(p.getQuestManager().getAttribs(Quest.FISHING_CONTEST).getB(MORRIS_SAW_TICKET))
			handleGate(p, obj);
		else
			p.startConversation(new Conversation(p) {
				{
					addNPC(NPC, HeadE.CALM_TALK, "Competition pass please.");
					if(p.getInventory().containsItem(FISHING_PASS, 1)) {
						addItem(FISHING_PASS, "You show Morris your pass");
						addNPC(NPC, HeadE.CALM_TALK, "Move on through. Talk to Bonzo to enter the competition", ()->{
							p.getQuestManager().getAttribs(Quest.FISHING_CONTEST).setB(MORRIS_SAW_TICKET, true);
						});
						addNext(()->{
							handleGate(p, obj);
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
