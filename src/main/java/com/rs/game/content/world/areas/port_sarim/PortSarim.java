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
package com.rs.game.content.world.areas.port_sarim;

import com.rs.game.content.Skillcapes;
import com.rs.game.content.quests.heroesquest.HeroesQuest;
import com.rs.game.content.quests.knightssword.KnightsSword;
import com.rs.game.content.quests.knightssword.ThurgoKnightsSwordD;
import com.rs.game.content.world.unorganized_dialogue.skillmasters.GenericSkillcapeOwnerD;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemAddedToInventoryHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PortSarim {

	public static PlayerStepHandler musicRustyAnchorInn = new PlayerStepHandler(new Tile[] { Tile.of(3053, 3255, 0), Tile.of(3053, 3254, 0), Tile.of(3053, 3259, 0), Tile.of(3053, 3260, 0) }, e -> {
		if(e.getTile().getY() == 3255 && e.getStep().getDir() == Direction.NORTH) {
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(719, true);
			return;
		}
		if(e.getTile().getY() == 3259 && e.getStep().getDir() == Direction.SOUTH) {
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(719, true);
			return;
		}
		if((e.getTile().getY() == 3260 || e.getTile().getY() == 3254) && e.getPlayer().getMusicsManager().isPlaying(719))
			e.getPlayer().getMusicsManager().nextAmbientSong();
	});

	public static ItemOnNPCHandler handleThurgoItem = new ItemOnNPCHandler(new Object[] { 604 }, e -> {
		if (e.getItem().getId() == 24303 || e.getItem().getId() == 24339)
			e.getPlayer().sendOptionDialogue("Would you like Thurgo to " + (e.getItem().getId() == 24339 ? "repair" : "forge") + " your Royal Crossbow?", ops -> {
				ops.add("Yes, please (Requires a stabilizer, frame, sight, and spring)", () -> {
					if (e.getPlayer().getInventory().containsItems(new Item(24340), new Item(24342), new Item(24344), new Item(24346))) {
						e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
						e.getPlayer().getInventory().deleteItem(24340, 1);
						e.getPlayer().getInventory().deleteItem(24342, 1);
						e.getPlayer().getInventory().deleteItem(24344, 1);
						e.getPlayer().getInventory().deleteItem(24346, 1);
						e.getPlayer().getInventory().addItem(e.getItem().getId() == 24339 ? 24338 : 24337, 1);
						e.getPlayer().sendMessage("Thurgo " + (e.getItem().getId() == 24339 ? "repairs" : "forges") + " your Royal crossbow.");
					}
				});
				ops.add("No, thanks.");
			});
	});

	public static ItemAddedToInventoryHandler handlePortSarimApron = new ItemAddedToInventoryHandler(new Object[] { 7957 }, e -> e.getItem().setId(1005));

	public static NPCClickHandler handleThurgo = new NPCClickHandler(new Object[] { 604 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						if (player.getQuestManager().getStage(Quest.KNIGHTS_SWORD) >= KnightsSword.FIND_DWARF)
							option("About Knight's Sword.", new Dialogue()
									.addNext(() -> {
										e.getPlayer().startConversation(new ThurgoKnightsSwordD(e.getPlayer()).getStart());
									}));
						option("About that skill cape...", new Dialogue()
								.addNext(() -> {
									player.startConversation(new GenericSkillcapeOwnerD(player, 604, Skillcapes.Smithing));
								})
						);
					}
				});
				create();
			}
		});
	});

	public static NPCClickHandler handleGerrantFishingShop = new NPCClickHandler(new Object[] { 558 }, e -> {
		if (e.getOption().equalsIgnoreCase("Trade"))
			ShopsHandler.openShop(e.getPlayer(), "gerrants_fishy_business");
		if (e.getOption().equalsIgnoreCase("Talk-to"))
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				int NPC = e.getNPCId();

				{
					addNPC(NPC, HeadE.CALM_TALK, "Welcome! You can buy fishing equipment at my store. We'll also buy anything you catch off you.");
					addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							if (e.getPlayer().getQuestManager().getStage(Quest.HEROES_QUEST) == HeroesQuest.GET_ITEMS
									&& !e.getPlayer().getInventory().containsItem(1581, 1))//blamish snail slime
								option("I want to find out how to catch a lava eel.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I want to find out how to catch a lava eel.")
										.addNPC(NPC, HeadE.CALM_TALK, "Lava eels, eh? That's a tricky one, that is. You'll need a lava-proof fishing rod. The" +
												" method for making this would be to take an ordinary fishing rod, and then cover it with fire-proof blamish oil.")
										.addPlayer(HeadE.HAPPY_TALKING, "Do you have one of those snail oils?")
										.addNPC(NPC, HeadE.CALM_TALK, "I do, yes...")
										.addPlayer(HeadE.HAPPY_TALKING, "...")
										.addPlayer(HeadE.HAPPY_TALKING, "Can I have one?")
										.addNPC(NPC, HeadE.CALM_TALK, "Sure!")
										.addSimple("He looks around the shop...")
										.addNPC(NPC, HeadE.CALM_TALK, "Got it!")
										.addItem(1581, "He gives you some blamish snail oil...", () -> {
											e.getPlayer().getInventory().addItem(1581, 1, true);
										})
										.addNPC(NPC, HeadE.CALM_TALK, "Don't forget to add this to unfinished Harralander")
										.addPlayer(HeadE.HAPPY_TALKING, "So, where can I fish lava eels?")
										.addNPC(NPC, HeadE.CALM_TALK, "Taverley dungeon or the lava maze in the Wilderness.")
								);
							option("Let's see what you've got then.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Let's see what you've got then.")
									.addNext(() -> {
										ShopsHandler.openShop(e.getPlayer(), "gerrants_fishy_business");
									}));
							option("Sorry, I'm not interested.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Sorry, I'm not interested.")
							);
						}
					});
					create();
				}
			});
	});

	public static ObjectClickHandler handleEnterIceDungeon = new ObjectClickHandler(new Object[]{9472}, e -> {
		e.getPlayer().setNextTile(Tile.of(3007, 9550, 0));
	});

	public static ObjectClickHandler handleExitIceDungeon = new ObjectClickHandler(new Object[]{32015}, new Tile[] { Tile.of(3008, 9550, 0) }, e -> {
		e.getPlayer().setNextTile(Tile.of(3008, 3149, 0));
	});

	public static ObjectClickHandler handleEnterWyvern = new ObjectClickHandler(new Object[]{33173}, e -> {
		e.getPlayer().setNextTile(Tile.of(3056, 9555, 0));
	});

	public static ObjectClickHandler handleExitWyvern = new ObjectClickHandler(new Object[]{33174}, e -> {
		e.getPlayer().setNextTile(Tile.of(3056, 9562, 0));
	});

	public static ObjectClickHandler handleEnterLadyLumbridgeBoat = new ObjectClickHandler(new Object[]{2594, 2593}, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if (p.getY() > obj.getY())
			e.getPlayer().setNextTile(Tile.of(3047, 3204, 0));
		if (p.getY() < obj.getY())
			e.getPlayer().setNextTile(Tile.of(3047, 3207, 1));
	});

	public static ObjectClickHandler handleEnterLadyLumbridgeBoatUpperLadder = new ObjectClickHandler(new Object[]{2590}, e -> {
		e.getPlayer().useStairs(828, Tile.of(e.getObject().getX() - 1, e.getObject().getY(), e.getObject().getPlane() - 1), 1, 2);
	});

	public static ObjectClickHandler handleLowerBoatLadder = new ObjectClickHandler(new Object[]{272}, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if (obj.getRotation() == 0)
			p.useStairs(828, Tile.of(obj.getX(), obj.getY() - 1, obj.getPlane() + 1), 1, 2);
		if (obj.getRotation() == 1)
			p.useStairs(828, Tile.of(obj.getX() - 1, obj.getY(), obj.getPlane() + 1), 1, 2);
		if (obj.getRotation() == 2)
			p.useStairs(828, Tile.of(obj.getX(), obj.getY() + 1, obj.getPlane() + 1), 1, 2);
		if (obj.getRotation() == 3)
			p.useStairs(828, Tile.of(obj.getX() + 1, obj.getY(), obj.getPlane() + 1), 1, 2);
	});
}
