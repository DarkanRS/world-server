package com.rs.game.content.quests.lostcity;

import static com.rs.game.content.quests.lostcity.LostCity.CHOP_DRAMEN_TREE;
import static com.rs.game.content.quests.lostcity.LostCity.FIND_ZANARIS;
import static com.rs.game.content.quests.lostcity.LostCity.NOT_STARTED;
import static com.rs.game.content.quests.lostcity.LostCity.QUEST_COMPLETE;
import static com.rs.game.content.quests.lostcity.LostCity.TALK_TO_LEPRAUCAN;
import static com.rs.game.content.quests.lostcity.LostCity.WIZARD;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class WizardLostCityD extends Conversation {
	public WizardLostCityD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.LOST_CITY)) {
		case NOT_STARTED -> {
			addPlayer(HeadE.HAPPY_TALKING, "Why are all of you standing around here?");
			addNPC(WIZARD, HeadE.LAUGH, "Hahaha, you dare talk to a mighty wizard such as myself? I bet you can't even cast Air Strike yet, amateur!");
			addPlayer(HeadE.FRUSTRATED, "...You're an idiot.");
		}
		case TALK_TO_LEPRAUCAN -> {
			addPlayer(HeadE.HAPPY_TALKING, "Found that leprechaun yet?");
			addNPC(WIZARD, HeadE.LAUGH, "Hahaha! Go away, amateur! You're not worthy of joining our great group!");
			addPlayer(HeadE.FRUSTRATED, "...Right.");
		}
		case CHOP_DRAMEN_TREE, FIND_ZANARIS, QUEST_COMPLETE ->  {
			addNPC(WIZARD, HeadE.LAUGH, "Hahaha, you're such an amateur! Go and play with some cabbage, amateur!");
			addPlayer(HeadE.SECRETIVE, "...Right.");

		}
		}
	}

	public static NPCClickHandler handleWizardDialogue = new NPCClickHandler(new Object[] { WIZARD }, e -> e.getPlayer().startConversation(new WizardLostCityD(e.getPlayer()).getStart()));
}
