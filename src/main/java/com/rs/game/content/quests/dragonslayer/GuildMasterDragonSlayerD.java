package com.rs.game.content.quests.dragonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.*;

@PluginEventHandler
public class GuildMasterDragonSlayerD extends Conversation {
	private final int MAIN_OPTIONS = 0;
	private final int ROUTE_TO_CRANDOR = 1;
	private final int WHERE_IS_THALZAR = 2;
	private final int WHERE_IS_LOZAR = 3;
	private final int WHERE_IS_MELZAR = 4;
	private final int BUYING_SHIP = 5;
	private final int DRAGON_BREATH = 6;


	public GuildMasterDragonSlayerD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.DRAGON_SLAYER)) {
		case NOT_STARTED -> {
			addPlayer(HeadE.SKEPTICAL_THINKING, "Can I have a quest?");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Aha! Yes! A mighty and perilous quest fit for the most powerful champions! And, at the end of it, " +
					"you will earn the right to wear the legendary rune platebody!");
			addOptions("Start Dragon Slayer?", new Options() {
				@Override
				public void create() {
					option("Yes", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "So, what is this quest?")
							.addNPC(GUILD_MASTER, HeadE.CALM_TALK, "You'll have to speak to Oziach, the maker of rune armour. He sets the quests that " +
									"champions must complete in order to wear it. Oziach lives in a hut by the cliffs to the west of Edgeville. He can be a " +
									"little ... odd ... sometimes, though.", ()->{p.getQuestManager().setStage(Quest.DRAGON_SLAYER, TALK_TO_OZIACH);}));
					option("No", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "On second thoughts, a mighty and perilous quest fit only for the most powerful champions " +
									"isn't something I want to commit to at the moment. Maybe later.")
							.addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Very well. Return when are ready"));
				}
			});

		}
		case TALK_TO_OZIACH -> {
			addPlayer(HeadE.HAPPY_TALKING, "Can I have a quest?");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "You're already on a quest for me, if I recall correctly. Have you talked to Oziach yet?");
			addPlayer(HeadE.HAPPY_TALKING, "No, not yet.");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Well, he's the only one who can grant you the right to wear rune platemail. He lives in a hut, " +
					"by the cliffs west of Edgeville.");
			addPlayer(HeadE.HAPPY_TALKING, "Okay I'll go and talk to him.");

		}
		case TALK_TO_GUILDMASTER -> {
			if(!p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(STARTED_DIALOGUE_GUILDMASTER_ATTR)) {
				addPlayer(HeadE.HAPPY_TALKING, "I talked to Oziach ... and he gave me a quest.");
				addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Oh? What did he tell you to do?");
				addPlayer(HeadE.HAPPY_TALKING, "Defeat the dragon of Crandor.");
				addNPC(GUILD_MASTER, HeadE.CALM_TALK, "The dragon of Crandor?");
				addPlayer(HeadE.HAPPY_TALKING, "Um, yes ...");
			}
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Goodness, he hasn't given you an easy job, has he?");
			addPlayer(HeadE.HAPPY_TALKING, "What's so special about this dragon?");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Thirty years ago, Crandor was a thriving community with a great tradition of mages and adventurers. " +
					"Many Crandorians even earned the right to be part of the Champions' Guild! One of their adventurers went too far, however.");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "He descended into the volcano in the centre of Crandor and woke the dragon Elvarg. He must have " +
					"fought valiantly against the dragon because they say that, to this day, she has a scar down her side.");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "But the dragon still won the fight. She emerged and laid waste to the whole of Crandor with her fire " +
					"breath! Some refugees managed to escape in fishing boats.");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "They landed on the coast, north of Rimmington, and set up camp but the dragon followed them and burned " +
					"the cramp to the ground. Out of all of the people of Crandor there were only three survivors: a trio of wizards who used magic to escape.");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Their names were Thalzar, Lozar, and Melzar. If you're serious about taking on Elvarg, first you'll need to " +
					"get to Crandor. The island is surrounded by dangerous reefs, so you'll need a ship capable of getting through them and a map to show you the way.");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "When you reach Crandor, you'll also need some kind of protection against the dragon's breath.");
			addNext(()->{
				p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(STARTED_DIALOGUE_GUILDMASTER_ATTR, true);
				p.startConversation(new GuildMasterDragonSlayerD(p, MAIN_OPTIONS, MAIN_OPTIONS).getStart());
			});
		}
		case PREPARE_FOR_CRANDOR -> {
			addPlayer(HeadE.HAPPY_TALKING, "About my quest to kill the dragon..");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "If you're serious about taking on Elvarg, first you'll need to get to Crandor. The island is surrounded" +
					" by dangerous reefs, so you'll need a ship capable of getting through them and a map to show you the way.");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "When you reach Crandor, you'll also need some kind of protection against the dragon's breath.");
			addNext(()->{
				p.startConversation(new GuildMasterDragonSlayerD(p, MAIN_OPTIONS, MAIN_OPTIONS).getStart());
			});
		}
		case REPORT_TO_OZIACH -> {
			addPlayer(HeadE.HAPPY_TALKING, "I killed the dragon!");
			addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Really? That's amazing! You are a true hero. There's no way that Oziach can refuse to sell you rune " +
					"platemail now. You should tell him at once!");
		}

		case QUEST_COMPLETE ->  {
			//Will not talk about Dragon Slayer
		}
		}


	}

	public GuildMasterDragonSlayerD(Player p, int convoID, int previousID) {
		super(p);
		mainOptions(p, previousID);
	}

	public GuildMasterDragonSlayerD(Player p, int convoID) {
		super(p);
		switch(convoID) {
		case ROUTE_TO_CRANDOR -> {
			routeToCrandor(p);
		}
		case WHERE_IS_THALZAR -> {
			whereisThalzarMapPiece(p);
		}
		case WHERE_IS_LOZAR -> {
			whereisLozarMapPiece(p);
		}
		case WHERE_IS_MELZAR -> {
			whereisMelzarsMapPiece(p);
		}
		case BUYING_SHIP -> {
			buyingShip(p);
		}
		case DRAGON_BREATH -> {
			dragonBreath(p);
		}
		}
	}

	private void mainOptions(Player p, int previous) {
		if(isFinished(p) && !p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(FINISHED_DIALOGUE_GUILDMASTER_ATTR)) {
			p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(FINISHED_DIALOGUE_GUILDMASTER_ATTR, true);
			p.getQuestManager().setStage(Quest.DRAGON_SLAYER, PREPARE_FOR_CRANDOR);
		}

		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(KNOWS_MAP_EXISTS_ATTR)) {
					if(previous != WHERE_IS_THALZAR)
						option("Where is Thalzar's map piece?", new Dialogue()
								.addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, WHERE_IS_THALZAR).getStart());}));
					if(previous != WHERE_IS_LOZAR)
						option("Where is Lozar's map piece?", new Dialogue()
								.addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, WHERE_IS_LOZAR).getStart());}));
					if(previous != WHERE_IS_MELZAR)
						option("Where is Melzar's map piece?", new Dialogue()
								.addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, WHERE_IS_MELZAR).getStart());}));
				} else
					option("How can I find the route to Crandor?", new Dialogue()
							.addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, ROUTE_TO_CRANDOR).getStart());}));
				if(previous != BUYING_SHIP)
					option("Where can I find the right ship?", new Dialogue()
							.addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, BUYING_SHIP).getStart());}));
				if(previous != DRAGON_BREATH)
					option("How can I protect myself from the dragon's breath?", new Dialogue()
							.addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, DRAGON_BREATH).getStart());}));
				if(p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(FINISHED_DIALOGUE_GUILDMASTER_ATTR))
					option("Okay, I'll get going!", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Okay, I'll get going!"));
			}
		});

	}

	private boolean isFinished(Player p) {
		GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER);
		return attr.getB(KNOWS_MAP_EXISTS_ATTR) && attr.getB(KNOWS_ABOUT_THALZAR_MAP_ATTR) && attr.getB(KNOWS_ABOUT_LOZAR_MAP_ATTR) && attr.getB(KNOWS_ABOUT_MELZAR_MAP_ATTR)
				&& attr.getB(KNOWS_ABOUT_SHIP_ATTR) && attr.getB(KNOWS_ABOUT_DRAGON_BREATH_ATTR);
	}

	private void routeToCrandor(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "How can I find the route to Crandor?");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Only one map exists that shows the route through the reefs of Crandor. The map was split into three pieces by Melzar, " +
				"Thalzar, and Lozar, the wizards who escaped from the dragon. Each of them took one piece.", ()->{
					p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(KNOWS_MAP_EXISTS_ATTR, true);
				});
		addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, DRAGON_BREATH, ROUTE_TO_CRANDOR).getStart());});
	}

	private void whereisThalzarMapPiece(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Where is Thalzar's map piece?");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Thalzar was the most paranoid of the three wizards. He hid his map piece and took the secret of its location " +
				"to his grave. I don't think you'd be able to find out where it is by ordinary means. " +
				"You'll need to talk to the Oracle on Ice Mountain.", ()->{
					p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(KNOWS_ABOUT_THALZAR_MAP_ATTR, true);
				});
		addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, MAIN_OPTIONS, WHERE_IS_THALZAR).getStart());});
	}
	private void whereisLozarMapPiece(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Where is Lozar's map piece?");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "A few weeks ago, I'd have told you to speak to Lozar herself, in her house across the river from Lumbridge. " +
				"Unfortunately, goblin raiders killed her, and stole everything. One of the goblins from the Goblin Village probably has the map piece now.", ()->{
					p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(KNOWS_ABOUT_LOZAR_MAP_ATTR, true);
				});
		addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, MAIN_OPTIONS, WHERE_IS_LOZAR).getStart());});
	}
	private void whereisMelzarsMapPiece(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Where is Melzar's map piece?");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Melzar built a castle on the site of the Crandorian refugee camp, north of Rimmington. He's locked " +
				"himself in there and no one's seen him for years. The inside of his castle is like a maze, and is populated by undead monsters.");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Maybe, if you could get all the way through the maze, you could find his piece of the map. Adventurers " +
				"sometimes go in there to prove themselves, so I can give you this key to Melzar's Maze.", ()->{
					if(!p.getInventory().containsItem(MELZAR_MAZE_KEY, 1))
						p.getInventory().addItem(new Item(MELZAR_MAZE_KEY, 1), true);
					p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(KNOWS_ABOUT_MELZAR_MAP_ATTR, true);
				});
		addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, MAIN_OPTIONS, WHERE_IS_MELZAR).getStart());});
	}

	private void buyingShip(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Where can I find the right ship?");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Even if you find the right route, only a ship made to the old crandorian design would be able to navigate " +
				"through the reefs to the island. If there's still one in existence, it's probably in Port Sarim.");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "Then, of course, you'll need to find a captain willing to sail to Crandor, and I'm not sure where you'd find " +
				"one of them!", ()->{
					p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(KNOWS_ABOUT_SHIP_ATTR, true);
				});
		addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, MAIN_OPTIONS, BUYING_SHIP).getStart());});
	}

	private void dragonBreath(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "How can I protect myself from the dragon's breath?");
		addNPC(GUILD_MASTER, HeadE.CALM_TALK, "That part shouldn't be too difficult, actually, I believe the Duke of Lumbridge has a special shield in his " +
				"armoury that is enchanted against dragon's breath.", ()->{
					p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(KNOWS_ABOUT_DRAGON_BREATH_ATTR, true);
				});
		addNext(()->{p.startConversation(new GuildMasterDragonSlayerD(p, MAIN_OPTIONS, DRAGON_BREATH).getStart());});
	}
}
