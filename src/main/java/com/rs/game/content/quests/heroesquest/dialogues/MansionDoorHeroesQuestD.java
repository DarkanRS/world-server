package com.rs.game.content.quests.heroesquest.dialogues;

import static com.rs.game.content.quests.heroesquest.HeroesQuest.GET_ITEMS;
import static com.rs.game.content.world.doors.Doors.handleDoor;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;

@PluginEventHandler
public class MansionDoorHeroesQuestD extends Conversation {
	private static final int NPC = 788;

	public MansionDoorHeroesQuestD(Player player, ObjectClickEvent e) {
		super(player);
		Dialogue intro = new Dialogue().addNPC(NPC, HeadE.FRUSTRATED, "Oi! Where do you think you're going pal?");
		Dialogue reportDuty = new Dialogue().addPlayer(HeadE.CALM_TALK, "Hi, I'm Hartigen. I've come to work here.")
				.addNPC(NPC, HeadE.CALM_TALK, "I assume you have your I.D. papers then?");
		switch (player.getQuestManager().getStage(Quest.HEROES_QUEST)) {
			case GET_ITEMS -> {
				if (player.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("mansion_open_phoenix")) {
					handleDoor(player, e.getObject());
					return;
				}
				if (player.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("mansion_open_black_arm")) {
					if (isWearingBlackArmour())
						handleDoor(player, e.getObject());
					return;
				}
				if (player.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("black_arm_trick") && isWearingBlackArmour())
					if (player.getInventory().containsItem(1584, 1))
						addNext(intro.addNext(() -> {
							player.startConversation(reportDuty
									.addSimple("You show the ID papers...", () -> {
										player.getInventory().removeItems(new Item(1584, 1));
										player.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("mansion_open_black_arm", true);
									})
									.addNPC(NPC, HeadE.CALM_TALK, "You'd better come in then."));
						}));
					else
						addNext(intro.addNext(() -> {
									player.startConversation(reportDuty
											.addPlayer(HeadE.SECRETIVE, "Uh... Yeah. About that...I must have left them in my other suit of armour.")
									);
								})
						);
				else if (player.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("phoenix_trick"))
					if (player.getInventory().hasCoins(1000))
						addNext(intro
								.addPlayer(HeadE.HAPPY_TALKING, "Can I go in there?")
								.addNPC(NPC, HeadE.CALM_TALK, "No in there is private.")
								.addPlayer(HeadE.HAPPY_TALKING, "Are you open to a bribe?")
								.addNPC(NPC, HeadE.CALM_TALK, "On these wages, absolutely It'll cost you 1,000 coin for me to urn a blind eye.")
								.addPlayer(HeadE.HAPPY_TALKING, "Sounds good")
								.addSimple("You hand him the coins and he winks at you.", () -> {
									player.getInventory().removeCoins(1000);
									player.getQuestManager().getAttribs(Quest.HEROES_QUEST).setB("mansion_open_phoenix", true);
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

	public boolean isWearingBlackArmour() {
		if (player.getEquipment().getChestId() == 1125 && player.getEquipment().getLegsId() == 1077 && player.getEquipment().getHatId() == 1165)
			return true;
		player.sendMessage("You need to be wearing black armour as a disguise...");
		return false;
	}
}
