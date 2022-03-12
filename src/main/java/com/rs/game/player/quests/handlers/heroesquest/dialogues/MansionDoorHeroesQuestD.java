package com.rs.game.player.quests.handlers.heroesquest.dialogues;

import static com.rs.game.player.content.world.doors.Doors.handleDoor;
import static com.rs.game.player.quests.handlers.heroesquest.HeroesQuest.GET_ITEMS;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;

@PluginEventHandler
public class MansionDoorHeroesQuestD extends Conversation {
	private static final int NPC = 788;

	public MansionDoorHeroesQuestD(Player p, ObjectClickEvent e) {
		super(p);
		Dialogue intro = new Dialogue().addNPC(NPC, HeadE.FRUSTRATED, "Oi! Where do you think you're going pal?");
		Dialogue reportDuty = new Dialogue().addPlayer(HeadE.CALM_TALK, "Hi, I'm Hartigen. I've come to work here.")
				.addNPC(NPC, HeadE.CALM_TALK, "I assume you have your I.D. papers then?");
		switch (p.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("mansion_open_phoenix")) {
					handleDoor(p, e.getObject());
					return;
				}
				if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("mansion_open_black_arm")) {
					if (isWearingBlackArmour(p))
						handleDoor(p, e.getObject());
					return;
				}
				if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("black_arm_trick") && isWearingBlackArmour(p))
					if (p.getInventory().containsItem(1584, 1))
						addNext(intro.addNext(() -> {
							p.startConversation(reportDuty
									.addSimple("You show the ID papers...", () -> {
										p.getInventory().removeItems(new Item(1584, 1));
										p.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("mansion_open_black_arm", true);
									})
									.addNPC(NPC, HeadE.CALM_TALK, "You'd better come in then."));
						}));
					else
						addNext(intro.addNext(() -> {
									p.startConversation(reportDuty
											.addPlayer(HeadE.SECRETIVE, "Uh... Yeah. About that...I must have left them in my other suit of armour.")
									);
								})
						);
				else if (p.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("phoenix_trick"))
					if (p.getInventory().containsItem(995, 1000))
						addNext(intro
								.addPlayer(HeadE.HAPPY_TALKING, "Can I go in there?")
								.addNPC(NPC, HeadE.CALM_TALK, "No in there is private.")
								.addPlayer(HeadE.HAPPY_TALKING, "Are you open to a bribe?")
								.addNPC(NPC, HeadE.CALM_TALK, "On these wages, absolutely It'll cost you 1,000 coin for me to urn a blind eye.")
								.addPlayer(HeadE.HAPPY_TALKING, "Sounds good")
								.addSimple("You hand him the coins and he winks at you.", () -> {
									p.getInventory().removeItems(new Item(995, 1000));
									p.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("mansion_open_phoenix", true);
								})
						);
					else
						addPlayer(HeadE.HAPPY_TALKING, "I'll need some money to bribe him...");
			}
			default -> {
				addNext(intro.addPlayer(HeadE.CALM_TALK, "Nowhere special."));
			}
		}
	}

	public static boolean isWearingBlackArmour(Player p) {
		if (p.getEquipment().getChestId() == 1125 && p.getEquipment().getLegsId() == 1077 && p.getEquipment().getHatId() == 1165)
			return true;
		p.sendMessage("You need to be wearing black armour as a disguise...");
		return false;
	}
}
