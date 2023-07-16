package com.rs.game.content.quests.heroesquest.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.heroesquest.HeroesQuest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.heroesquest.HeroesQuest.GET_ITEMS;
import static com.rs.game.content.quests.heroesquest.HeroesQuest.NOT_STARTED;

@PluginEventHandler
public class AchiettiesHeroesQuestD extends Conversation {
	private static final int NPC = 796;

	public AchiettiesHeroesQuestD(Player player) {
		super(player);
		Dialogue itemsOptions = new Dialogue();
		itemsOptions.addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Any hints on getting the thieves armband?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Any hints on getting the thieves armband?")
						.addNPC(NPC, HeadE.CALM_TALK, "I'm sure you have relevant contacts to find out about that.")
						.addNext(() -> player.startConversation(new Conversation(itemsOptions))));
				option("Any hints on getting the feather?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Any hints on getting the feather?")
						.addNPC(NPC, HeadE.CALM_TALK, "Not really - other than Entrana firebirds tend to live on Entrana")
						.addNext(() -> player.startConversation(new Conversation(itemsOptions))));
				option("Any hints on getting the eel?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Any hints on getting the eel?")
						.addNPC(NPC, HeadE.CALM_TALK, "Maybe go and find someone who knows a lot about fishing? (Try Gerrant in Port Sarim...)")
						.addNext(() -> player.startConversation(new Conversation(itemsOptions))));
				option("I'll start looking for all those things then", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I'll start looking for all those things then")
						.addNPC(NPC, HeadE.CALM_TALK, "Good luck with that."));
			}
		});
		switch (player.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case NOT_STARTED -> {
				Dialogue startQuest = new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I'm a hero - may I apply to join?")
						.addOptions("Start Heroes Quest?", new Options() {
							@Override
							public void create() {
								option("Yes", new Dialogue()
										.addNPC(NPC, HeadE.CALM_TALK, "Well, you have a lot of quest points, and you have done all of the required quests, " +
												"so you may now begin the tasks to meet the entry requirements for membership in the Heroes' Guild.", () -> {
											player.getQuestManager().setStage(Quest.HEROES_QUEST, 1);
										})
										.addNPC(NPC, HeadE.CALM_TALK, "The three items required for entrance are: An Entranan Firebird feather, a Master " +
												"Thieves' armband, and a cooked Lava Eel.")
										.addNext(itemsOptions));
								option("No", new Dialogue());
							}
						});
				addNPC(NPC, HeadE.CALM_TALK, "Greetings. Welcome to the Heroes' Guild.");
				addNPC(NPC, HeadE.CALM_TALK, "Only the greatest heroes of this land may gain entrance to this guild.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						if (HeroesQuest.meetsRequirements(player))
							option("I'm a hero, may I apply to join?", startQuest);
						else
							option("I'm a hero, may I apply to join?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I'm a hero - may I apply to join?")
									.addNPC(NPC, HeadE.CALM_TALK, "You're a hero? I've never heard of YOU. You are required to possess at least 56 quest " +
											"points to file an application.")
									.addNPC(NPC, HeadE.CALM_TALK, "Additionally you must have completed the Shield of Arrav, Lost City, Merlin's Crystal " +
											"and Dragon Slayer.")
									.addNext(() -> {
										player.getQuestManager().showQuestDetailInterface(Quest.HEROES_QUEST);
									})
							);
						option("Good for the foremost heroes of the land.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Good for the foremost heroes of the land.")
								.addNPC(NPC, HeadE.CALM_TALK, "Yes. Yes it is.")
						);
					}
				});
			}
			case GET_ITEMS -> {
				addNPC(NPC, HeadE.CALM_TALK, "Greetings. Welcome to the Heroes' Guild. How goes thy quest adventurer?");
				if (hasAllItems()) {
					addPlayer(HeadE.HAPPY_TALKING, "I have all the required items.");
					addNPC(NPC, HeadE.CALM_TALK, "I see that you have. Well done. Now, to complete the quest, and gain entry to the Heroes' Guild in your" +
							" final task all that you have to do is...");
					addPlayer(HeadE.AMAZED, "W-what? What do you mean? There's MORE?");
					addNPC(NPC, HeadE.CALM_TALK, "I'm sorry, I was just having a little fun with you. Just a little Heroes' Guild humour there. What I really meant was...");
					addNPC(NPC, HeadE.CALM_TALK, "Congratulations! You have completed the Heroes' Guild entry requirements! You will find the door now open for you! Enter, Hero! And take this reward!");
					addNext(() -> {
						player.getQuestManager().completeQuest(Quest.HEROES_QUEST);
						player.getInventory().removeItems(new Item(2149, 1), new Item(1579, 1), new Item(1583, 1));
					});
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "It's tough. I've not done it yet.");
				addNPC(NPC, HeadE.CALM_TALK, "Remember, the items you need to enter are: An Entranan Firebirds' feather, A Master Thieves armband, and a " +
						"cooked Lava Eel.");
				addNext(itemsOptions);
			}
		}
	}

	public boolean hasAllItems() {
		return player.getInventory().containsItems(new Item(2149, 1), new Item(1579, 1), new Item(1583, 1));
	}

	public static NPCClickHandler handleAchietties = new NPCClickHandler(new Object[] { 796 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.HEROES_QUEST)) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				int NPC = e.getNPCId();

				{
					addNPC(NPC, HeadE.CALM_TALK, "Greetings, welcome to the heroes guild!");
					addPlayer(HeadE.HAPPY_TALKING, "Thank you...");
					addNPC(NPC, HeadE.CALM_TALK, "You're welcome.");
					create();
				}
			});
		} else {
			e.getPlayer().startConversation(new AchiettiesHeroesQuestD(e.getPlayer()).getStart());
		}
	});
}
