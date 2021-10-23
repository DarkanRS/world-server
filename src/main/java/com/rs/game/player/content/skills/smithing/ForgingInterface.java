package com.rs.game.player.content.skills.smithing;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.smithing.Smithing.ForgingBar;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class ForgingInterface {

	public static final int componentChilds[] = new int[30];
	public static final int CLICKED_CHILDS[] = { 28, -1, 5, 1 };
	private static final int SMITHING_INTERFACE = 300;
	
	public static ButtonClickHandler handleIComponents = new ButtonClickHandler(SMITHING_INTERFACE) {
		@Override
		public void handle(ButtonClickEvent e) {
			int slot = -1;
			int ticks = -1;
			for (int i = 3; i <= 6; i++) {
				for (int index = 0; index < componentChilds.length; index++) {
					if (componentChilds[index] + i != e.getComponentId())
						continue;
					slot = index;
					ticks = CLICKED_CHILDS[i - 3];
					break;
				}
			}
			if (slot == -1)
				return;
			e.getPlayer().getActionManager().setAction(new Smithing(ticks, slot));
		}
	};

	private static void calculateComponentConfigurations() {
		int base = 18;
		for (int i = 0; i < componentChilds.length; i++) {
			if (base == 250) {
				base = 267;
			}
			componentChilds[i] = base;
			base += 8;
		}
	}

	private static int getBasedAmount(Item item) {
		String def = item.getDefinitions().getName();
		if (def.contains("dagger")) {
			return 1;
		} else if (def.contains("hatchet") || def.contains("mace") || def.contains("iron spit")) {
			return 2;
		} else if (def.contains("bolts") || def.contains("med helm")) {
			return 3;
		} else if (def.contains("sword") || def.contains("dart tip") || def.contains("nails") || def.contains("wire")) {
			return 4;
		} else if (def.contains("arrow") || def.contains("pickaxe") || def.contains("scimitar")) {
			return 5;
		} else if (def.contains("longsword") || def.contains("limbs")) {
			return 6;
		} else if (def.contains("knife") || def.contains("full helm") || def.contains("studs")) {
			return 7;
		} else if (def.contains("sq shield") || def.contains("warhammer") || def.contains("grapple tip")) {
			return 9;
		} else if (def.contains("battleaxe")) {
			return 10;
		} else if (def.contains("chainbody") || def.contains("oil lantern")) {
			return 11;
		} else if (def.contains("kiteshield")) {
			return 12;
		} else if (def.contains("claws")) {
			return 13;
		} else if (def.contains("2h sword")) {
			return 14;
		} else if (def.contains("plateskirt") || def.contains("platelegs")) {
			return 16;
		} else if (def.contains("platebody")) {
			return 18;
		} else if (def.contains("bullseye lantern")) {
			return 19;
		}
		return 1;
	}

	public static int getFixedAmount(ForgingBar bar, Item item) {
		String name = item.getDefinitions().getName();
		int increment = getBasedAmount(item);
		if (name.contains("dagger") && bar != ForgingBar.BRONZE) {
			increment--;
		} else if (name.contains("hatchet") && bar == ForgingBar.BRONZE) {
			increment--;
		}
		return increment;
	}

	public static int getForgedAmount(int id) {
		String name = ItemDefinitions.getDefs(id).getName();
		if (name.contains("knife")) {
			return 5;
		} else if (name.contains("bolts") || name.contains("dart tip")) {
			return 10;
		} else if (name.contains("arrowheads") || name.contains("nails")) {
			return 15;
		}
		return 1;
	}

	public static String[] getStrings(Player player, ForgingBar bar, int index, int itemId) {
		if (itemId == -1 || index < 0 || index >= bar.getItems().length) {
			return null;
		}
		StringBuilder barName = new StringBuilder();
		StringBuilder levelString = new StringBuilder();
		String name = ItemDefinitions.getDefs(itemId).getName().toLowerCase();
		String barVariableName = bar.toString().toLowerCase();
		int levelRequired = bar.getLevel() + getFixedAmount(bar, bar.getItems()[index]);
		int barAmount = getActualAmount(levelRequired, bar, itemId);
		if (player.getInventory().getItems().getNumberOf(bar.getBarId()) >= barAmount) {
			barName.append("<col=00FF00>");
		}
		barName.append(barAmount).append(" ").append(barAmount > 1 ? "bars" : "bar");
		if (levelRequired >= 99) {
			levelRequired = 99;
		}
		if (player.getSkills().getLevel(Constants.SMITHING) >= levelRequired) {
			levelString.append("<col=FFFFFF>");
		}
		levelString.append(Utils.formatPlayerNameForDisplay(name.replace(barVariableName + " ", "")));
		return new String[] { levelString.toString(), barName.toString() };
	}

	public static int getLevels(ForgingBar bar, int slot, Player player) {
		int base = bar.getLevel();
		int barAmount = getFixedAmount(bar, bar.getItems()[slot]);
		int level = base + barAmount;
		if (level > 99)
			level = 99;
		return level;
	}

	private static void sendComponentConfigs(Player player, ForgingBar bar) {
		for (int i : bar.getComponentChilds()) {
			player.getPackets().setIFHidden(SMITHING_INTERFACE, i - 1, false);
		}
	}

	public static int getActualAmount(int levelRequired, ForgingBar bar, int id) {
		if (levelRequired >= 99) {
			levelRequired = 99;
		}
		int level = levelRequired - bar.getLevel();
		String name = ItemDefinitions.getDefs(id).getName().toLowerCase();
		if (level >= 0 && level <= 4) {
			if (name.contains("2h")) {
				return 3;
			}
			return 1;
		} else if (level >= 4 && level <= 8) {
			if (name.contains("knife") || name.contains("limb") || name.contains("studs") || name.contains("arrow")) {
				return 1;
			}
			return 2;
		} else if (level >= 9 && level <= 16) {
			if (name.contains("grapple")) {
				return 1;
			} else if (name.contains("claws")) {
				return 2;
			}
			return 3;
		} else if (level >= 17) {
			if (name.contains("bullseye")) {
				return 1;
			}
			return 5;
		}
		return 1;
	}

	public static void sendSmithingInterface(Player player, ForgingBar bar) {
		calculateComponentConfigurations();
		player.getTempAttribs().setO("SmithingBar", bar);
		sendComponentConfigs(player, bar);
		for (int i = 0; i < bar.getItems().length; i++) {
			player.getPackets().setIFItem(SMITHING_INTERFACE, componentChilds[i], bar.getItems()[i].getId(), 1);
			String[] name = getStrings(player, bar, i, bar.getItems()[i].getId());
			if (name != null) {
				player.getPackets().setIFText(300, componentChilds[i] + 1, name[0]);
				player.getPackets().setIFText(300, componentChilds[i] + 2, name[1]);
			}
		}
		player.getPackets().setIFText(300, 14, Utils.formatPlayerNameForDisplay(bar.toString().toLowerCase()) + "");
		player.getInterfaceManager().sendInterface(SMITHING_INTERFACE);
	}
}
