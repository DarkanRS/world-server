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
package com.rs.game.player.content.skills.smithing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.rs.game.player.Player;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.skills.util.Category;
import com.rs.game.player.content.skills.util.ReqItem;
import com.rs.game.player.dialogues.CreationActionD;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

/* INTERFACE 825 */
/* INTERFACE 1070 */
/* INTERFACE 1071 */
/* INTERFACE 1072 */

@PluginEventHandler
public class ArtisansWorkshop  {

	public static final HashMap<String, int[]> ORES = new HashMap<>();
	public static final int ORE_IDX = 0, ORE_NOTED = 1, A_ORE_ID = 2, CAP_AMT = 3;

	public static final String[] ORE_OPTIONS = { "Iron", "Coal", "Mithril", "Adamantite", "Runite", "None" };

	static {
		ORES.put("Iron", new int[] { 0, 441, 25629, 4000 });
		ORES.put("Coal", new int[] { 1, 454, 25630, 8000 });
		ORES.put("Mithril", new int[] { 2, 448, 25631, 4000 });
		ORES.put("Adamantite", new int[] { 3, 450, 25632, 4000 });
		ORES.put("Runite", new int[] { 4, 452, 25633, 4000 });
	}

	public static int numStoredOres(Player player, int itemId) {
		return player.artisanOres[itemId-25629];
	}

	public static void removeOres(Player player, String oreName, int amount) {
		player.artisanOres[ORES.get(oreName)[ORE_IDX]] -= amount;
		player.sendMessage("You withdraw " + amount + " ores. You now have " + player.artisanOres[ORES.get(oreName)[ORE_IDX]] + " " + oreName.toLowerCase() + " in the furnace.");
		sendOreVars(player);
	}

	public static void addOres(Player player, String oreName, int amount) {
		player.artisanOres[ORES.get(oreName)[ORE_IDX]] += amount;
		player.sendMessage("You deposit " + amount + " ores. You now have " + player.artisanOres[ORES.get(oreName)[ORE_IDX]] + " " + oreName.toLowerCase() + " in the furnace.");
		sendOreVars(player);
	}

	public static void depositOres(Player player, String oreName, int amount) {
		if (amount > player.getInventory().getAmountOf(ORES.get(oreName)[ORE_NOTED]))
			amount = player.getInventory().getAmountOf(ORES.get(oreName)[ORE_NOTED]);
		if (amount == 0) {
			player.sendMessage("You don't have any " + oreName + " to deposit.");
			return;
		}
		if (amount + player.artisanOres[ORES.get(oreName)[ORE_IDX]] > ORES.get(oreName)[CAP_AMT])
			amount = ORES.get(oreName)[CAP_AMT] - player.artisanOres[ORES.get(oreName)[ORE_IDX]];
		if (amount == 0) {
			player.sendMessage("You already have " + ORES.get(oreName)[CAP_AMT] + " " + oreName + " stored in the furnace. You can't fit any more in.");
			return;
		}
		player.getInventory().deleteItem(ORES.get(oreName)[ORE_NOTED], amount);
		addOres(player, oreName, amount);
	}

	public static void withdrawOres(Player player, String oreName, int amount) {
		if (amount > player.artisanOres[ORES.get(oreName)[ORE_IDX]])
			amount = player.artisanOres[ORES.get(oreName)[ORE_IDX]];
		if (amount == 0) {
			player.sendMessage("You don't have any " + oreName + " to withdraw.");
			return;
		}
		player.getInventory().addItem(ORES.get(oreName)[ORE_NOTED], amount);
		removeOres(player, oreName, amount);
	}

	public static void handleDepositOres(Player player) {
		String[] filteredOres = Arrays.stream(ORE_OPTIONS).filter(oreName -> oreName.equals("None") ? true : player.getInventory().containsItem(ORES.get(oreName)[ORE_NOTED], 1)).toArray(String[]::new);
		if (filteredOres.length < 2) {
			player.sendMessage("You don't have any ores to return.");
			return;
		}
		player.sendOptionDialogue("Which ore would you like to deposit?", filteredOres, new DialogueOptionEvent() {
			@Override
			public void run(Player player) {
				if (!getOptionString().equals("None"))
					promptForDeposit(player, getOptionString());
			}
		});
	}

	public static void promptForDeposit(Player player, String oreName) {
		player.sendOptionDialogue("How many would you like to deposit?", new String[] { "10", "100", "1000", "All", "X" }, new DialogueOptionEvent() {
			@Override
			public void run(Player player) {
				switch(getOption()) {
				case 1:
				case 2:
				case 3:
				case 4:
					depositOres(player, oreName, (int) Math.pow(10, getOption()));
					break;
				case 5:
					player.sendInputInteger("How many would you like to deposit?", (amount) -> depositOres(player, oreName, amount));
					break;
				}
			}
		});
	}

