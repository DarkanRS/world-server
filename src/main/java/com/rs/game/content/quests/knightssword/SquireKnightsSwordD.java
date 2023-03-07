package com.rs.game.content.quests.knightssword;

import static com.rs.game.content.quests.knightssword.KnightsSword.*;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SquireKnightsSwordD extends Conversation {
	public static final int NEW_SWORD_CONVO = 0;
	public static final int ABUNDANT_WITH_SWORDS_CONVO = 1;
	public static final int DWARVES_MAKE_ANOTHER_CONVO = 2;
	public static final int VAGUE_AREA_CONVO = 3;

	public SquireKnightsSwordD(Player player) {
		super(player);

		if(player.getInventory().containsItem(BLURITE_SWORD)) {
			addNPC(SQUIRE, HeadE.CALM_TALK, "That's it! That's the sword I need!");
			addNPC(SQUIRE, HeadE.CALM_TALK, "Please let me have it.");
			addOptions("Give the sword?", new Options() {
				@Override
				public void create() {
					option("You can have it", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Of course you can have it")
							.addSimple("You give the Squire the Knight's Sword.")
							.addNext(()-> {
								player.getInventory().removeItems(new Item(BLURITE_SWORD, 1));
								player.getQuestManager().completeQuest(Quest.KNIGHTS_SWORD);
							})
							);
					option("I think I will keep it.", new Dialogue()
							.addPlayer(HeadE.SKEPTICAL_THINKING, "No, I think I will keep it")
							.addNPC(SQUIRE, HeadE.SAD, "Darn, what am I to do?")
							);
				}
			});

			return;
		}


		switch (player.getQuestManager().getStage(Quest.KNIGHTS_SWORD)) {
		case NOT_STARTED -> {
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("And how is life as a squire?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "And how is life as a squire?")
							.addNPC(SQUIRE, HeadE.CALM_TALK, "Well, Sir Vyvin is a good guy to work for, however, I'm in a spot of trouble today. I've gone " +
									"and lost Sir Vyvin's sword!")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("Do you know where you lost it?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Do you know where you lost it?")
											.addNPC(SQUIRE, HeadE.CALM_TALK, "Well now, if I knew THAT it wouldn't be lost, now would it?")
											.addOptions("Choose an option:", new Options() {
												@Override
												public void create() {
													option("Well, do you know the VAGUE AREA you lost it?", new Dialogue()
															.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, VAGUE_AREA_CONVO).getStart());}));
													option("I can make a new sword if you like...", new Dialogue()
															.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, NEW_SWORD_CONVO).getStart());}));
													option("Well, the kingdom is fairly abundant with swords...", new Dialogue()
															.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, ABUNDANT_WITH_SWORDS_CONVO).getStart());}));

												}
											}));
									option("I can make a new sword if you like...", new Dialogue()
											.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, NEW_SWORD_CONVO).getStart());}));
									option("Is he angry?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Is he angry?")
											.addNPC(SQUIRE, HeadE.CALM_TALK, "He doesn't know yet. I was hoping I could think of something to do before he does find out, But I find myself at a loss.")
											);
								}
							}));
					option("Wouldn't you prefer to be a squire for me?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Wouldn't you prefer to be a squire for me?")
							.addNPC(SQUIRE, HeadE.CALM_TALK, "No, sorry, I'm loyal to Sir Vyvin."));
				}
			});


		}
		case TALK_TO_RELDO -> {
			addNPC(SQUIRE, HeadE.CALM_TALK, "So how are you doing getting a sword?");
			addPlayer(HeadE.HAPPY_TALKING, "I'm looking for Reldo to help me.");
			addNPC(SQUIRE, HeadE.CALM_TALK, "Please try and find him quickly... I am scared Sir Vyvin will find out!");
		}
		case FIND_DWARF -> {
			addNPC(SQUIRE, HeadE.CALM_TALK, "So how are you doing getting a sword?");
			addPlayer(HeadE.HAPPY_TALKING, "I'm looking for Imcando dwarves to help me.");
			addNPC(SQUIRE, HeadE.CALM_TALK, "Please try and find them quickly... I am scared Sir Vyvin will find out!");
		}
		case GET_PICTURE -> {
			if(player.getQuestManager().getAttribs(Quest.KNIGHTS_SWORD).getB("picture_location_known")) {
				addNPC(SQUIRE, HeadE.CALM_TALK, "Did you find the portrait?");
				if(player.getInventory().containsItem(PORTRAIT)) {
					addPlayer(HeadE.HAPPY_TALKING, "Yes, I just need to take it to the dwarf");
					addNPC(SQUIRE, HeadE.CALM_TALK, "Great!");
				} else
					addPlayer(HeadE.CALM_TALK, "No, not yet");
				return;

			}
			addPlayer(HeadE.HAPPY_TALKING, "I found the dwarf!");
			addNPC(SQUIRE, HeadE.AMAZED_MILD, "You found the dwarf?");
			addPlayer(HeadE.HAPPY_TALKING, "Yes, I found the dwarf...");
			addNPC(SQUIRE, HeadE.CALM_TALK, "So... What happened?");
			addPlayer(HeadE.HAPPY_TALKING, "He needs a picture of the sword to remake it.");
			addNPC(SQUIRE, HeadE.SKEPTICAL_THINKING, "Hmm...");
			addNPC(SQUIRE, HeadE.CALM_TALK, "Well, Sir Vyvin keeps a portrait with him holding it in his cupboard in his room.");
			addNPC(SQUIRE, HeadE.CALM_TALK, "You should get it without him knowing though. Don't let him see you. His room is on the 3rd floor on the east" +
					" side of the castle.", ()->{
						player.getQuestManager().getAttribs(Quest.KNIGHTS_SWORD).setB("picture_location_known", true);
					});
		}
		case GET_MATERIALS -> {
			addPlayer(HeadE.HAPPY_TALKING, "I showed the dwarf the portrait. He said he could make it!");
			addNPC(SQUIRE, HeadE.CALM_TALK, "Oh I am glad to hear that!");
		}
		}
	}

	public SquireKnightsSwordD(Player player, int convoID) {
		super(player);

		switch(convoID) {
		case NEW_SWORD_CONVO:
			makeNewSwordConvo();
			break;
		case ABUNDANT_WITH_SWORDS_CONVO:
			abundantWithSwords();
			break;
		case DWARVES_MAKE_ANOTHER_CONVO:
			dwarvesMakeAnother();
			break;
		case VAGUE_AREA_CONVO:
			vagueArea();
			break;
		}

	}

	private void makeNewSwordConvo() {
		addPlayer(HeadE.HAPPY_TALKING, "I can make a new sword if you like...");
		addNPC(SQUIRE, HeadE.CALM_TALK, "Thanks for the offer. I'd be surprised if you could though.");
		addNPC(SQUIRE, HeadE.CALM_TALK, "The thing is, this sword is a family heirloom. It has been passed down through" +
				" Vyvin's family for five generations! It was originally made by the Imcando dwarves, who were");
		addNPC(SQUIRE, HeadE.CALM_TALK, "a particularly skilled tribe of dwarven smiths. I doubt anyone could make it" +
				" in the style they do.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("So would these dwarves make another one?", new Dialogue()
						.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, DWARVES_MAKE_ANOTHER_CONVO).getStart());}));
				option("Well I hope you find it soon.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Well, I hope you find it soon.")
						.addNPC(SQUIRE, HeadE.CALM_TALK, "Yes, me too. I'm not looking forward to telling Vyvin I've lost it. " +
								"He's going to want it for the parade next week as well."));
			}
		});
	}

	private void abundantWithSwords() {
		addPlayer(HeadE.HAPPY_TALKING, "Well, the kingdom is fairly abundant with swords...");
		addNPC(SQUIRE, HeadE.CALM_TALK, "Yes. You can get bronze swords anywhere. But THIS isn't any old sword.");
		addNPC(SQUIRE, HeadE.CALM_TALK, "The thing is, this sword is a family heirloom. It has been passed down through Vyvin's family for five generations! " +
				"It was originally made by the Imcando dwarves, who were");
		addNPC(SQUIRE, HeadE.CALM_TALK, "a particularly skilled tribe of dwarven smiths. I doubt anyone could make it in the style they do.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("So would these dwarves make another one?", new Dialogue()
						.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, DWARVES_MAKE_ANOTHER_CONVO).getStart());}));
				option("Well I hope you find it soon.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Well, I hope you find it soon.")
						.addNPC(SQUIRE, HeadE.CALM_TALK, "Yes, me too. I'm not looking forward to telling Vyvin I've lost it. " +
								"He's going to want it for the parade next week as well."));
			}
		});


	}

	private void dwarvesMakeAnother() {
		addPlayer(HeadE.HAPPY_TALKING, "So would these dwarves make another one?");
		addNPC(SQUIRE, HeadE.CALM_TALK, "I'm not a hundred percent sure the Imcando tribe exists anymore. " +
				"I should think Reldo, the palace librarian in Varrock, will know; he has done a lot of research " +
				"on the races of Gielinor.");
		addNPC(SQUIRE, HeadE.CALM_TALK, "I don't suppose you could try and track down the Imcando dwarves " +
				"for me? I've got so much work to do...");
		addOptions("Start Knight's Sword?", new Options() {
			@Override
			public void create() {
				option("Ok, I'll give it a go.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Ok, I'll give it a go.", () -> {
							player.getQuestManager().setStage(Quest.KNIGHTS_SWORD, TALK_TO_RELDO, true);
						})
						.addNPC(SQUIRE, HeadE.CALM_TALK, "Thank you very much! As I say, the best place to " +
								"start should be with Reldo..."));
				option("No, I've got lots of mining work to do.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "No, I've got lots of mining work to do.")
						.addNPC(SQUIRE, HeadE.CALM_TALK, "Oh man... I'm in such trouble..."));
			}
		});
	}

	private void vagueArea() {
		addPlayer(HeadE.HAPPY_TALKING, "Well, do you know the VAGUE AREA you lost it in?");
		addNPC(SQUIRE, HeadE.CALM_TALK, "No. I was carrying it for him all the way from where he had it stored" +
				" in Lumbridge. It must have slipped from my pack during the trip, and you know what people are like these days...");
		addNPC(SQUIRE, HeadE.CALM_TALK, "Someone will have just picked it up and kept it for themselves.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("I can make a new sword if you like...", new Dialogue()
						.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, NEW_SWORD_CONVO).getStart());}));
				option("Well, the kingdom is fairly abundant with swords...", new Dialogue()
						.addNext(()->{player.startConversation(new SquireKnightsSwordD(player, ABUNDANT_WITH_SWORDS_CONVO).getStart());}));
				option("Well I hope you find it soon.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Well, I hope you find it soon.")
						.addNPC(SQUIRE, HeadE.CALM_TALK, "Yes, me too. I'm not looking forward to telling Vyvin I've lost it. " +
								"He's going to want it for the parade next week as well."));
			}
		});
	}
}
