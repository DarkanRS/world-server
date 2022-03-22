package com.rs.game.content.miscitems;

import com.rs.game.content.Potions.Potion;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.skills.cooking.Foods;
import com.rs.game.content.skills.cooking.Foods.Food;
import com.rs.game.model.entity.player.controllers.BarrowsController;
import com.rs.game.model.entity.player.managers.TreasureTrailsManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class StaffPotato {

	public static ItemClickHandler handle = new ItemClickHandler(new Object[] { 5733 }, new String[] { "Eat", "Heal", "CM-Tool", "Commands", "Drop" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Drop" -> {
				e.getPlayer().sendOptionDialogue("Drop it? It will be destroyed.", ops -> {
					ops.add("Yes, drop it.", () -> e.getPlayer().getInventory().deleteItem(e.getItem()));
					ops.add("Nevermind.");
				});
			}
			case "Eat" -> {
				e.getPlayer().sendMessage("You eat the meme.");
				e.getPlayer().incrementCount("Food eaten");
				e.getPlayer().setNextAnimation(Foods.EAT_ANIM);
				e.getPlayer().addFoodDelay(3);
				e.getPlayer().getActionManager().setActionDelay(e.getPlayer().getActionManager().getActionDelay() + 3);
				e.getPlayer().heal(Food.ROCKTAIL.getHeal() * 10, Food.ROCKTAIL.getExtraHP() * 10);
				Potion.EXTREME_ATTACK.getEffect().accept(e.getPlayer());
				Potion.EXTREME_STRENGTH.getEffect().accept(e.getPlayer());
				Potion.EXTREME_DEFENCE.getEffect().accept(e.getPlayer());
				Potion.EXTREME_RANGING.getEffect().accept(e.getPlayer());
				Potion.EXTREME_MAGIC.getEffect().accept(e.getPlayer());
				Potion.SUPER_RESTORE.getEffect().accept(e.getPlayer());
				Potion.ANTIPOISONPP.getEffect().accept(e.getPlayer());
				Potion.JUJU_FARMING_POTION.getEffect().accept(e.getPlayer());
				Potion.JUJU_WOODCUTTING_POTION.getEffect().accept(e.getPlayer());
				Potion.JUJU_FISHING_POTION.getEffect().accept(e.getPlayer());
				Potion.JUJU_MINING_POTION.getEffect().accept(e.getPlayer());
				Potion.SUPER_ENERGY.getEffect().accept(e.getPlayer());
				Potion.SUMMONING_POTION.getEffect().accept(e.getPlayer());
				Potion.SUPER_ANTIFIRE.getEffect().accept(e.getPlayer());
			}
			case "Heal" -> e.getPlayer().setHitpoints(e.getPlayer().getMaxHitpoints());
			case "Commands" -> {
				e.getPlayer().startConversation(new Dialogue().addOptions(o1 -> {
					o1.add("Toggle God", new Dialogue().addNext(() -> {
						e.getPlayer().setNextAnimation(new Animation(361));
						e.getPlayer().setNextSpotAnim(new SpotAnim(122));
						e.getPlayer().getNSV().setB("godMode", !e.getPlayer().getNSV().getB("godMode"));
						e.getPlayer().sendMessage("GODMODE: " + e.getPlayer().getNSV().getB("godMode"));
					}));
					o1.add("Toggle Infinite Prayer", new Dialogue().addNext(() -> {
						e.getPlayer().setNextAnimation(new Animation(412));
						e.getPlayer().setNextSpotAnim(new SpotAnim(121));
						e.getPlayer().getNSV().setB("infPrayer", !e.getPlayer().getNSV().getB("infPrayer"));
						e.getPlayer().sendMessage("INFINITE PRAYER: " + e.getPlayer().getNSV().getB("infPrayer"));
					}));
					o1.add("Toggle Infinite Spec", new Dialogue().addNext(() -> {
						e.getPlayer().getNSV().setB("infSpecialAttack", !e.getPlayer().getNSV().getB("infSpecialAttack"));
						e.getPlayer().sendMessage("INFINITE SPECIAL ATTACK: " + e.getPlayer().getNSV().getB("infSpecialAttack"));
					}));
					o1.add("Toggle Invisibility", new Dialogue().addNext(() -> {
						e.getPlayer().getAppearance().setHidden(!e.getPlayer().getAppearance().isHidden());
						e.getPlayer().sendMessage("Hidden: " + e.getPlayer().getAppearance().isHidden());
					}));
					o1.add("Send drops directly to bank until logout", new Dialogue().addNext(() -> {
						e.getPlayer().getNSV().setB("sendingDropsToBank", true);
					}));
					o1.add("Reset slayer task", new Dialogue().addNext(() -> {
						e.getPlayer().getSlayer().removeTask();
						e.getPlayer().updateSlayerTask();
					}));
					o1.add("Bank", new Dialogue().addNext(() -> {
						e.getPlayer().getBank().open();
					}));
					o1.add("Magic book", new Dialogue().addOptions(o2 -> {
						o2.add("Modern", () -> e.getPlayer().getCombatDefinitions().setSpellBook(0));
						o2.add("Ancient", () -> e.getPlayer().getCombatDefinitions().setSpellBook(1));
						o2.add("Lunar", () -> e.getPlayer().getCombatDefinitions().setSpellBook(2));
						o2.add("Dungeoneering", () -> e.getPlayer().getCombatDefinitions().setSpellBook(3));
					}));
					o1.add("Prayer book", new Dialogue().addOptions(o2 -> {
						o2.add("Modern", () -> e.getPlayer().getPrayer().setPrayerBook(false));
						o2.add("Ancient curses", () -> e.getPlayer().getPrayer().setPrayerBook(true));
					}));
					o1.add("Loot a barrows chest", () -> {
						if (e.getPlayer().getControllerManager().isIn(BarrowsController.class))
							e.getPlayer().getControllerManager().getController(BarrowsController.class).cheat();
						else
							e.getPlayer().sendMessage("You're not at barrows.");
					});
					o1.add("Turn clue scroll boxes to caskets", () -> {
						for (Item item : e.getPlayer().getInventory().getItems().array()) {
							if (item == null)
								continue;
							for (int i = 0;i < TreasureTrailsManager.SCROLL_BOXES.length;i++) {
								if (item.getId() == TreasureTrailsManager.SCROLL_BOXES[i])
									item.setId(TreasureTrailsManager.CASKETS[i]);
							}
							e.getPlayer().getInventory().refresh();
						}
					});
				}));
			}
			case "CM-Tool" -> {
				
			}
			}
		}
	};
	
}
