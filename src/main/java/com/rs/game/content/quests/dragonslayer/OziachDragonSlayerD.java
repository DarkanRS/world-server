package com.rs.game.content.quests.dragonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.*;

@PluginEventHandler
public class OziachDragonSlayerD extends Conversation {
	private final int DRAGON_SOUNDS_FUN = 0;

	public OziachDragonSlayerD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.DRAGON_SLAYER)) {
		case NOT_STARTED -> {
			addPlayer(HeadE.HAPPY_TALKING, "Good day to you.");
			addNPC(OZIACH, HeadE.CALM_TALK, "Aye, 'tis a fair day my friend");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("I'm not your friend", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not your friend.")
							.addNPC(OZIACH, HeadE.CALM_TALK, "I'm surprised if you're anyone's friend with those kind of manners."));
					option("Yes, it's a very nice day.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Yes, it's a very nice day.")
							.addNPC(OZIACH, HeadE.CALM_TALK, "Aye, may the gods walk by yer side. Now leave me alone."));
				}
			});

		}
		case TALK_TO_OZIACH -> {
			addPlayer(HeadE.HAPPY_TALKING, "Good day to you.");
			addNPC(OZIACH, HeadE.CALM_TALK, "Aye, 'tis a fair day my friend");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("About Dragon Slayer?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Can you sell me a rune platebody?")
							.addNPC(OZIACH, HeadE.CALM_TALK, "So, how does thee know I 'ave some?")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("The Guildmaster of the Champion's Guild told me.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "The Guildmaster of the Champion's Guild told me.")
											.addNPC(OZIACH, HeadE.CALM_TALK, "Yes, I suppose he would, wouldn't he? He's always sending you fancy-pants " +
													"'heroes' up to bother me. Telling me I'll give them a quest or sommat like that. Well, I'm not going to " +
													"let just anyone wear my rune platemail. It's only for heroes. So leave me alone.")
											.addOptions("Choose an option:", new Options() {
												@Override
												public void create() {
													option("I thought you were going to give me a quest.", new Dialogue()
															.addPlayer(HeadE.HAPPY_TALKING, "I thought you were going to give me a quest.")
															.addNPC(OZIACH, HeadE.CALM_TALK, "*sigh* All right, I'll give ye a quest. I'll let ye wear my " +
																	"rune platemail if ye.. Slay the dragon of Crandor!")
															.addNext(()->{p.startConversation(new OziachDragonSlayerD(p, DRAGON_SOUNDS_FUN).getStart());}));
													option("That's a pity. I'm not a hero", new Dialogue()
															.addPlayer(HeadE.HAPPY_TALKING, "That's a pity. I'm not a hero.")
															.addNPC(OZIACH, HeadE.CALM_TALK, "Aye, I ken tell!"));
												}
											}));
									option("I am a master detective", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I am a master detective.")
											.addNPC(OZIACH, HeadE.CALM_TALK, "Well, however you found out about it, I'm not going to let just anyone wear my " +
													"rune platemail. It's only for heroes. So leave me alone.")
											.addOptions("Choose an option:", new Options() {
												@Override
												public void create() {
													option("I thought you were going to give me a quest.", new Dialogue()
															.addPlayer(HeadE.HAPPY_TALKING, "I thought you were going to give me a quest.")
															.addNPC(OZIACH, HeadE.CALM_TALK, "*sigh* All right, I'll give ye a quest. I'll let ye wear my " +
																	"rune platemail if ye.. Slay the dragon of Crandor!")
															.addNext(()->{p.startConversation(new OziachDragonSlayerD(p, DRAGON_SOUNDS_FUN).getStart());}));
													option("That's a pity. I'm not a hero.", new Dialogue()
															.addPlayer(HeadE.HAPPY_TALKING, "That's a pity. I'm not a hero.")
															.addNPC(OZIACH, HeadE.CALM_TALK, "Aye, I ken tell!"));
												}
											}));
								}
							}));
					option("I'm not your friend", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not your friend.")
							.addNPC(OZIACH, HeadE.CALM_TALK, "I'm surprised if you're anyone's friend with those kind of manners."));
					option("Yes, it's a very nice day.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Yes, it's a very nice day.")
							.addNPC(OZIACH, HeadE.CALM_TALK, "Aye, may the gods walk by yer side. Now leave me alone."));
				}
			});
		}
		case TALK_TO_GUILDMASTER, PREPARE_FOR_CRANDOR -> {
			addPlayer(HeadE.HAPPY_TALKING, "Good day to you.");
			addNPC(OZIACH, HeadE.CALM_TALK, "Have ye slayed that dragon yet?");
			addPlayer(HeadE.HAPPY_TALKING, "Um... no.");
			addNPC(OZIACH, HeadE.CALM_TALK, "Be off with ye then.");
		}
		case REPORT_TO_OZIACH -> {
			addPlayer(HeadE.HAPPY_TALKING, "Good day to you.");
			addNPC(OZIACH, HeadE.CALM_TALK, "Have ye slayed that dragon yet?");
			if(p.getInventory().containsItem(ELVARG_HEAD))
				addPlayer(HeadE.HAPPY_TALKING, "I have its head here.");
			addNPC(OZIACH, HeadE.CALM_TALK, "You actually did it? I underestimated ye, adventurer. I apologise. Yer a true hero, and I'd be happy to sell " +
					"ye rune platebodies.");
			addNext(()->{p.getQuestManager().completeQuest(Quest.DRAGON_SLAYER);});
		}

		case QUEST_COMPLETE ->  {
			//Never reached
		}
		}


	}

	public OziachDragonSlayerD(Player p, int convoID) {
		super(p);
		switch(convoID) {
		case DRAGON_SOUNDS_FUN -> {
			dragonSoundsFun(p);
		}
		}
	}

	private void dragonSoundsFun(Player p) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("A dragon, that sounds like fun.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "A dragon, that sounds like fun.")
						.addNPC(OZIACH, HeadE.CALM_TALK, "Hah, yes, you are a typical reckless adventurer, aren't you? Now go kill the dragon and get out" +
								" of my face.")
						.addPlayer(HeadE.HAPPY_TALKING, "But how can I defeat the dragon?")
						.addNPC(OZIACH, HeadE.CALM_TALK, "Go talk to the Guildmaster in the Champions' Guild. He'll help ye out if yer so keen on doing a quest. " +
								"I'm not going to be handholding any adventurers.", ()->{p.getQuestManager().setStage(Quest.DRAGON_SLAYER, TALK_TO_GUILDMASTER, true);}));
				option("I may be a champion, but I don't think I'm, up to dragon-killing yet.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I may be a champion, but I don't think I'm, up to dragon-killing yet.")
						.addNPC(OZIACH, HeadE.CALM_TALK, "Yes, I can understand that. Yer a coward."));
			}
		});


	}



}
