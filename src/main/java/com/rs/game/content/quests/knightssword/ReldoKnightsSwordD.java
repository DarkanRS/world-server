package com.rs.game.content.quests.knightssword;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.knightssword.KnightsSword.*;

@PluginEventHandler
public class ReldoKnightsSwordD extends Conversation {
	public ReldoKnightsSwordD(Player player) {
		super(player);
		addPlayer(HeadE.HAPPY_TALKING, "What do you know about the Imcando dwarves?");
		addNPC(RELDO, HeadE.CALM_TALK, "The Imcando dwarves, you say?");
		addNPC(RELDO, HeadE.CALM_TALK, "Ah yes... for many hundreds of years they were the world's most skilled smiths. They used secret smithing knowledge " +
				"passed down from generation to generation.");
		addNPC(RELDO, HeadE.CALM_TALK, "Unfortunately, about a century ago, the once thriving race was wiped out during the barbarian invasions of that time.");
		addPlayer(HeadE.HAPPY_TALKING, "So are there any Imcando left at all?");
		addNPC(RELDO, HeadE.CALM_TALK, "I believe a few of them survived, but with the bulk of their population destroyed their numbers have dwindled even further.");
		addNPC(RELDO, HeadE.CALM_TALK, "They tend to keep to themselves, and they tend not to tell people they're descendants of the Imcando, which is why " +
				"people think the tribe is extinct. However...");
		addNPC(RELDO, HeadE.CALM_TALK, "... you could try taking them some redberry pie. They REALLY like redberry pie. I believe I remember a couple living in " +
				"Asgarnia near the cliffs on the Asgarnian southern peninsula.", ()->{
					if(player.getQuestManager().getStage(Quest.KNIGHTS_SWORD) == TALK_TO_RELDO)
						player.getQuestManager().setStage(Quest.KNIGHTS_SWORD, FIND_DWARF, true);
				});
	}
}
