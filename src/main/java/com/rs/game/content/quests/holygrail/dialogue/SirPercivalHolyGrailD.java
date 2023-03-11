package com.rs.game.content.quests.holygrail.dialogue;

import static com.rs.game.content.quests.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SirPercivalHolyGrailD extends Conversation {
	private static final int NPC = 211;
	public SirPercivalHolyGrailD(Player player) {
		super(player);
		if(player.getQuestManager().getStage(Quest.HOLY_GRAIL) == SPEAK_TO_PERCIVAL) {
			Dialogue options = new Dialogue();
			if(player.getInventory().containsItem(16))
				options.addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("How did you end up in a sack?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How did you end up in a sack?")
								.addNPC(NPC, HeadE.CALM_TALK, "It's a little embarrassing really. After going on a long and challenging quest to retrieve the boots" +
										" of Arkaneeses, defeating many powerful enemies on the way, I fell into a goblin trap!")
								.addNPC(NPC, HeadE.CALM_TALK, "I've been kept as a slave here for the last 3 months! A day or so ago, " +
										"they decided it was a fun game to put me in this sack; then they forgot about me! I'm now very hungry, and my bones feel very stiff.")
								.addNext(options)
						);
						option("Come with me, I shall make you a king.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Come with me, I shall make you a king.")
								.addNPC(NPC, HeadE.CALM_TALK, "What are you talking about? The king of where?")
								.addPlayer(HeadE.HAPPY_TALKING, "Your father is apparently someone called the Fisher King. He is dying and wishes you to be his heir.")
								.addNPC(NPC, HeadE.CALM_TALK, "I have been told that before. I have not been able to find the castle again though.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I do have the means to get us there - a magic whistle!")
								.addItem(16, "You show him the magic whistle and explain where to blow it...", () -> {
									player.getInventory().deleteItem(new Item(16, 1));
									player.getQuestManager().setStage(Quest.HOLY_GRAIL, GIVE_AURTHUR_HOLY_GRAIL);
								})
								.addNPC(NPC, HeadE.CALM_TALK, "Ok, I will see you there then!")
						);
						option("Your father wishes to speak to you.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Your father wishes to speak to you.")
								.addNPC(NPC, HeadE.CALM_TALK, "My father? You have spoken to him recently?")
								.addPlayer(HeadE.HAPPY_TALKING, "He is dying and wishes you to be his heir")
								.addNPC(NPC, HeadE.CALM_TALK, "I have been told that before. I have not been able to find that castle again though.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I do have the means to get us there - a magic whistle!")
								.addItem(16, "You show him the magic whistle and explain where to blow it...", () -> {
									player.getInventory().deleteItem(new Item(16, 1));
									player.getQuestManager().setStage(Quest.HOLY_GRAIL, GIVE_AURTHUR_HOLY_GRAIL);
								})
								.addNPC(NPC, HeadE.CALM_TALK, "Ok, I will see you there then!")
						);
					}
				});
			if(!player.getInventory().containsItem(16))
				options.addPlayer(HeadE.AMAZED, "Found you!")
						.addNPC(NPC, HeadE.CALM_TALK, "That you did...")
						.addPlayer(HeadE.HAPPY_TALKING, "I need to give you a whistle...")
						.addNPC(NPC, HeadE.CALM_TALK, "A what?")
						.addPlayer(HeadE.HAPPY_TALKING, "I will be right back, stay here.")
						.addNPC(NPC, HeadE.CALM_TALK, "Okay");
			addNPC(NPC, HeadE.CALM_TALK, "Wow, thank you! I could hardly breathe in there!");
			addNext(options);
			return;
		}
		addNPC(NPC, HeadE.CALM_TALK, "Hi");
		addPlayer(HeadE.HAPPY_TALKING, "Hello");
		addNPC(NPC, HeadE.CALM_TALK, "...");
		addPlayer(HeadE.HAPPY_TALKING, "...");
		addNPC(NPC, HeadE.CALM_TALK, "See you later then?");
		addPlayer(HeadE.HAPPY_TALKING, "Yes...");
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new SirPercivalHolyGrailD(e.getPlayer()).getStart()));
}