	public static void handleWithdrawOres(Player player) {
		String[] filteredOres = Arrays.stream(ORE_OPTIONS).filter(oreName -> oreName.equals("None") ? true : player.artisanOres[ORES.get(oreName)[ORE_IDX]] > 0).toArray(String[]::new);
		if (filteredOres.length < 2) {
			player.sendMessage("You don't have any ores stored in the smelter.");
			return;
		}
		player.sendOptionDialogue("Which ore would you like to withdraw?", filteredOres, new DialogueOptionEvent() {
			@Override
			public void run(Player player) {
				if (!getOptionString().equals("None"))
					promptForWithdrawal(player, getOptionString());
			}
		});
	}

	public static void promptForWithdrawal(Player player, String oreName) {
		player.sendOptionDialogue("How many would you like to withdraw?", new String[] { "10", "100", "1000", "All", "X" }, new DialogueOptionEvent() {
			@Override
			public void run(Player player) {
				switch(getOption()) {
				case 1:
				case 2:
				case 3:
				case 4:
					withdrawOres(player, oreName, (int) Math.pow(10, getOption()));
					break;
				case 5:
					player.sendInputInteger("How many would you like to withdraw?", (amount) -> withdrawOres(player, oreName, amount));
					break;
				}
			}
		});
	}

	public static void depositAllArmor(Player player) {
		for (int i = 20572;i <= 20631;i++)
			if (player.getInventory().containsItem(i, 1))
				player.getInventory().deleteItem(i, player.getInventory().getAmountOf(i));
		player.sendMessage("You deposit all your armor.");
	}

	public static void returnAllIngots(Player player) {
		for (int i = 20632;i <= 20652;i++)
			if (player.getInventory().containsItem(i, 1)) {
				ReqItem defs = ReqItem.getRequirements(i);
				Item[] materials = defs.getMaterialsFor(player.getInventory().getAmountOf(i));
				for (Item mat : materials)
					if (mat.getAmount() + player.artisanOres[ORES.get(mat.getDefinitions().name)[ORE_IDX]] > ORES.get(mat.getDefinitions().name)[CAP_AMT]) {
						player.sendMessage("You already have " + ORES.get(mat.getDefinitions().name)[CAP_AMT] + " " + mat.getDefinitions().name + " stored in the furnace. You can't fit any more in.");
						return;
					}
				for (Item mat : materials)
					addOres(player, mat.getDefinitions().name, mat.getAmount());
				player.getInventory().deleteItem(i, player.getInventory().getAmountOf(i));
			}
	}

