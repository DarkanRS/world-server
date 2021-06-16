package com.rs.game.player.content.world.regions;

import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.pathing.Direction;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.achievements.AchievementDef;
import com.rs.game.player.content.achievements.AchievementDef.Area;
import com.rs.game.player.content.achievements.AchievementDef.Difficulty;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.combat.XPType;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.WorldUtil;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Varrock {
	
	public static NPCClickHandler handleBlueMoonBartender = new NPCClickHandler(false, new Object[] { 733 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().setRouteEvent(new RouteEvent(e.getNPC(), () -> {
				if (!WorldUtil.isInRange(e.getPlayer(), e.getNPC(), 2))
					return;
				if (e.getPlayer().getTreasureTrailsManager().useNPC(e.getNPC()))
					return;
			}, true));
		}
	};
	
	public static ObjectClickHandler handleDummies = new ObjectClickHandler(new Object[] { 23921 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSkills().getLevelForXp(Constants.ATTACK) >= 8) {
				e.getPlayer().sendMessage("There is nothing more you can learn from hitting a dummy.");
				return;
			}
			XPType type = e.getPlayer().getCombatDefinitions().getAttackStyle().getXpType();
			if (type != XPType.ACCURATE && type != XPType.AGGRESSIVE && type != XPType.CONTROLLED && type != XPType.DEFENSIVE) {
				e.getPlayer().sendMessage("You can't hit a dummy with that attack style.");
				return;
			}
			e.getPlayer().setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(e.getPlayer().getEquipment().getWeaponId(), e.getPlayer().getCombatDefinitions().getAttackStyle())));
			e.getPlayer().lock(3);
			World.sendObjectAnimation(e.getPlayer(), e.getObject(), new Animation(6482));
			e.getPlayer().getSkills().addXp(Constants.ATTACK, 5);
		}
	};
	
	public static NPCClickHandler handleReldo = new NPCClickHandler(647) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.VARROCK_ARMOR).getStart());
						}
					});
				}
			});
		}
	};
	
	public static NPCClickHandler handleRatBurgiss = new NPCClickHandler(5833) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.VARROCK_ARMOR).getStart());
						}
					});
				}
			});
		}
	};
	
	public static NPCClickHandler handleNaff = new NPCClickHandler(359) {
		@Override
		public void handle(NPCClickEvent e) {
			int max = 8;
			if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.ELITE, false))
				max = 80;
			else if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.HARD, false))
				max = 64;
			else if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.MEDIUM, false))
				max = 32;
			else if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.EASY, false))
				max = 16;
			int amountLeft = max - e.getPlayer().getDailyI("naffStavesBought");
			if (amountLeft <= 0) {
				e.getPlayer().sendMessage("Naff has no staves left today.");
				return;
			}
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You don't have enough inventory space to buy any staves.");
				return;
			}
			e.getPlayer().sendInputInteger("How many battlestaves would you like to buy? (" + amountLeft +" available)", amount -> {
				int coinsOnPlayer = e.getPlayer().getInventory().getAmountOf(995);
				int maxBuyable = coinsOnPlayer / 7000;
				if (amount > maxBuyable)
					amount = maxBuyable;
				if (amount > amountLeft)
					amount = amountLeft;
				if (amount <= 0) {
					e.getPlayer().sendMessage("You don't have enough money to buy any staves right now.");
					return;
				}
				final int finalAmount = amount;
				final int cost = 7000 * amount;
				e.getPlayer().sendOptionDialogue("Buy " + amount + " battlestaves for " + Utils.formatNumber(cost) + " coins?", new String[] { "Yes", "No thanks." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 2)
							return;
						if (!e.getPlayer().getInventory().containsItem(995, cost)) {
							e.getPlayer().sendMessage("You don't have enough money for that.");
							return;
						}
						e.getPlayer().getInventory().deleteItem(995, cost);
						e.getPlayer().getInventory().addItemDrop(1392, finalAmount);
						e.getPlayer().setDailyI("naffStavesBought", e.getPlayer().getDailyI("naffStavesBought") + finalAmount);
					}
				});
			});
		}
	};
	
	public static NPCClickHandler handleDealga = new NPCClickHandler(11475) {
		@Override
		public void handle(NPCClickEvent e) {
			ShopsHandler.openShop(e.getPlayer(), "dealgas_scimitar_emporium");
		}
	};
	
	public static ObjectClickHandler handleKeldagrimTrapdoor = new ObjectClickHandler(new Object[] { 28094 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2911, 10176, 0));
		}
	};
	
	public static ObjectClickHandler handleRiverLumSteppingStones = new ObjectClickHandler(new Object[] { 9315 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 31))
				return;
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getObject().getRotation() == 1 ? -5 : 5, 0, 0), 4);
		}
	};
	
	public static ObjectClickHandler handleGrandExchangeShortcut = new ObjectClickHandler(new Object[] { 9311, 9312 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 21))
				return;
			WorldTasksManager.schedule(new WorldTask() {
				int ticks = 0;

				@Override
				public void run() {
					boolean withinGE = e.getObjectId() == 9312;
					WorldTile tile = withinGE ? new WorldTile(3139, 3516, 0) : new WorldTile(3143, 3514, 0);
					e.getPlayer().lock();
					ticks++;
					if (ticks == 1) {
						e.getPlayer().setNextAnimation(new Animation(2589));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject(), 1, withinGE ? Direction.WEST : Direction.EAST));
					} else if (ticks == 3) {
						e.getPlayer().setNextWorldTile(new WorldTile(3141, 3515, 0));
						e.getPlayer().setNextAnimation(new Animation(2590));
					} else if (ticks == 5) {
						e.getPlayer().setNextAnimation(new Animation(2591));
						e.getPlayer().setNextWorldTile(tile);
					} else if (ticks == 6) {
						e.getPlayer().setNextWorldTile(new WorldTile(tile.getX() + (withinGE ? -1 : 1), tile.getY(), tile.getPlane()));
						e.getPlayer().unlock();
						stop();
					}
				}
			}, 0, 0);
		}
	};
}
