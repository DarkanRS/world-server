package com.rs.game.content.items;

import com.rs.engine.dialogue.Dialogue;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.Potions.Potion;
import com.rs.game.content.combat.CombatDefinitions.Spellbook;
import com.rs.game.content.minigames.barrows.BarrowsController;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.content.skills.cooking.Foods;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.utils.Ticks;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.function.Consumer;

@PluginEventHandler
public class StaffPotato {
	
	private enum Command {
		GOD("Toggle God", p -> {
			p.setNextAnimation(new Animation(361));
			p.setNextSpotAnim(new SpotAnim(122));
			p.getNSV().setB("godMode", !p.getNSV().getB("godMode"));
			p.sendMessage("GODMODE: " + p.getNSV().getB("godMode"));
		}),
		INF_PRAY("Toggle Infinite Prayer", p -> {
			p.setNextAnimation(new Animation(412));
			p.setNextSpotAnim(new SpotAnim(121));
			p.getNSV().setB("infPrayer", !p.getNSV().getB("infPrayer"));
			p.sendMessage("INFINITE PRAYER: " + p.getNSV().getB("infPrayer"));
		}),
		INF_SPEC("Toggle Infinite Spec", p -> {
			p.getNSV().setB("infSpecialAttack", !p.getNSV().getB("infSpecialAttack"));
			p.sendMessage("INFINITE SPECIAL ATTACK: " + p.getNSV().getB("infSpecialAttack"));
		}),
		INF_RUN("Toggle Infinite Run", p -> {
			p.getNSV().setB("infRun", !p.getNSV().getB("infRun"));
			p.sendMessage("INFINITE RUN: " + p.getNSV().getB("infRun"));
		}),
		INVISIBILITY("Toggle Invisibility", p -> {
			p.getAppearance().setHidden(!p.getAppearance().isHidden());
			p.sendMessage("HIDDEN: " + p.getAppearance().isHidden());
		}),
		BANK_DROPS("Send drops directly to bank until logout", p -> {
			p.getNSV().setB("sendingDropsToBank", true);
		}),
		RESET_TASK("Reset slayer task", p -> {
			p.getSlayer().removeTask();
			p.updateSlayerTask();
		}),
		BANK("Bank", p -> {
			p.getBank().open();
		}),
		MAGE_BOOK("Magic book", p -> {
			p.startConversation(new Dialogue().addOptions(o2 -> {
				o2.add("Modern", () -> p.getCombatDefinitions().setSpellbook(Spellbook.MODERN));
				o2.add("Ancient", () -> p.getCombatDefinitions().setSpellbook(Spellbook.ANCIENT));
				o2.add("Lunar", () -> p.getCombatDefinitions().setSpellbook(Spellbook.LUNAR));
				o2.add("Dungeoneering", () -> p.getCombatDefinitions().setSpellbook(Spellbook.DUNGEONEERING));
			}));
		}),
		PRAY_BOOK("Prayer book", p -> {
			p.startConversation(new Dialogue().addOptions(o2 -> {
			o2.add("Modern", () -> p.getPrayer().setPrayerBook(false));
			o2.add("Ancient curses", () -> p.getPrayer().setPrayerBook(true));
				}));
		}),
		LOOT_BARROWS("Loot a barrows chest", p -> {
			if (p.getControllerManager().isIn(BarrowsController.class))
				p.getControllerManager().getController(BarrowsController.class).cheat();
			else
				p.sendMessage("You're not at barrows.");
		}),
		CLUES_TO_CASKETS("Turn clue scroll boxes to caskets", p -> {
			for (Item item : p.getInventory().getItems().array()) {
				if (item == null)
					continue;
				for (int i = 0;i < TreasureTrailsManager.SCROLL_BOXES.length;i++) {
					if (item.getId() == TreasureTrailsManager.SCROLL_BOXES[i])
						item.setId(TreasureTrailsManager.CASKETS[i]);
				}
				p.getInventory().refresh();
			}
		}),
		INSTA_FARM("Instant grow all farm patches", p -> {
			for (int i = 0;i < 200;i++)
				p.tickFarming();
		}),
		NEVER_LOG("Neverlog", p -> p.getNSV().setB("idleLogImmune", true)),
		AGGRO_POT("Aggro pot toggle", p -> {
			if (p.hasEffect(Effect.AGGRESSION_POTION))
				p.removeEffect(Effect.AGGRESSION_POTION);
			else
				p.addEffect(Effect.AGGRESSION_POTION, Ticks.fromHours(10));
			p.sendMessage("Aggression potion: " + p.hasEffect(Effect.AGGRESSION_POTION));
		}),
		NO_RANDOMS("Stop randoms for session", p -> {
			p.getNSV().setL("lastRandom", World.getServerTicks() + Ticks.fromHours(300));
		});
		
