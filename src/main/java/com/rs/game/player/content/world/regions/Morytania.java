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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world.regions;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.dialogue.statements.ItemStatement;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.quests.Quest;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Morytania  {

	public static enum BloomResource {

		FUNGI_ON_LOG(3509, 1, 2970),
		BUDDING_BRANCH(3511, 2, 2972),
		GOLDEN_PEAR_BUSH(3513, 3, 2974);

		private final int objectId, product, druidPouch;

		private BloomResource(int objectId, int druidPouch, int product) {
			this.objectId = objectId;
			this.druidPouch = druidPouch;
			this.product = product;
		}

		public int getObject() {
			return objectId;
		}

		public int getDruidPouch() {
			return druidPouch;
		}

		public int getProduct() {
			return product;
		}

		public static BloomResource forObject(int id) {
			for (BloomResource harvest : BloomResource.values())
				if (harvest.objectId == id)
					return harvest;
			return null;
		}
	}

	public static NPCClickHandler handleHiylikMyna = new NPCClickHandler(1514) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
						}
					});
				}
			});
		}
	};

	public static NPCClickHandler handleRobin = new NPCClickHandler(1694) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
						}
					});
				}
			});
		}
	};

	public static NPCClickHandler handleStrangeOldManBarrows = new NPCClickHandler(2024) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
						}
					});
				}
			});
		}
	};

	public static NPCClickHandler handleOldManRal = new NPCClickHandler(4708) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
						}
					});
				}
			});
		}
	};

	public static ObjectClickHandler handleSlayerTowerChains = new ObjectClickHandler(new Object[] { 9319, 9320 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), e.getObject().getX() < 3447 ? 61 : 71))
				return;
			e.getPlayer().useStairs(828, e.getPlayer().transform(0, 0, e.getObjectId() == 9319 ? 1 : -1), 1, 2);
		}
	};

	public static ObjectClickHandler handleLabEntrance = new ObjectClickHandler(new Object[] { 18049 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(3637, 9695, 0), 1, 1);
		}
	};

	public static ObjectClickHandler handleLabExit = new ObjectClickHandler(new Object[] { 18050 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(3643, 3306, 0), 1, 1);
		}
	};

	public static ObjectClickHandler handleBurghDeRottToMineFence = new ObjectClickHandler(new Object[] { 12776 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 1 : -1, 0, 0), 2923);
		}
	};

	public static ObjectClickHandler handleBrokenFence = new ObjectClickHandler(new Object[] { 18411 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -1 : 1, 0));
		}
	};

	public static ObjectClickHandler handleTempleTrapdoor = new ObjectClickHandler(new Object[] { 30572 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(3405, 9906, 0));
		}
	};

	public static ObjectClickHandler handleTempleTrapdoorCanifisSide = new ObjectClickHandler(new Object[] { 30574 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(3440, 9887, 0));
		}
	};

	public static ObjectClickHandler handleTempleLadder = new ObjectClickHandler(new Object[] { 30575 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(3405, 3506, 0));
		}
	};

	public static ObjectClickHandler handleHolyBarrier = new ObjectClickHandler(new Object[] { 3443 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(3423, 3484, 0));
		}
	};

	public static ObjectClickHandler handleSwampTrapdoorShortcut = new ObjectClickHandler(new Object[] { 5055, 5054 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(e.getObjectId() == 5055 ? new WorldTile(3477, 9845, 0) : new WorldTile(3495, 3466, 0));
		}
	};

	public static ObjectClickHandler handleMyrequeWall = new ObjectClickHandler(new Object[] { 5052 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleOneWayDoor(e.getPlayer(), e.getObject(), 1);
		}
	};

	public static ObjectClickHandler handleSwampWoodenDoors = new ObjectClickHandler(new Object[] { 30261, 30262, 30265 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getObjectId() == 30265 ? new WorldTile(3500, 9812, 0) : new WorldTile(3510, 3448, 0));
		}
	};

	public static ObjectClickHandler handleTreeBridgeShortcut = new ObjectClickHandler(new Object[] { 5005 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().isAt(3502, 3431))
				e.getPlayer().ladder(new WorldTile(3502, 3425, 0));
			else
				e.getPlayer().ladder(new WorldTile(3502, 3432, 0));
		}
	};

	public static ObjectClickHandler handleSwampBoatFromMorton = new ObjectClickHandler(new Object[] { 6969 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3500, 3380, 0));
		}
	};

	public static ObjectClickHandler handleSwampBoatToMorton = new ObjectClickHandler(new Object[] { 6970 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3521, 3284, 0));
		}
	};

	public static ObjectClickHandler handleGrottoTree = new ObjectClickHandler(new Object[] { 3517 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2)
				e.getPlayer().useLadder(new WorldTile(2272, 5334, e.getPlayer().getQuestManager().isComplete(Quest.NATURE_SPIRIT) ? 1 : 0));
		}
	};

	public static ObjectClickHandler handleExitNatureGrotto = new ObjectClickHandler(new Object[] { 3525, 3526 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(new WorldTile(3440, 3337, 0));
		}
	};

	public static ObjectClickHandler handleGrottoBridge = new ObjectClickHandler(new Object[] { 3522 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			int jumpTo = ((e.getObject().getY() <= 3329) ? e.getObject().getY()+2 : e.getObject().getY()-2);
			WorldTile endTile = new WorldTile(e.getObject().getX(), jumpTo, e.getObject().getPlane());
			e.getPlayer().lock();
			e.getPlayer().setNextFaceWorldTile(endTile);
			e.getPlayer().setNextAnimation(new Animation(769));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().unlockNextTick();
					e.getPlayer().setNextWorldTile(endTile);
					e.getPlayer().setNextAnimation(new Animation(-1));
				}
			}, 1);
		}
	};

	public static ItemClickHandler handleBonesackTele = new ItemClickHandler(new Object[] { 15215 }, new String[] { "Teleport" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Magic.sendTeleportSpell(e.getPlayer(), 12055, 12057, 2133, 2134, 0, 0, new WorldTile(3362, 3504, 0), 3, true, Magic.MAGIC_TELEPORT);
		}
	};

	private static void drakanTeleport(Player player, Item item, WorldTile location) {
		if (item.getMetaDataI("drakanCharges") <= 0) {
			player.sendMessage("The medallion seems unresponsive. It probably needs recharging.");
			return;
		}
		if (Magic.sendTeleportSpell(player, 8939, 8941, 1864, 1864, 0, 0, location, 2, true, Magic.MAGIC_TELEPORT))
			if (player.getX() >= 3398 && player.getX() <= 3841 && player.getY() >= 3161 && player.getY() <= 3586)
				player.sendMessage("Due to the short nature of your teleport, the medallion does not use a charge.");
			else
				player.sendMessage("Your medallion has " + item.decMetaDataI("drakanCharges") + " charges left.");
	}

	public static ItemClickHandler handleDrakansMedallion = new ItemClickHandler(new Object[] { 21576 }, new String[] { "Teleport", "Check-charges" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!Quest.BRANCHES_OF_DARKMEYER.meetsRequirements(e.getPlayer(), "to use the medallion."))
				return;
			if (e.getOption().equals("Teleport")) {
				if (e.getItem().getMetaDataI("drakanCharges") <= 0) {
					e.getPlayer().sendMessage("The medallion seems unresponsive. It probably needs recharging.");
					return;
				}
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addOptions("Where would you like to teleport?", new Options() {
							@Override
							public void create() {
								option("Barrows", () -> drakanTeleport(e.getPlayer(), e.getItem(), new WorldTile(3565, 3312, 0)));
								option("Burgh de Rott", () -> drakanTeleport(e.getPlayer(), e.getItem(), new WorldTile(3491, 3199, 0)));
								option("Meiyerditch", () -> drakanTeleport(e.getPlayer(), e.getItem(), new WorldTile(3626, 9618, 0)));
								option("Darkmeyer", () -> drakanTeleport(e.getPlayer(), e.getItem(), new WorldTile(3628, 3364, 00)));
								option("Meiyerditch Laboratories", () -> drakanTeleport(e.getPlayer(), e.getItem(), new WorldTile(3633, 9696, 0)));
							}
						});
					}
				});
			} else
				e.getPlayer().sendMessage("It looks like it has another " + e.getItem().getMetaDataI("drakanCharges", 0) + " charges left.");
		}
	};

	public static ObjectClickHandler handleMedallionRecharge = new ObjectClickHandler(new Object[] { 61094 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Item medal = e.getPlayer().getItemWithPlayer(21576);
			if (medal == null) {
				e.getPlayer().sendMessage("You don't have a medallion with you.");
				return;
			}
			medal.addMetaData("drakanCharges", 10);
			e.getPlayer().sendMessage("You dip the medallion into the blood. Eww. It feels heartily recharged, though.");
		}
	};

	public static ObjectClickHandler handleDrakanMedallionCave = new ObjectClickHandler(new Object[] { 61091, 59921 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Quest.BRANCHES_OF_DARKMEYER.meetsRequirements(e.getPlayer(), "to enter the cave."))
				return;
			e.getPlayer().fadeScreen(() -> {
				e.getPlayer().setNextWorldTile(e.getObjectId() == 59921 ? new WorldTile(2273, 5152, 0) : new WorldTile(3498, 3204, 0));
			});
		}
	};

	public static ObjectClickHandler handleClaimDrakanMedallion = new ObjectClickHandler(new Object[] { 61092 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Quest.BRANCHES_OF_DARKMEYER.meetsRequirements(e.getPlayer(), "to search this."))
				return;
			if (e.getPlayer().containsItem(21576)) {
				e.getPlayer().sendMessage("You have already retrieved the medallion from here.");
				return;
			}
			e.getPlayer().sendOptionDialogue("Are you sure you want to inspect the coffin?", new String[] { "Yes.", "No, I'm scared!" }, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (option == 1) {
						player.setNextAnimation(new Animation(14745));
						player.lock();
						e.getPlayer().applyHit(new Hit(null, 10, HitLook.TRUE_DAMAGE), 0, () -> {
							player.getInventory().addItem(new Item(21576, 1).addMetaData("drakanCharges", 10));
							e.getObject().setIdTemporary(61093, 20);
							player.unlock();
						});
					} else
						player.startConversation(new Conversation(e.getPlayer()).addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Veliaf did ask me to search everywhere."));
				}
			});
		}
	};

	public static ObjectClickHandler handlePickBloomed = new ObjectClickHandler(new Object[] { 3509, 3511, 3513 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			final BloomResource product = BloomResource.forObject(e.getObjectId());
			final String productName = ItemDefinitions.getDefs(product.getProduct()).getName().toLowerCase();

			e.getPlayer().setNextAnimation(new Animation(3659));
			e.getPlayer().lock(1);
			WorldTasks.delay(0, () -> {
				if (e.getPlayer().getInventory().addItemDrop(product.getProduct(), 1)) {
					e.getPlayer().sendMessage("You pick a " + productName + ".");
					e.getPlayer().incrementCount(productName + " bloomed", 1);
					World.spawnObject(new GameObject(product.getObject()-1, e.getObject().getType(), e.getObject().getRotation(), e.getObject()));

				}
			});
		}
	};

	public static ItemOnObjectHandler handleDipSickle = new ItemOnObjectHandler(new Object[] { 3521 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getItem().getId() == 2961) {
				e.getPlayer().lock(2);
				e.getPlayer().setNextAnimation(new Animation(9104));
				e.getPlayer().getInventory().deleteItem(e.getItem());
				e.getPlayer().getInventory().addItem(2963, 1);
				ItemStatement grottoBlessing = new ItemStatement(2963, "You dip the sickle into the grotto water and bless it.");
				grottoBlessing.send(e.getPlayer());
			}
		}
	};

	public static ItemClickHandler handleBloom = new ItemClickHandler(new Object[] { 2963 }, new String[] { "Bloom" }) {
		@Override
		public void handle(ItemClickEvent e) {
			int randomPrayerCost = Utils.random(10, 60);
			if (e.getPlayer().getPrayer().getPoints() >= 60) {
				e.getPlayer().getPrayer().drainPrayer(randomPrayerCost);
				e.getPlayer().lock(2);
				e.getPlayer().setNextAnimation(new Animation(9104));
				for (int x = -1;x <= 1;x++)
					for (int y = -1;y <= 1;y++) {
						if (x == 0 && y == 0)
							continue;
						World.sendSpotAnim(e.getPlayer(), new SpotAnim(263), e.getPlayer().transform(x, y));
						GameObject object = World.getObject(e.getPlayer().transform(x, y), ObjectType.SCENERY_INTERACT);
						if (object == null)
							continue;
						switch (object.getId()) {
						case 3512:
						case 3510:
						case 3508:
							object.setIdTemporary(object.getId()+1, Ticks.fromSeconds(30));
							break;
						}
					}
				return;
			}
			e.getPlayer().sendMessage("You need more prayer points to do this.");
		}
	};
}
