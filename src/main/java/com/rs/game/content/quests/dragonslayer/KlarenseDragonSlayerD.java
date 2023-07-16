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
public class KlarenseDragonSlayerD extends Conversation {
	private final int MAIN_OPTIONS = 0;
	private final int WHEN_SEAWORTHY = 1;
	private final int WOULD_YOU_TAKE_ME = 2;
	private final int WHY_IS_SHE_DAMAGED = 3;
	private final int ID_LIKE_TO_BUY_HER = 4;




	public KlarenseDragonSlayerD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.DRAGON_SLAYER)) {
		case NOT_STARTED, TALK_TO_OZIACH, TALK_TO_GUILDMASTER -> {
			addNPC(KLARENSE, HeadE.CALM_TALK, "The Lady Lumbridge is for sale, in case you are interested.");
			addPlayer(HeadE.HAPPY_TALKING, "Oh this boat?");
			addNPC(KLARENSE, HeadE.CALM_TALK, "Yes, the boat sells with Jenkins The Cabinboy.");
			addPlayer(HeadE.CALM_TALK, "No thank you...");
		}
		case PREPARE_FOR_CRANDOR -> {
			if(p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(OWNS_BOAT_ATTR)) {
				if(p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(IS_BOAT_FIXED_ATTR)) {
					addNPC(KLARENSE, HeadE.CALM_TALK, "Hello, captain! Here to take your new ship for a spin?");
					addPlayer(HeadE.HAPPY_TALKING, "Yes, I will be headed to Crandor soon.");
				} else {
					addNPC(KLARENSE, HeadE.CALM_TALK, "Hello, captain! Here to inspect your new ship? Just a little work and she'll be seaworthy again.");
					addPlayer(HeadE.HAPPY_TALKING, "So, what needs fixing on the ship?");
					addNPC(KLARENSE, HeadE.CALM_TALK, "Well, the big gaping hole in the hold is the problem. You'll need a few wooden planks hammered in with steel nails to fix it.");
				}
			} else {
				addNPC(KLARENSE, HeadE.CALM_TALK, "So, are you interested in buying a ship? Now, I'll be straight with you: She's not quite seaworthy right now," +
						" but give her a bit of work and she'll be the nippiest ship this side of Port Khazard.");
				addNext(() -> {
					p.startConversation(new KlarenseDragonSlayerD(p, MAIN_OPTIONS, MAIN_OPTIONS).getStart());
				});
			}
		}
		case REPORT_TO_OZIACH -> {
			addNPC(KLARENSE, HeadE.CALM_TALK, "It's a miracle, The Lady Lumbridge washed up back over here. It needs repairs in the hull again though.");
			addPlayer(HeadE.HAPPY_TALKING, "Wow, now I can go to Crandor when I please?");
			addNPC(KLARENSE, HeadE.CALM_TALK, "That you may.");
		}
		case QUEST_COMPLETE ->  {
			//Will not talk about Dragon Slayer
		}
		}


	}

	public KlarenseDragonSlayerD(Player p, int convoID, int previousID) {
		super(p);
		mainOptions(p, previousID);
	}

	public KlarenseDragonSlayerD(Player p, int convoID) {
		super(p);
		switch(convoID) {
		case WHEN_SEAWORTHY -> {
			whenSeaworthy(p);
		}
		case WOULD_YOU_TAKE_ME -> {
			wouldYouTakeMe(p);
		}
		case WHY_IS_SHE_DAMAGED -> {
			whyIsSheDamaged(p);
		}
		case ID_LIKE_TO_BUY_HER -> {
			idLikeToBuyHer(p);
		}
		}
	}

	private void mainOptions(Player p, int previous) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(previous!=WHEN_SEAWORTHY)
					option("Do you know when she will be seaworthy?", new Dialogue()
							.addNext(()->{p.startConversation(new KlarenseDragonSlayerD(p, WHEN_SEAWORTHY).getStart());}));
				if(previous!=WOULD_YOU_TAKE_ME)
					option("Would you take me to Crandor when she's ready?", new Dialogue()
							.addNext(()->{p.startConversation(new KlarenseDragonSlayerD(p, WOULD_YOU_TAKE_ME).getStart());}));
				if(previous!=WHY_IS_SHE_DAMAGED)
					option("Why is she damaged?", new Dialogue()
							.addNext(()->{p.startConversation(new KlarenseDragonSlayerD(p, WHY_IS_SHE_DAMAGED).getStart());}));
				if(previous!=ID_LIKE_TO_BUY_HER)
					option("I'd like to buy her.", new Dialogue()
							.addNext(()->{p.startConversation(new KlarenseDragonSlayerD(p, ID_LIKE_TO_BUY_HER).getStart());}));
				option("Ah well, never mind", new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Ah well, never mind"));
			}
		});

	}

	private void whenSeaworthy(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Do you know when she will be seaworthy?");
		addNPC(KLARENSE, HeadE.CALM_TALK, "No, not really. Port Sarim's shipbuilders aren't very efficient so it could be quite a while");
		addNext(()->{p.startConversation(new KlarenseDragonSlayerD(p, MAIN_OPTIONS, WHEN_SEAWORTHY).getStart());});
	}

	private void wouldYouTakeMe(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Would you take me to Crandor when she's ready?");
		addNPC(KLARENSE, HeadE.CALM_TALK, "Crandor? You're joking, right?");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Yes. Ha ha ha!", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Yes. Ha ha ha!")
						.addNPC(KLARENSE, HeadE.CALM_TALK, "Crandor's not something we sailors joke about. You can't sail from here to Catherby, or Entrana, or " +
								"Ardougne without going past that accursed island. You can't get to close to it because of the reefs, but you can always see it.")
						.addNPC(KLARENSE, HeadE.CALM_TALK, "Sometimes you can see a dark shape in the sky, circling the island. That's when you have to sail " +
								"on as quick as you can and pray it's not hungry. Every year, more ships are lost to that dragon")
						.addNext(()->{new KlarenseDragonSlayerD(p, MAIN_OPTIONS, WOULD_YOU_TAKE_ME).getStart();}));
				option("No. I want to go to Crandor.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "No. I want to go to Crandor.")
						.addNPC(KLARENSE, HeadE.CALM_TALK, "Then you must be crazy. That island is surrounded by reefs that would rip this ship to shreds. " +
								"Even if you found a map, you'd need an experienced captain to stand a chance of getting through and, even if I could get to it,")
						.addNPC(KLARENSE, HeadE.CALM_TALK, "there's no way I'm going any closer to that dragon than I have to. They say it can destroy whole ships with one bite.")
						.addNext(()->{p.startConversation(new KlarenseDragonSlayerD(p, MAIN_OPTIONS, WOULD_YOU_TAKE_ME).getStart());}));
			}
		});

	}
	private void whyIsSheDamaged(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Why is she damaged?");
		addNPC(KLARENSE, HeadE.CALM_TALK, "Oh, there was not particular accident. It's just years of wear and tear. The Lady Lumbridge is an old crandorian " +
				"fishing ship – the last one of her kind, as far as I know. That kind of ship was always mightily manoeuvrable, but not too tough.");
		addNPC(KLARENSE, HeadE.CALM_TALK, "She happened to be somewhere else when Crandor was destroyed, and she's had several owners since then. Not all of them " +
				"looked after her too well, but once she's patched up, she'll be good as new!");
		addNext(()->{p.startConversation(new KlarenseDragonSlayerD(p, MAIN_OPTIONS, WHY_IS_SHE_DAMAGED).getStart());});
	}
	private void idLikeToBuyHer(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "I'd like to buy her.");
		addNPC(KLARENSE, HeadE.CALM_TALK, "Of course! I'm sure the work needed to do on it wouldn't be too expensive. How does 2,000 gold sound? I'll " +
				"even throw in my cabin boy, Jenkins, for free! He'll swab the decks and splice the mainsails for you!");
		if(p.getInventory().hasCoins(2000))
			addOptions("Buy Lady Lumbridge?", new Options() {
				@Override
				public void create() {
					option("Yep, sounds good.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Yep, sounds good.")
							.addNPC(KLARENSE, HeadE.CALM_TALK, "Okey dokey, she's all yours!", () -> {
								p.getInventory().removeCoins(2000);
								p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(OWNS_BOAT_ATTR, true);
							}));
					option("I'm not paying that much for a broken boat!", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not paying that much for a broken boat!")
							.addNPC(KLARENSE, HeadE.CALM_TALK, "That fair enough, I suppose."));
				}
			});
		else {
			addPlayer(HeadE.HAPPY_TALKING, "Darn, I don't have it.");
			addNPC(KLARENSE, HeadE.CALM_TALK, "That's too bad...");
			addPlayer(HeadE.SAD, "It is...");
		}
	}

}