	public static void openIngotCreation(Player player, final ReqItem[] ingots) {
		player.getDialogueManager().execute(new Dialogue() {
			@Override
			public void start() {
				SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "What kind of bar would you like?", player.getInventory().getFreeSlots(), ingots, null);
			}

			@Override
			public void run(int interfaceId, int componentId) {
				int option = SkillsDialogue.getItemSlot(componentId);
				if (option > ingots.length) {
					end();
					return;
				}
				int quantity = SkillsDialogue.getQuantity(player);
				if (quantity > player.getInventory().getFreeSlots())
					quantity = player.getInventory().getFreeSlots();
				Item[] materials = ingots[option].getMaterialsFor(quantity);
				if (materials.length <= 0)
					return;
				for (Item mat : materials)
					if (player.getInventory().getAmountOf(mat.getId()) < mat.getAmount()) {
						player.sendMessage("You need " + mat.getAmount() + " " + mat.getDefinitions().name + " to make " + quantity + " " + ingots[option].getProduct().getDefinitions().name+ (quantity > 1 ? "s" : "") +".");
						end();
						return;
					}
				for (Item mat : materials)
					if (mat.getId() >= 25629 && mat.getId() <= 25633)
						removeOres(player, mat.getDefinitions().name, mat.getAmount());
				player.getInventory().addItem(new Item(ingots[option].getProduct().setAmount(quantity)));
				end();
			}

			@Override
			public void finish() {

			}

		});
	}

	//	@Override
	//	public void onItemCreation(Player player, int skill, Item item, double xp) {
	//		if (item.getId() >= 20572 && item.getId() <= 20631) {
	//			int currXp = (int) player.get("artisan-xp");
	//			if ((currXp + xp) > 10000) {
	//				int currRep = (int) player.get("artisan-rep");
	//				if (currRep >= 100)
	//					return;
	//				player.save("artisan-xp", 0);
	//				player.save("artisan-rep", currRep+1);
	//				updateReputation(player);
	//			} else {
	//				player.save("artisan-xp", currXp + xp);
	//			}
	//		}
	//	}

	public void updateReputation(Player player) {
		//TODO find the varbit and the interface
	}

	public void openReputationShop(Player player) {
		//TODO find the interface id for it
	}

	//	@ButtonClickHandler(ids = { 0 })
	//	public static void onButtonClick(ButtonClickEvent e) {
	//		//TODO find interface id and button handlers
	//	}

	public static int getHighestIngot(Player player) {
		int highest = 20632;
		for (int i = 20632;i <= 20652;i++)
			if (player.getInventory().containsItem(i, 1))
				highest = i;
		return highest;
	}

	private static ReqItem[] IRON = { ReqItem.IRON_INGOT_I, ReqItem.IRON_INGOT_II, ReqItem.IRON_INGOT_III, ReqItem.IRON_INGOT_IV };
	private static ReqItem[] STEEL = { ReqItem.STEEL_INGOT_I, ReqItem.STEEL_INGOT_II, ReqItem.STEEL_INGOT_III, ReqItem.STEEL_INGOT_IV };
	private static ReqItem[] MITHRIL = { ReqItem.MITHRIL_INGOT_I, ReqItem.MITHRIL_INGOT_II, ReqItem.MITHRIL_INGOT_III, ReqItem.MITHRIL_INGOT_IV };
	private static ReqItem[] ADAMANT = { ReqItem.ADAMANT_INGOT_I, ReqItem.ADAMANT_INGOT_II, ReqItem.ADAMANT_INGOT_III, ReqItem.ADAMANT_INGOT_IV };
	private static ReqItem[] RUNE = { ReqItem.RUNE_INGOT_I, ReqItem.RUNE_INGOT_II, ReqItem.RUNE_INGOT_III, ReqItem.RUNE_INGOT_IV };

	private static HashMap<String, ReqItem[]> BAR_SETS = new HashMap<>();

	static {
		BAR_SETS.put("Iron", IRON);
		BAR_SETS.put("Steel", STEEL);
		BAR_SETS.put("Mithril", MITHRIL);
		BAR_SETS.put("Adamant", ADAMANT);
		BAR_SETS.put("Rune", RUNE);
	}

	public static ObjectClickHandler handleDepositArmor = new ObjectClickHandler(new Object[] { 29396 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			depositAllArmor(e.getPlayer());
		}
	};

	public static ObjectClickHandler handleAnvil = new ObjectClickHandler(new Object[] { 4046 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			int highestIngot = getHighestIngot(e.getPlayer());
			if (highestIngot != -1)
				e.getPlayer().getDialogueManager().execute(new CreationActionD(Category.ARTISANS, highestIngot, 898, 15).setConsistentAnimation());
			else
				e.getPlayer().sendMessage("You don't have any ingots to smith.");
		}
	};

	public static ObjectClickHandler handleFurnaces = new ObjectClickHandler(new Object[] { 29394, 29395 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getOpNum()) {
			case OBJECT_OP1:
				ArrayList<String> options = new ArrayList<>();
				for (ReqItem[] bar : BAR_SETS.values()) {
					if (!e.getPlayer().getInventory().hasMaterials(bar[0].getMaterials()))
						continue;
					options.add(bar[0].getProduct().getDefinitions().name.split(" ")[0]);
				}
				if (options.size() > 1)
					e.getPlayer().sendOptionDialogue("Which type of bar would you like to make?", options.toArray(new String[options.size()]), new DialogueOptionEvent() {
						@Override
						public void run(Player player) {
							openIngotCreation(player, BAR_SETS.get(getOptionString()));
						}
					});
				else if (options.size() == 1)
					openIngotCreation(e.getPlayer(), BAR_SETS.get(options.get(0)));
				else
					e.getPlayer().sendMessage("You don't have any ore stored.");
				break;
			case OBJECT_OP2:
				returnAllIngots(e.getPlayer());
				break;
			case OBJECT_OP3:
				handleDepositOres(e.getPlayer());
				break;
			case OBJECT_OP4:
				handleWithdrawOres(e.getPlayer());
				break;
			default:
				break;
			}
		}
	};

	public static LoginHandler onLogin = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if (e.getPlayer().artisanOres == null)
				e.getPlayer().artisanOres = new int[5];
			sendOreVars(e.getPlayer());
		}
	};

	public static void sendOreVars(Player player) {
		player.getVars().setVarBit(8857, player.artisanOres[0]);
		player.getVars().setVarBit(8859, player.artisanOres[1]);
		player.getVars().setVarBit(8862, player.artisanOres[2]);
		player.getVars().setVarBit(8863, player.artisanOres[3]);
		player.getVars().setVarBit(8865, player.artisanOres[4]);
		player.getPackets().sendRunScriptReverse(4182);
	}
}
