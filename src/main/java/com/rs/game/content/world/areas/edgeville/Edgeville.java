// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.world.areas.edgeville;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.dragonslayer.OziachDragonSlayerD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Edgeville  {

	public static LoginHandler setSafetyStrongholdPosterPulled = new LoginHandler(e -> e.getPlayer().getVars().setVarBit(6278, 1));

	public static ObjectClickHandler handleWildernessDitch = new ObjectClickHandler(new Object[] { 29319, 29320 }, e -> {
		if (e.getPlayer().getY() <= 9917) {
			e.getPlayer().getControllerManager().startController(new WildernessController());
			e.getPlayer().handleOneWayDoor(e.getObject());
		} else
			e.getPlayer().handleOneWayDoor(e.getObject());
	});

    public static ObjectClickHandler handleBlackKnightWall = new ObjectClickHandler(new Object[] { 2341 }, e -> {
    	Doors.handleDoor(e.getPlayer(), e.getObject(), -1);
    });

	public static ObjectClickHandler handleJailEntrance = new ObjectClickHandler(new Object[] { 29603 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(3082, 4229, 0), 0, 1);
	});

	public static ObjectClickHandler handleJailExit = new ObjectClickHandler(new Object[] { 29602 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(3074, 3456, 0), 0, 1);
	});

	public static ObjectClickHandler handlePosterEntrance = new ObjectClickHandler(new Object[] { 29735 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(3140, 4230, 2), 0, 1);
	});

	public static ObjectClickHandler handlePosterExit = new ObjectClickHandler(new Object[] { 29623 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(3077, 4235, 0), 0, 1);
	});

	public static ObjectClickHandler handleJailDoors = new ObjectClickHandler(new Object[] { 29624 }, e -> {
		if (e.getObject().getRotation() == 0) {
			if (e.getPlayer().getPlane() == 0)
				e.getPlayer().useStairs(-1, e.getPlayer().transform(0, 3, 2), 0, 1);
			else
				e.getPlayer().useStairs(-1, e.getPlayer().transform(0, -3, -2), 0, 1);
		} else if (e.getPlayer().getPlane() == 0)
			e.getPlayer().useStairs(-1, e.getPlayer().transform(-1, 2, 1), 0, 1);
		else
			e.getPlayer().useStairs(-1, e.getPlayer().transform(1, -2, -1), 0, 1);
	});

	public static ObjectClickHandler handleEdgevilleMonkeybars = new ObjectClickHandler(new Object[] { 29375 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 15))
			return;
		Agility.crossMonkeybars(e.getPlayer(), e.getObject().getTile(), e.getObject().getTile().transform(0, e.getPlayer().getY() > 9967 ? -5 : 5, 0), 20.0);
	});

	public static ObjectClickHandler handleMonastaryLadders = new ObjectClickHandler(new Object[] { 2641 }, e -> {
		Player p = e.getPlayer();
		if(p.getSkills().getLevel(Constants.PRAYER) >= 31)
			p.useLadder(Tile.of(p.getX(), p.getY(), p.getPlane()+1));
		else
			p.startConversation(new Conversation(p) {
				int NPC = 801;
				{
					addNPC(NPC, HeadE.FRUSTRATED, "Hey! You are not part of the monastary!");
					addPlayer(HeadE.HAPPY_TALKING, "Oh.");
					addSimple("You need 31 prayer to enter the inner monastary.");
					create();
				}
			});
	});

	public static NPCClickHandler handleOziachDialogue = new NPCClickHandler(new Object[] { 747 }, e -> {
		if (e.getOption().equalsIgnoreCase("trade"))
			if(e.getPlayer().isQuestComplete(Quest.DRAGON_SLAYER))
				ShopsHandler.openShop(e.getPlayer(), "oziach");
			else
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.FRUSTRATED, "I don't have anything to sell...");
						create();
					}
				});
		if(e.getOption().equalsIgnoreCase("Talk-to"))
			if(e.getPlayer().isQuestComplete(Quest.DRAGON_SLAYER))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CALM, "Good day to you.");
						addNPC(747, HeadE.HAPPY_TALKING, "Aye, 'tis a fair day, mighty dragon-slaying friend.");
						if (player.getInventory().containsItem(11286))
							addNPC(747, HeadE.AMAZED, "Ye've got... Ye've found a draconic visage! Could I look at it?");

						addOptions(new Options() {
							@Override
							public void create() {
								if (player.getInventory().containsItem(11286))
									option("Here you go", new Dialogue().addPlayer(HeadE.CALM, "Here you go.")
											.addNPC(747, HeadE.AMAZED_MILD, "Amazin'! Ye can almost feel it pulsing with draconic power!")
											.addNPC(747, HeadE.AMAZED_MILD, "Now, if ye want me to, I could attach this to yer anti-dragonbreath shield and make something pretty special.")
											.addNPC(747, HeadE.AMAZED_MILD, "The shield won't be easy to wield though; ye'll need level 75 Defence.")
											.addNPC(747, HeadE.AMAZED_MILD, "I'll Charge 1,250,000 coins to construct it. What d'ye say?")
											.addOption("Select an option", "Yes, please!", "No, thanks.", "That's a bit expensive!")
											.addNPC(747, HeadE.CALM, "Great lets take a look.")
											.addNext(
													(player.getInventory().containsItem(11286) && player.getInventory().hasCoins(1250000) && player.getInventory().containsItem(1540) ?
															new Dialogue().addItem(11283, "Oziach skillfully forges the shield and visage into a new shield.", () -> {
																player.getInventory().deleteItem(11286, 1);
																player.getInventory().removeCoins(1250000);
																player.getInventory().deleteItem(1540, 1);
																player.getInventory().addItem(11283, 1);
															}) : new Dialogue().addNPC(747, HeadE.CALM_TALK, "Ye seem to be missing some stuff. Come see me when ye have an anti-dragon shield and my payment."))
													));
								option("Can I buy a rune platebody now, please?", new Dialogue().addPlayer(HeadE.CALM, "Can I buy a rune platebody now, please?").addNext(() -> {
									ShopsHandler.openShop(player, "oziach");
								}));
								if (!player.getInventory().containsItem(11286)) {
									option("I'm not your friend.", new Dialogue().addPlayer(HeadE.CALM, "I'm not your friend.").addNPC(747, HeadE.FRUSTRATED, "I'm surprised if you're anyone's friend with those kind of manners."));
									option("Yes, it's a very nice day.", new Dialogue().addPlayer(HeadE.CALM, "Yes, it's a very nice day.").addNPC(747, HeadE.HAPPY_TALKING, "Aye, may the gods walk by yer side. Now leave me alone."));
								}
							}
						});
						create();
					}
				});
			else
				e.getPlayer().startConversation(new OziachDragonSlayerD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleAbbotLangleyDialogue = new NPCClickHandler(new Object[] { 801 }, e -> {
		Player p = e.getPlayer();
		int NPC = e.getNPCId();
		p.startConversation(new Conversation(p) {
			{
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Can you heal me? I'm injured.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Can you heal me? I'm injured.")
								.addNPC(NPC, HeadE.CALM_TALK, "Ok.")
								.addSimple("Abbot Langley places his hands on your head. You feel a little better.", () ->{
									p.heal(p.getMaxHitpoints());
								})
								);
						option("Isn't this place built a bit out of the way?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Isn't this place built a bit out of the way?")
								.addNPC(NPC, HeadE.CALM_TALK, "We like it that way actually! We get disturbed less. We still get rather a large amount " +
										"of travellers looking for sanctuary and healing here as it is!")
								);
						if(p.getSkills().getLevel(Constants.PRAYER)<31)
							option("How do I get further into the monastery?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "How do I get further into the monastery?")
									.addNPC(NPC, HeadE.CALM_TALK, "I'm sorry but only members of our order are allowed in the second level of the monastery.")
									.addOptions("Choose an option:", new Options() {
										@Override
										public void create() {
											option("Well can I join your order?", new Dialogue()
													.addPlayer(HeadE.HAPPY_TALKING, "Well can I join your order?")
													.addNPC(NPC, HeadE.CALM_TALK, "No. I am sorry, but I feel you are not devout enough.")
													.addSimple("You need 31 prayer to enter the inner monastary.")
													);
											option("Oh, sorry.", new Dialogue()
													.addPlayer(HeadE.HAPPY_TALKING, "Oh, sorry.")
													);
										}
									})
									);
					}
				});
				create();
			}
		});
	});
}
