package com.rs.game.content.quests.templeofikov.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.templeofikov.TempleOfIkov;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.templeofikov.TempleOfIkov.*;

@PluginEventHandler
public class LucienArdougneTempleOfIkov extends Conversation {
	private static final int NPC = 8345;

	class QuestPrompt extends Conversation {
		public QuestPrompt(Player player) {
			super(player);
			if(TempleOfIkov.meetsRequirements(player)) {
				addOptions("Start Temple Of Ikov?", new Options() {
					@Override
					public void create() {
						option("Yes", new Dialogue()
								.addPlayer(HeadE.LAUGH, "This will probably be easy!")
								.addNPC(NPC, HeadE.CALM_TALK, "It's not as easy as it sounds. The monster can only be killed with a weapon of ice. There are many other dangers.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'm up for it!")
								.addNPC(NPC, HeadE.CALM_TALK, "You will need my pendent. Without it you will not be able to enter the Chamber of Fear.")
								.addItem(86, "Lucien gives you his pendant", () -> {
									player.getQuestManager().setStage(Quest.TEMPLE_OF_IKOV, HELP_LUCIEN);
									player.getInventory().addItem(new Item(86, 1), true);
								})
								.addNPC(NPC, HeadE.CALM_TALK, "I cannot stay here much longer. I will be west of the Grand Exchange in Varrock. I have a small holding up there.")
						);
						option("No", new Dialogue());
					}
				});
			}
			if(!TempleOfIkov.meetsRequirements(player)) {
				addNPC(NPC, HeadE.CALM_TALK, "A laugh you say? You do not seem to have the skill required...");
				addNext(addNext(() -> {
					player.getQuestManager().showQuestDetailInterface(Quest.TEMPLE_OF_IKOV);
				}));
			}
		}
	}

	public LucienArdougneTempleOfIkov(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.TEMPLE_OF_IKOV)) {
			case NOT_STARTED -> {
				Dialogue mainOptions = new Dialogue();
				mainOptions.addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Why can't you get it yourself?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Why can't you get it yourself?")
								.addNPC(NPC, HeadE.CALM_TALK, "The guardians of the Staff of Armadyl fear me! They have set up a magical barrier which even my power cannot overcome!")
								.addNext(mainOptions)
						);
						option("That sounds like a laugh!", new Dialogue()
								.addPlayer(HeadE.LAUGH, "That sounds like a laugh!")
								.addNext(() -> player.startConversation(new LucienArdougneTempleOfIkov.QuestPrompt(player).getStart()))
						);
						option("Oh no! Sounds far too dangerous.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Oh no! Sounds far too dangerous.")
								.addNPC(NPC, HeadE.CALM_TALK, "Wimp! Call yourself a hero?! My daughter is more a hero than you!")
						);
						option("What's the reward?!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What's the reward?!")
								.addNPC(NPC, HeadE.CALM_TALK, "I see you are the mercenary type.")
								.addPlayer(HeadE.HAPPY_TALKING, "It's a living.")
								.addNPC(NPC, HeadE.CALM_TALK, "I will reward you well if you bring me the staff.")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("That sounds like a laugh!", new Dialogue()
												.addPlayer(HeadE.LAUGH, "That sounds like a laugh!")
												.addNext(() -> player.startConversation(new LucienArdougneTempleOfIkov.QuestPrompt(player).getStart()))
										);
										option("I'll pass this time.", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "I'll pass this time.")
												.addNPC(NPC, HeadE.FRUSTRATED, "Hmph.")
										);
									}
								})
						);
					}
				});
				addNPC(NPC, HeadE.CALM_TALK, "I seek a hero to go on an important mission!");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I'm a mighty hero!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'm a mighty hero!")
								.addNPC(NPC, HeadE.CALM_TALK, "I require the Staff of Armadyl. It is in the deserted Temple of Ikov, near Hemenster, north " +
										"east of here. Take care hero! There is a dangerous monster somewhere in the temple!")
								.addNext(mainOptions)
						);
						option("Yep, lots of heroes about these days.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Yep, lots of heroes about these days.")
								.addNPC(NPC, HeadE.CALM_TALK, "Well, if you see any be sure to point them in my direction.")
						);
					}
				});
			}
			case HELP_LUCIEN -> {
				addNPC(NPC, HeadE.CALM_TALK, "I told you not to meet me here again!");
				addPlayer(HeadE.HAPPY_TALKING, "Sorry! Can you remind me of my mission?");
				addNPC(NPC, HeadE.CALM_TALK, "My patience grows thin hero! I need the Staff of Armadyl. It's in the Temple of Ikov, near Hemenster, north east of here.");
				addPlayer(HeadE.HAPPY_TALKING, "Okay great.");
				if(player.getInventory().getAmountOf(86) == 0) {
					addPlayer(HeadE.SCARED, "Umm...");
					addNPC(NPC, HeadE.FRUSTRATED, "What is it?");
					addPlayer(HeadE.SECRETIVE, "I lost the pendent you gave me...");
					addItem(86, "Lucien does a slight of hand and gives you his pendant", ()->{
						player.getInventory().addItem(new Item(86, 1), true);
					});
				}
			}
			case QUEST_COMPLETE ->  {
				if(TempleOfIkov.isLucienSide(player)) {
					addNPC(NPC, HeadE.CALM_TALK, "I told you not to meet me here again!");
					addPlayer(HeadE.AMAZED, "Geez, okay!");
					return;
				}
				addNPC(NPC, HeadE.CALM_TALK, "Do not speak to me!");
				addPlayer(HeadE.HAPPY_TALKING, "...");
			}
		}
	}


    public static NPCClickHandler handleLucienFromArdougne = new NPCClickHandler(new Object[] {NPC}, e -> e.getPlayer().startConversation(new LucienArdougneTempleOfIkov(e.getPlayer()).getStart()));
}
