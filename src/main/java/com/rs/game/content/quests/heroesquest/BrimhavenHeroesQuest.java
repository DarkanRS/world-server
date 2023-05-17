package com.rs.game.content.quests.heroesquest;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.heroesquest.dialogues.AlfonsoTheWaiterHeroesQuestD;
import com.rs.game.content.quests.heroesquest.dialogues.CharlieTheCookHeroesQuestD;
import com.rs.game.content.quests.heroesquest.dialogues.MansionDoorHeroesQuestD;
import com.rs.game.content.quests.heroesquest.dialogues.TrobertHeroesQuestD;
import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

import static com.rs.game.content.world.doors.Doors.handleDoor;

@PluginEventHandler
public class BrimhavenHeroesQuest {
	public static ObjectClickHandler handleMansionDoor = new ObjectClickHandler(new Object[]{2627}, e -> {
		if (e.getPlayer().getY() > e.getObject().getY())
			handleDoor(e.getPlayer(), e.getObject());
		else
			e.getPlayer().startConversation(new MansionDoorHeroesQuestD(e.getPlayer(), e));
	});

	public static NPCClickHandler garvTalk = new NPCClickHandler(new Object[] { 788 }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to"))
			e.getPlayer().startConversation(new Dialogue()
					.addPlayer(HeadE.HAPPY_TALKING, "Hello.")
					.addNPC(788, HeadE.FRUSTRATED, "What yer doing 'round here!? Leave, before I really get angry.")
			);
	});


	public static ObjectClickHandler handleCandleStickChest = new ObjectClickHandler(new Object[]{37044}, e -> {
		if (e.getOption().equalsIgnoreCase("Search"))
			if (e.getPlayer().getQuestManager().getStage(Quest.HEROES_QUEST) == HeroesQuest.GET_ITEMS)
				if (e.getPlayer().getInventory().containsItem(1577, 1))
					e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.SECRETIVE, "It's empty..."));
				else
					e.getPlayer().getInventory().addItem(1577, 1);
	});

	public static ObjectClickHandler handleBlackArmHideoutDoor = new ObjectClickHandler(new Object[]{2626}, e -> {
		if (e.getPlayer().getX() >= e.getObject().getX()) {
			handleDoor(e.getPlayer(), e.getObject());
			return;
		}
		if (e.getPlayer().isQuestComplete(Quest.HEROES_QUEST) || e.getPlayer().getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("black_arm_hideout_open")) {
			handleDoor(e.getPlayer(), e.getObject());
			return;
		}
		if (e.getPlayer().getQuestManager().getStage(Quest.HEROES_QUEST) == HeroesQuest.GET_ITEMS && ShieldOfArrav.isBlackArmGang(e.getPlayer())) {
			e.getPlayer().startConversation(new Dialogue().addNPC(789, HeadE.SECRETIVE, "Yes, what do you want?")
					.addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("Rabbit's Foot.", new Dialogue().addNPC(789, HeadE.CALM_TALK, "No idea what you are talking about, go away!"));
							option("Four leaved Clover.", new Dialogue()
									.addPlayer(HeadE.CALM_TALK, "Four leaved clover")
									.addNPC(789, HeadE.HAPPY_TALKING, "Oh, you're one of the gang are you? Ok, hold up a second, I'll just let you in" +
											" through here.")
									.addSimple("You hear the door being unbarred from inside.", () -> {
										e.getPlayer().getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("black_arm_hideout_open", true);
									}));
							option("Lucky horseshoe", new Dialogue().addNPC(789, HeadE.CALM_TALK, "No idea what you are talking about, go away!"));
							option("Black cat", new Dialogue().addNPC(789, HeadE.CALM_TALK, "No idea what you are talking about, go away!"));
						}
					}));
		} else
			e.getPlayer().sendMessage("The door won't open");
	});

	public static NPCClickHandler gruborTalk = new NPCClickHandler(new Object[] { 789 }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to"))
			e.getPlayer().startConversation(new Dialogue()
					.addPlayer(HeadE.HAPPY_TALKING, "Hello.")
					.addNPC(789, HeadE.CALM_TALK, "Hi. I'm a little busy right now. Maybe you should speak to Trobert. He runs things around here.")
			);
	});

	public static NPCClickHandler trobertTalk = new NPCClickHandler(new Object[] { 1884 }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to"))
			e.getPlayer().startConversation(new TrobertHeroesQuestD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleAlfonseWaiter = new NPCClickHandler(new Object[] { 793 }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to")) {
			e.getPlayer().startConversation(new AlfonsoTheWaiterHeroesQuestD(e.getPlayer()).getStart());
		} else if (e.getOption().equalsIgnoreCase("trade"))
			ShopsHandler.openShop(e.getPlayer(), "alfonso_waiter_shop");
	});

	public static NPCClickHandler handleCharlieCook = new NPCClickHandler(new Object[] { 794 }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to"))
			e.getPlayer().startConversation(new CharlieTheCookHeroesQuestD(e.getPlayer()).getStart());
	});

	public static ObjectClickHandler handleCharliesWall = new ObjectClickHandler(new Object[]{2629}, e -> e.getPlayer().sendMessage("It is just a wall..."));
}
