package com.rs.game.content.quests.scorpioncatcher;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SeerScorpionCatcherD extends Conversation {
	private final static int NPC = 388;

	public SeerScorpionCatcherD(Player player) {
		super(player);
		if (!player.getInventory().containsOneItem(ScorpionCatcher.EMPTY_CAGE, ScorpionCatcher.CAUGHT_CAGE_1, ScorpionCatcher.CAUGHT_CAGE_2, ScorpionCatcher.CAUGHT_CAGE_3)) {
			addNPC(NPC, HeadE.CALM_TALK, "Thormac has your cage.");
			addPlayer(HeadE.FRUSTRATED, "Oh...");
		} else {
            addNPC(NPC, HeadE.CALM_TALK, "Many greetings");
            addPlayer(HeadE.HAPPY_TALKING, "Your friend Thormac sent me to speak to you.");
        }
		
		if (player.getInventory().containsItem(ScorpionCatcher.CAUGHT_CAGE_3, 1)) {
			addPlayer(HeadE.HAPPY_TALKING, "I got all the scorpions!");
			addNPC(NPC, HeadE.CALM_TALK, "Great!");
			return;
		}


		addNPC(NPC, HeadE.CALM_TALK, "What does the old fellow want?");
		addPlayer(HeadE.HAPPY_TALKING, "He's lost his valuable lesser Kharid scorpions.");
		addNPC(NPC, HeadE.CALM_TALK, "Well you have come to the right place. I am a master of animal detection. Let me look into my looking glass.");
		addSimple("The seer produces a small mirror");
		addSimple("The seer gazes into the mirror");
		addSimple("The seer smoothes his hair with his hand.");
		addNPC(NPC, HeadE.CALM_TALK, "I can see a scorpion that you seek. It resides in a dark place, between a lake and a holy island. There by the " +
				"entrance shall you find it. So close and yet so far.", () -> player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB("scorp1LocKnown", true));
		addPlayer(HeadE.CONFUSED, "That was cryptic. Can't you just tell me where it is?");
		addNPC(NPC, HeadE.CALM_TALK, "Where would be the fun in that?");
		addPlayer(HeadE.FRUSTRATED, "Any more scorpions?");
		addNPC(NPC, HeadE.CALM_TALK, "Well, I've checked my looking glass. The second Kharid Scorpion has been spirited away by a brute of a man." +
				" He runs a shop in a village two canoe trips from Lumbridge.", () -> player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB("scorp2LocKnown", true));
		addNPC(NPC, HeadE.CALM_TALK, "That's all I can tell you about that scorpion.");
		addPlayer(HeadE.FRUSTRATED, "Any more scorpions?");
		addNPC(NPC, HeadE.CALM_TALK, "It's good that you should ask. I have information on the last scorpion for you. It seems to be in some sort of " +
				"upstairs room. There's some brown clothing lying on a table nearby.");
		addPlayer(HeadE.VERY_FRUSTRATED, "Oh come on now! Brown clothing? That's not a clue!");
		addNPC(NPC, HeadE.CALM_TALK, "Alright alright! The clothing is adorned with a golden four-pointed star. Happy now?");
		addPlayer(HeadE.HAPPY_TALKING, "Not really.");
		addNPC(NPC, HeadE.FRUSTRATED, "Fine! You should go to where monks reside.");
		addPlayer(HeadE.HAPPY_TALKING, "Up a tree?");
		addNPC(NPC, HeadE.ANGRY, "MONKS! NOT MONKEYS!!!!", () -> player.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB("scorp3LocKnown", true));
	}
}
