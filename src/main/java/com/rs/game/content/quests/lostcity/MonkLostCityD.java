package com.rs.game.content.quests.lostcity;

import static com.rs.game.content.quests.lostcity.LostCity.CHOP_DRAMEN_TREE;
import static com.rs.game.content.quests.lostcity.LostCity.FIND_ZANARIS;
import static com.rs.game.content.quests.lostcity.LostCity.MONK;
import static com.rs.game.content.quests.lostcity.LostCity.NOT_STARTED;
import static com.rs.game.content.quests.lostcity.LostCity.QUEST_COMPLETE;
import static com.rs.game.content.quests.lostcity.LostCity.TALK_TO_LEPRAUCAN;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MonkLostCityD extends Conversation {
	public MonkLostCityD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.LOST_CITY)) {
		case NOT_STARTED -> {
			addPlayer(HeadE.HAPPY_TALKING, "Why are all of you standing around here?");
			addNPC(MONK, HeadE.FRUSTRATED, "None of your business. Get lost.");
		}
		case TALK_TO_LEPRAUCAN -> {
			addPlayer(HeadE.HAPPY_TALKING, "Have you found the tree with the leprechaun yet?");
			addNPC(MONK, HeadE.SKEPTICAL_THINKING, "No, we've looked for ages but haven't... Hey! Wait a minute! How did you know about that?");
			addPlayer(HeadE.HAPPY_TALKING, "Thanks for the information!");
			addNPC(MONK, HeadE.FRUSTRATED, "...You tricked me. I'm not talking to you anymore.");
		}
		case CHOP_DRAMEN_TREE, FIND_ZANARIS, QUEST_COMPLETE ->  {
			addNPC(MONK, HeadE.FRUSTRATED, "I already told you. I'm not talking to you anymore.");
		}
		}
	}

	public static NPCClickHandler handleMonkDialogue = new NPCClickHandler(new Object[] { MONK }, e -> e.getPlayer().startConversation(new MonkLostCityD(e.getPlayer()).getStart()));
}