		private String name;
		private Consumer<Player> action;
		
		Command(String name, Consumer<Player> action) {
			this.name = name;
			this.action = action;
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemClickHandler handle = new ItemClickHandler(new Object[] { 5733 }, new String[] { "Eat", "Heal", "CM-Tool", "Commands", "Drop" }, e -> {
		switch(e.getOption()) {
		case "Drop" -> {
			e.getPlayer().sendOptionDialogue("Drop it? It will be destroyed.", ops -> {
				ops.add("Yes, drop it.", () -> e.getPlayer().getInventory().deleteItem(e.getItem()));
				ops.add("Nevermind.");
			});
		}
		case "Eat" -> {
			if (!e.getPlayer().canEat())
				return;
			e.getPlayer().incrementCount("Food eaten");
			e.getPlayer().setNextAnimation(Foods.EAT_ANIM);
			e.getPlayer().addFoodDelay(3);
			e.getPlayer().getActionManager().setActionDelay(e.getPlayer().getActionManager().getActionDelay() + 3);
			e.getPlayer().heal(280, 100);
			e.getPlayer().addEffect(Effect.OVERLOAD, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.BONFIRE, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.ANTIPOISON, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.SUPER_ANTIFIRE, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.JUJU_MINE_BANK, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.JUJU_WC_BANK, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.JUJU_FARMING, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.JUJU_HUNTER, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.JUJU_FISHING, Ticks.fromHours(10));
			e.getPlayer().addEffect(Effect.REV_IMMUNE, Ticks.fromHours(10));
			Potion.RECOVER_SPECIAL.getEffect().accept(e.getPlayer());
			Potion.SUPER_RESTORE.getEffect().accept(e.getPlayer());
			Potion.SUPER_ENERGY.getEffect().accept(e.getPlayer());
			Potion.SUMMONING_POTION.getEffect().accept(e.getPlayer());
			Potion.STRONG_ARTISANS_POTION.getEffect().accept(e.getPlayer());
			Potion.STRONG_GATHERERS_POTION.getEffect().accept(e.getPlayer());
			Potion.STRONG_NATURALISTS_POTION.getEffect().accept(e.getPlayer());
			Potion.STRONG_SURVIVALISTS_POTION.getEffect().accept(e.getPlayer());
		}
		case "Heal" -> {
			Command command = e.getPlayer().getNSV().getO("lastPotatoCommand");
			if (command != null)
				command.action.accept(e.getPlayer());
		}
		case "Commands" -> {
			e.getPlayer().startConversation(new Dialogue().addOptions(o1 -> {
				for (Command command : Command.values())
					o1.add(command.name, () -> {
						command.action.accept(e.getPlayer());
						e.getPlayer().getNSV().setO("lastPotatoCommand", command);
					});
			}));
		}
		case "CM-Tool" -> {
			e.getPlayer().sendOptionDialogue("What would you like to do?", op -> {
				SimpleImmutableEntry<Tile, Controller> lastLoc = e.getPlayer().getNSV().getO("savedPotatoLoc");
				if (lastLoc != null)
					op.add("Teleport to saved location.", () -> {
						Magic.sendNormalTeleportSpell(e.getPlayer(), lastLoc.getKey(), p -> {
							if (lastLoc.getValue() != null) {
								p.getControllerManager().setController(lastLoc.getValue());
								p.getControllerManager().sendInterfaces();
							}
						});
					});
				op.add("Save current location.", () -> {
					e.getPlayer().getNSV().setO("savedPotatoLoc", new SimpleImmutableEntry<Tile, Controller>(Tile.of(e.getPlayer().getTile()), e.getPlayer().getControllerManager().getController()));
					e.getPlayer().sendMessage("Location saved.");
				});
			});
		}
		}
	});
	
}
