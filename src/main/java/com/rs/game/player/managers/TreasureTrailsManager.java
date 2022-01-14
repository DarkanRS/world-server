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
package com.rs.game.player.managers;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.others.ClueNPC;
import com.rs.game.npc.others.Ugi;
import com.rs.game.object.GameObject;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.WildernessController;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.player.dialogues.SimpleItemMessage;
import com.rs.game.player.managers.EmotesManager.Emote;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.file.FileManager;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class TreasureTrailsManager {

	//VARC 1323 = bitpacked location for arrow pointy clue interface 996

	private static final int EASY = 0, MEDIUM = 1, HARD = 2, ELITE = 3;
	public static final int SOURCE_DIG = 0, SOURCE_EMOTE = 1, SOURCE_NPC = 2, SOURCE_PUZZLENPC = 3, SOURCE_OBJECT = 4;

	public static final int[] SCROLL_BOXES = { 19005, 19065, 18937, 19041 };
	public static final int[] CLUE_SCROLLS = { 2677, 2801, 2722, 19043 };
	public static final int[] CASKETS = { 2714, 2802, 2724, 19039 };
	public static final int[] PUZZLES = { 2798, 3565, 3576, 19042 };
	public static final int[] BASE_PIECES = { 2749, 3619, 3643, 18841 };
	public static final String[] LEVEL = { "Easy", "Medium", "Hard", "Elite" };

	private static final int PUZZLE_SIZE = 25;

	private Clue currentClue;

	private transient int cluePhase;
	private transient Player player;
	private transient List<Item> pieces;

	public static ButtonClickHandler handlePuzzleButtons = new ButtonClickHandler(363) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 4)
				e.getPlayer().getTreasureTrailsManager().movePuzzlePeice(e.getSlotId());
		}
	};

	public static ButtonClickHandler handleSextantButtons = new ButtonClickHandler(365) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().getTreasureTrailsManager().handleSextant(e.getComponentId());
		}
	};

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getPhase() {
		return cluePhase;
	}

	public void setPhase(int phase) {
		cluePhase = phase;
	}

	private int generateClueSize(int level) {
		switch (level) {
		case EASY:
			return 1 + Utils.random(3);
		case MEDIUM:
			return 2 + Utils.random(3);
		case HARD:
			return 3 + Utils.random(4);
		case ELITE:
			return 3 + Utils.random(3);
		}
		return 1;
	}

	private ClueDetails generateClueDetails(int dificulty) {
		while (true) {
			ClueDetails detail = ClueDetails.values()[Utils.random(ClueDetails.values().length)];
			if (detail.level > dificulty)
				continue;
			/*
			 * int difference = dificulty - detail.level; if (difference == 0)
			 * return detail; else if (difference == 1 && Utils.random(2) == 0)
			 * return detail; else if (difference == 2 && Utils.random(4) == 0)
			 * return detail; else if (difference == 3 && Utils.random(8) == 0)
			 * return detail;
			 */
			return detail; // else we get same clues all time o.o
			// continue;
		}
	}

	public static int getScrollLevel(int id) {
		for (int i = 0; i < CLUE_SCROLLS.length; i++)
			if (CLUE_SCROLLS[i] == id)
				return i;
		return -1;
	}

	private int getScrollboxLevel(int id) {
		for (int i = 0; i < SCROLL_BOXES.length; i++)
			if (SCROLL_BOXES[i] == id)
				return i;
		return -1;
	}

	private int getCasketLevel(int id) {
		for (int i = 0; i < CASKETS.length; i++)
			if (CASKETS[i] == id)
				return i;
		return -1;
	}

	public void setNextClue(int source) {
		int lastPhase = (currentClue.details.type == COORDINATE && currentClue.dificulty >= HARD) ? 2 : (currentClue.details.type == EMOTE ? 5 : 0);

		if (cluePhase == lastPhase) {
			cluePhase = 0;
			pieces = null;

			if (source == SOURCE_DIG) {
				if (!currentClue.isLast()) {
					currentClue.count--;
					currentClue.details = generateClueDetails(currentClue.dificulty);
					player.sendMessage("You've found another clue!");
					player.getDialogueManager().execute(new SimpleItemMessage(), CLUE_SCROLLS[currentClue.dificulty], "You've found another clue!");
				} else {
					player.getInventory().deleteItem(CLUE_SCROLLS[currentClue.dificulty], 1);
					player.getInventory().addItemDrop(CASKETS[currentClue.dificulty], 1);
					player.sendMessage("Well done! You've completed the Treasure Trail.");
					currentClue = null;
				}
			} else if (source == SOURCE_NPC || source == SOURCE_PUZZLENPC) {
				if (!currentClue.isLast()) {
					currentClue.count--;
					int id = currentClue.details.ids[0];
					currentClue.details = generateClueDetails(currentClue.dificulty);
					if (id == 2812)
						player.getDialogueManager().execute(new SimpleItemMessage(), CLUE_SCROLLS[currentClue.dificulty], "Grrrrow!");
					else
						player.getDialogueManager().execute(new SimpleItemMessage(), CLUE_SCROLLS[currentClue.dificulty], NPCDefinitions.getDefs(id).getName() + " has given you another clue scroll.");
				} else {
					player.getInventory().deleteItem(CLUE_SCROLLS[currentClue.dificulty], 1);
					player.getInventory().addItemDrop(CASKETS[currentClue.dificulty], 1);
					player.sendMessage("Well done! You've completed the Treasure Trail.");
					currentClue = null;
				}
			} else if (source == SOURCE_EMOTE) {
				if (!currentClue.isLast()) {
					currentClue.count--;
					currentClue.details = generateClueDetails(currentClue.dificulty);
					player.sendMessage("You've been given another clue!");
					player.getDialogueManager().execute(new SimpleItemMessage(), CLUE_SCROLLS[currentClue.dificulty], "You've been given another clue!");
				} else {
					player.getInventory().deleteItem(CLUE_SCROLLS[currentClue.dificulty], 1);
					player.getInventory().addItemDrop(CASKETS[currentClue.dificulty], 1);
					player.sendMessage("Well done! You've completed the Treasure Trail.");
					currentClue = null;
				}
			} else if (source == SOURCE_OBJECT) {
				if (!currentClue.isLast()) {
					currentClue.count--;
					currentClue.details = generateClueDetails(currentClue.dificulty);
					player.sendMessage("You've found another clue!");
					player.getDialogueManager().execute(new SimpleItemMessage(), CLUE_SCROLLS[currentClue.dificulty], "You've found another clue!");
				} else {
					player.getInventory().deleteItem(CLUE_SCROLLS[currentClue.dificulty], 1);
					player.getInventory().addItemDrop(CASKETS[currentClue.dificulty], 1);
					player.sendMessage("Well done! You've completed the Treasure Trail.");
					currentClue = null;
				}
			} else
				throw new RuntimeException("UNKNOWN_SOURCE:" + source);
		} else if (cluePhase == 0 && (currentClue.details.type == COORDINATE || currentClue.details.type == EMOTE) && currentClue.dificulty >= HARD) {
			// spawn combat npc
			boolean inWilderness = player.getControllerManager().getController() instanceof WildernessController;
			boolean isCoordinateClue = currentClue.details.type == COORDINATE;
			final ClueNPC npc = new ClueNPC(player, inWilderness ? isCoordinateClue ? 1007 : 5144 : isCoordinateClue ? 1264 : 5145, World.getFreeTile(player, 1));
			npc.setNextSpotAnim(new SpotAnim(74));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					npc.setTarget(player);
					npc.setNextForceTalk(new ForceTalk(npc.getId() == 1007 ? "For Zamorak!" : npc.getId() == 1264 ? "For Saradomin!" : "I expect you to die!"));
				}
			});
			cluePhase = 1;
		} else if (((cluePhase == 0 && currentClue.dificulty < HARD) || (cluePhase == 2 && currentClue.dificulty >= HARD)) && currentClue.details.type == EMOTE) {
			final NPC npc = new Ugi(player, 5141, World.getFreeTile(player, 1));
			npc.setNextSpotAnim(new SpotAnim(74));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					npc.faceEntity(player);
				}
			});
			cluePhase = ((Emote[]) currentClue.details.parameters[0]).length == 1 ? 4 : 3;
		} else if (cluePhase == 3)
			cluePhase = 4; // for emotes
	}

	public static boolean isScroll(int id) {
		return getScrollLevel(id) != -1;
	}

	public void resetCurrentClue() {
		currentClue = null;
	}

	public boolean hasClueScrollItem() {
		for (int scroll : CLUE_SCROLLS)
			if (player.containsItem(scroll))
				return true;
		return false;
	}

	public void setCurrentClue(int level) {
		currentClue = new Clue(generateClueDetails(level), generateClueSize(level), level);
	}

	public void SelectAClue(int level, int index) {
		ClueDetails detail = ClueDetails.values()[index];
		currentClue = new Clue(detail, generateClueSize(level), level);
	}
	public boolean useItem(Item item, int slot) {
		int level = getScrollboxLevel(item.getId());
		if (level != -1) {
			if (hasClueScrollItem()) {
				player.sendMessage("You should finish the clue you already have first.");
				return true;
			}
			resetCurrentClue();
			player.getInventory().deleteItem(SCROLL_BOXES[level], 1);
			player.getInventory().addItem(CLUE_SCROLLS[level], 1);
			player.getDialogueManager().execute(new SimpleItemMessage(), CLUE_SCROLLS[level], "You've found another clue!");
			return true;
		}
		level = getCasketLevel(item.getId());
		if (level != -1) {
			player.getInventory().deleteItem(item.getId(), 1);
			openReward(level);
			return true;
		}
		level = getScrollLevel(item.getId());
		if (level != -1) {
			if (currentClue == null)
				setCurrentClue(level);
			if (currentClue.details.type == SIMPLE || currentClue.details.type == ANAGRAM) {
				player.getInterfaceManager().sendInterface(345);
				int offset = (8 - currentClue.details.parameters.length) / 2;
				for (int i = 0; i < 8; i++)
					player.getPackets().setIFText(345, i + 1, "");
				for (int i = currentClue.details.type == EMOTE ? 2 : 0; i < currentClue.details.parameters.length; i++)
					player.getPackets().setIFText(345, i + 1 + offset, "<shad=000000>" + (String) currentClue.details.parameters[i] + "</shad>");
			} else if (currentClue.details.type == EMOTE) {
				player.getInterfaceManager().sendInterface(345);
				for (int i = 0; i < 8; i++)
					player.getPackets().setIFText(345, i + 1, "");
				for (int i = 2; i < currentClue.details.parameters.length; i++)
					player.getPackets().setIFText(345, i, "<shad=000000>" + (String) currentClue.details.parameters[i] + "</shad>");
			} else if (currentClue.details.type == MAP)
				player.getInterfaceManager().sendInterface((int) currentClue.details.parameters[0]);
			else if (currentClue.details.type == COORDINATE) {
				player.getInterfaceManager().sendInterface(345);
				for (int i = 0; i < 8; i++)
					player.getPackets().setIFText(345, i + 1, "");
				player.getPackets().setIFText(345, 4, ((Integer) currentClue.details.parameters[0] <= 9 ? "0" : "") + currentClue.details.parameters[0] + " degrees, " + ((Integer) currentClue.details.parameters[1] <= 9 ? "0" : "")
						+ currentClue.details.parameters[1] + " minutes " + ((Integer) currentClue.details.parameters[2] == NORTH ? "north" : "south"));
				player.getPackets().setIFText(345, 5, ((Integer) currentClue.details.parameters[3] <= 9 ? "0" : "") + currentClue.details.parameters[3] + " degrees, " + ((Integer) currentClue.details.parameters[4] <= 9 ? "0" : "")
						+ currentClue.details.parameters[4] + " minutes " + ((Integer) currentClue.details.parameters[5] == WEST ? "west" : "east"));
			}
			return true;
		}
		return false;
	}

	private boolean hasCurrentClue() {
		if (!player.getInventory().containsOneItem(CLUE_SCROLLS[currentClue.dificulty]))
			return false;
		return true;
	}

	public void useEmote(Emote emote) {
		if (currentClue == null)
			return;
		if (currentClue.details.type != EMOTE)
			return;
		else if (!hasCurrentClue())
			return;
		else if (!player.withinDistance(new WorldTile(currentClue.details.getId()), 8)) {
			player.sendMessage("Hint: " + new WorldTile(currentClue.details.getId()).toString());
			return;
		} else if (emote != ((Emote[]) currentClue.details.parameters[0])[cluePhase == 3 ? 1 : 0])
			return;
		Item[] requiredItems = (Item[]) currentClue.details.parameters[1];
		if (requiredItems.length > 0)
			for (Item item : requiredItems) {
				int slot = item.getId() == 20922 ? Equipment.RING : item.getDefinitions().getEquipSlot();
				Item requestedItem = player.getEquipment().getItem(slot);
				if (requestedItem == null)
					return;
				if (slot == Equipment.RING && item.getId() == 20922) {
					if (!player.getEquipment().containsOneItem(2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566))
						return;
				} else if (requestedItem.getId() != item.getId())
					return;
			}
		setNextClue(SOURCE_EMOTE);
	}

	public boolean useDig() {
		if (currentClue == null)
			return false;
		if ((currentClue.details.type == SIMPLE || currentClue.details.type == MAP) && currentClue.details.idType == TILE) {
			if (!hasCurrentClue())
				return false;
			WorldTile tile = new WorldTile(currentClue.details.getId());
			if (!player.withinDistance(tile, currentClue.details.type == MAP ? 6 : 16))
				return false;
			setNextClue(SOURCE_DIG);
			return true;
		}
		if (currentClue.details.type == COORDINATE /*&& hasSextantItems()*/) {
			if (!hasCurrentClue())
				return false;
			WorldTile t = getTile((Integer) currentClue.details.parameters[0], (Integer) currentClue.details.parameters[1], (Integer) currentClue.details.parameters[2], (Integer) currentClue.details.parameters[3],
					(Integer) currentClue.details.parameters[4], (Integer) currentClue.details.parameters[5]);
			if (!player.withinDistance(t, 6)) // setted distance cuz the getTile
				// method may miss 3-5 tiles on rs
				return false;
			setNextClue(SOURCE_DIG);
			return true;
		}
		return false;
	}

	public boolean useObject(GameObject object) {
		if ((currentClue == null) || currentClue.details.idType != OBJECT || !currentClue.details.isId(object.getId()))
			return false;
		if (currentClue.details.type != SIMPLE && currentClue.details.type != MAP)
			return false;
		else if (!hasCurrentClue())
			return false;
		setNextClue(SOURCE_OBJECT);
		return true;
	}

	public boolean useNPC(final NPC npc) {
		if ((currentClue == null) || currentClue.details.idType != NPC || !currentClue.details.isId(npc.getId()))
			return false;
		if (currentClue.details.type != SIMPLE && currentClue.details.type != ANAGRAM)
			return false;
		if (!hasCurrentClue())
			return false;
		if (currentClue.details.type == ANAGRAM && currentClue.dificulty >= HARD && !player.containsOneItem(PUZZLES)) {
			player.getDialogueManager().execute(new Dialogue() {
				@Override
				public void start() {
					sendNPCDialogue(currentClue.details.getId(), Dialogue.NORMAL, "I have a puzzle for you!");
					stage = 0;
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (stage == 0) {
						int puzzle_id = currentClue.dificulty == ELITE ? PUZZLES[3] : PUZZLES[Utils.random(PUZZLES.length - 1)];
						sendEntityDialogue(IS_ITEM, puzzle_id, -1, "", npc.getName() + " has given you a puzzle box!");
						player.getInventory().addItem(puzzle_id, 1);
						stage = 1;
					} else
						end();
				}

				@Override
				public void finish() {
				}

			});

			return true;
		}
		if (currentClue.details.type == ANAGRAM && currentClue.dificulty >= HARD && !hasCompletedPuzzle()) {
			player.getDialogueManager().execute(new Dialogue() {
				@Override
				public void start() {
					sendNPCDialogue(currentClue.details.getId(), Dialogue.NORMAL, "That doesn't look right.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					end();
				}

				@Override
				public void finish() {
				}

			});

			return true;
		}
		if ((currentClue.details.type != ANAGRAM) || (currentClue.dificulty < HARD)) {
			player.getDialogueManager().execute(new Dialogue() {
				@Override
				public void start() {
					sendNPCDialogue(currentClue.details.getId(), Dialogue.NORMAL, "Congratulations! You have come to right place.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					end();
					setNextClue(SOURCE_NPC);
				}

				@Override
				public void finish() {
				}

			});
			return true;
		}
		player.getDialogueManager().execute(new Dialogue() {
			@Override
			public void start() {
				sendNPCDialogue(currentClue.details.getId(), Dialogue.NORMAL, "Good job!");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				end();
				for (int id : PUZZLES)
					player.getInventory().deleteItem(id, 1);
				setNextClue(SOURCE_PUZZLENPC);
			}

			@Override
			public void finish() {
			}

		});
		return true;
	}

	public void openPuzzle(int itemId) {
		if (currentClue == null)
			return;
		if (currentClue.details.type == ANAGRAM)
			if (currentClue.dificulty != HARD && currentClue.dificulty != ELITE)
				return;
		int base = BASE_PIECES[getBasePiece(itemId)];
		if (pieces == null) {
			pieces = new ArrayList<>(PUZZLE_SIZE);
			for (int index = 0; index < PUZZLE_SIZE - 1; index++)
				pieces.add(new Item(base + index, 1));
			pieces.add(new Item(-1, 1));
			player.getPackets().sendItems(207, pieces.toArray(new Item[pieces.size()]));

			pieces.clear();
			int[] mix = createMix();
			for (int element : mix)
				if (element != -1)
					pieces.add(new Item(base + element, 1));
				else
					pieces.add(new Item(-1, 1));
		}
		player.getPackets().setIFRightClickOps(363, 4, 0, 25, 0);
		player.getPackets().sendItems(140, pieces.toArray(new Item[pieces.size()]));
		player.getInterfaceManager().sendInterface(363);
	}

	public void movePuzzlePeice(int index) {
		int[] directSlots = { index - 1, index + 1, index + 5, index - 5 };
		for (int slot : directSlots) {
			if (slot >= PUZZLE_SIZE || slot < 0)
				continue;
			Item emptyPeice = pieces.get(slot);
			if (emptyPeice.getId() == -1) {
				pieces.set(slot, pieces.get(index));
				pieces.set(index, emptyPeice);
			}
		}
		player.getPackets().sendItems(140, pieces.toArray(new Item[pieces.size()]));
	}

	private int getBasePiece(int requestedId) {
		for (int index = 0; index < PUZZLES.length; index++)
			if (PUZZLES[index] == requestedId)
				return index;
		return -1;
	}

	private boolean hasCompletedPuzzle() {
		if (pieces == null)
			return false;
		main: for (int id : PUZZLES) {
			int base = BASE_PIECES[getBasePiece(id)];
			for (int index = 0; index < PUZZLE_SIZE - 1; index++)
				if (index + base != pieces.get(index).getId())
					continue main;
			return true;
		}
		return false;
	}

	public static Item[] generateRewards(Player player, int level) {
		ArrayList<Item> rewards = new ArrayList<>();
		if (level == EASY)
			for (int i = 0;i < Utils.randomInclusive(2, 4);i++)
				Utils.add(rewards, DropTable.calculateDrops(player, DropSets.getDropSet("easy_casket")));
		else if (level == MEDIUM)
			for (int i = 0;i < Utils.randomInclusive(3, 5);i++)
				Utils.add(rewards, DropTable.calculateDrops(player, DropSets.getDropSet("medium_casket")));
		else if (level == HARD)
			for (int i = 0;i < Utils.randomInclusive(4, 6);i++)
				Utils.add(rewards, DropTable.calculateDrops(player, DropSets.getDropSet("hard_casket")));
		else if (level == ELITE)
			for (int i = 0;i < Utils.randomInclusive(4, 6);i++)
				Utils.add(rewards, DropTable.calculateDrops(player, DropSets.getDropSet("elite_casket")));
		Item[] rewArr = new Item[rewards.size()];
		for (int i = 0;i < rewArr.length;i++)
			rewArr[i] = rewards.get(i);
		return rewArr;
	}

	public void openReward(int level) {
		player.getInterfaceManager().sendInterface(364);
		player.getPackets().sendInterSetItemsOptionsScript(364, 4, 141, 3, 4, "Examine");
		final Item[] rewards = generateRewards(player, level);
		player.getPackets().sendMusicEffect(193);
		player.getPackets().setIFRightClickOps(364, 4, 0, rewards.length, 0);
		player.incrementCount(LEVEL[level] + " clues completed");
		boolean banked = false;
		for (Item item : rewards) {
			if (player.getInventory().hasRoomFor(item))
				player.getInventory().addItemDrop(item);
			else {
				if (item.getDefinitions().isNoted())
					item.setId(item.getDefinitions().certId);
				player.getBank().addItem(item, true);
				banked = true;
			}
			if ((item.getId() >= 10330 && item.getId() <= 10353) || (item.getId() >= 19308 && item.getId() <= 19322)) {
				World.sendWorldMessage("<img=4><shad=000000><col=00FF00>" + player.getDisplayName() + " has just recieved a " + item.getDefinitions().getName() + " drop from a clue scroll!", false);
				FileManager.writeToFile("droplog.txt", player.getDisplayName() + " has just recieved a " + item.getDefinitions().getName() + " drop from a clue scroll!");
			}
		}
		if (banked)
			player.sendMessage("As you had no space in your inventory, the items were sent to your bank.", true);
		player.getPackets().sendItems(141, rewards);
	}

	public boolean hasSextantItems() {
		return player.getInventory().containsItem(2575, 1) && player.getInventory().containsItem(2576, 1) && player.getInventory().containsItem(2574, 1);
	}

	public void handleSextant(int componentId) {
		if (currentClue == null || currentClue.details.type != COORDINATE) {
			player.sendMessage("It seems the telescope is not operational at the moment.");
			return;
		}
		if (componentId == 12) {
			int[] location = getCoordinates(player);
			player.sendMessage("The sextant displays:");
			player.sendMessage((location[0] <= 9 ? "0" : "") + location[0] + " degrees, " + (location[1] <= 9 ? "0" : "") + location[1] + " minutes " + (location[2] == NORTH ? "north" : "south"));
			player.sendMessage((location[3] <= 9 ? "0" : "") + location[3] + " degrees, " + (location[4] <= 9 ? "0" : "") + location[4] + " minutes " + (location[5] == WEST ? "west" : "east"));
		}
	}

	public void useSextant() {
		if (!hasSextantItems()) {
			player.sendMessage("You also need a watch and a chart to use the sextant!");
			return;
		}
		player.getInterfaceManager().sendInterface(365);
	}

	public static WorldTile getTile(int degreeY, int minY, int dirY, int degreeX, int minX, int dirX) {
		double offsetY = degreeY * 60 / 1.875 + minY / 1.875;
		double offsetX = degreeX * 60 / 1.875 + minX / 1.875;
		return new WorldTile(2440 + (dirX == EAST ? (int) offsetX : (int) -offsetX), 3162 + (dirY == NORTH ? (int) offsetY : (int) -offsetY), 0);
	}

	private int[] getCoordinates(WorldTile tile) {
		int dirX = tile.getX() > 2440 ? EAST : WEST;
		int dirY = tile.getY() > 3162 ? NORTH : SOUTH;
		int x = dirX == EAST ? (tile.getX() - 2440) : (2440 - tile.getX());
		int y = dirY == NORTH ? (tile.getY() - 3162) : (3162 - tile.getY());
		int minX = (int) (x * 1.875);
		int minY = (int) (y * 1.875);
		int degreeX = minX / 60;
		int degreeY = minY / 60;
		minX -= degreeX * 60;
		minY -= degreeY * 60;
		return new int[] { degreeY, minY, dirY, degreeX, minX, dirX };
	}

	private static int[] createMix() {
		int[] mix = new int[25];
		for (int i = 0; i < mix.length; i++)
			mix[i] = i - 1;
		shuffle(mix);
		// http://www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html
		int inversions = 0;
		for (int i = 0; i < mix.length - 1; i++) {
			if (mix[i] == -1)
				continue;
			for (int j = i; j < mix.length; j++)
				if (mix[j] > mix[i])
					inversions++;
		}
		// If the grid width is odd, then the number of inversions in a solvable
		// situation is even.
		if ((inversions & 1) != 0)
			return createMix(); // could just simply swap last 2 pieces but need
		// to make sure they arn't blank
		return mix;
	}

	private static void shuffle(int[] mix) {
		for (int i = mix.length - 1; i > 0; i--) {
			int index = Utils.random(i + 1);
			int tmp = mix[index];
			mix[index] = mix[i];
			mix[i] = tmp;
		}
	}

	private static final int COORDINATE = 0, EMOTE = 1, MAP = 2, SIMPLE = 3, ANAGRAM = 5, UNUSED = -1;
	private static final int TILE = 0, OBJECT = 1, NPC = 2;
	private static final int NORTH = 0, SOUTH = 1, WEST = 2, EAST = 3;

	public static final String[] UGIS_QUOTES = { "Always bring a banana to a party.", "Once, I was a poor man, but then I found a party hat.", "There were three goblins in a bar, which one left first?",
			"Would you like to buy a pewter spoon?", "In the end, only the three-legged survive.", "I heard that the tall man fears only strong winds.", "In Canifis the men are known for eating much spam.",
			"I am the egg man, are you one of the egg men?", "The sudden appearance of a deaf squirrel is most puzzling, Comrade.", "I believe that it is very rainy in Varrock.", "The slowest of fishermen catch the swiftest of fish.",
			"It is quite easy being green.", "Don't forget to find the jade monkey.", };

	public static final String UGI_BADREQS = "I do not believe we have any business, Comrade";

	public static enum ClueDetails {
		SIMPLE_A_1(EASY, SIMPLE, OBJECT, 34585, "A crate found in the tower of", "a church is your next location."),
		SIMPLE_D_1(MEDIUM, SIMPLE, OBJECT, 24911, "A town with a different sort of", "night-life is your destination.", "Search for some crates", "in one of the houses."),
		SIMPLE_G_1(EASY, SIMPLE, TILE, 40275377, "Dig near some giant mushrooms behind", "the Grand Tree."),
		SIMPLE_L_1(EASY, SIMPLE, OBJECT, 37011, "Go to the village being", "attacked by trolls, search the", "drawers in one of the houses."),
		SIMPLE_N_1(MEDIUM, SIMPLE, OBJECT, 10159, "North of the best monkey restaurant on Karamja,", "look for the centre of the triangle of boats and", "search there."),
		SIMPLE_O_1(EASY, SIMPLE, NPC, 376, "One of the sailors in Port Sarim is your", "next destination."),
		SIMPLE_S_1(EASY, SIMPLE, OBJECT, 66875, "Search a barrel near the Combat.", "Skill SlayerMasterD at the combat", "training area, in Burthorpe."),
		SIMPLE_S_2(EASY, SIMPLE, OBJECT, 66875, "Search a barrel outside the Pick", "and Lute inn, in Taverley."),
		SIMPLE_S_3(EASY, SIMPLE, OBJECT, 66875, "Search a barrel outside the mill,", "in Taverley."),
		SIMPLE_S_4(EASY, SIMPLE, OBJECT, 40093, "Search chests found in the", "upstairs of shops in Port Sarim."),
		SIMPLE_S_5(EASY, SIMPLE, OBJECT, 48998, "Search for a crate in a building in", "Hemenster."),
		SIMPLE_S_6(EASY, SIMPLE, OBJECT, 46266, "Search for a crate in Varrock", "Palace."),
		SIMPLE_S_7(EASY, SIMPLE, OBJECT, 25775, "Search for a crate on the ground", "floor of a house in Seers' Village."),
		SIMPLE_S_8(EASY, SIMPLE, OBJECT, 11745, "Search in the basement of the", "Artisan Dwarves' workshop in", "Falador."),
		SIMPLE_S_9(EASY, SIMPLE, OBJECT, 46239, "Search the boxes in one of the", "tents in Al Kharid."),
		SIMPLE_S_10(EASY, SIMPLE, OBJECT, 46237, "Search the boxes in the Goblin", "house near Lumbridge."),
		SIMPLE_S_11(EASY, SIMPLE, OBJECT, 46236, "Search the boxes in the house", "near the South entrance of", "Varrock."),
		SIMPLE_S_12(EASY, SIMPLE, OBJECT, 34586, "Search the crate just outside", "the Armour shop in east", "Ardougne."),
		SIMPLE_S_13(EASY, SIMPLE, OBJECT, 46238, "Search the crates in the", "northernmost house in Al Kharid."),
		SIMPLE_S_14(EASY, SIMPLE, OBJECT, 11745, "Search the crates of Falador's", "general store."),
		SIMPLE_S_15(EASY, SIMPLE, OBJECT, 37010, "Search the chest in the Duke of", "Lumbridge's bedroom."),
		SIMPLE_S_16(EASY, SIMPLE, OBJECT, 25593, "Search the chest in the left-hand", "tower of Camelot castle."),
		SIMPLE_S_17(EASY, SIMPLE, OBJECT, 30928, "Search the chests in the", "Dwarven Mine."),
		SIMPLE_S_18(EASY, SIMPLE, OBJECT, 35470, "Search the chests in Al", "Kharid palace."),
		SIMPLE_S_19(EASY, SIMPLE, OBJECT, 21806, "Search the crate in the left-hand ", "tower of Lumbridge castle."),
		SIMPLE_S_20(EASY, SIMPLE, OBJECT, 46270, "Search the crate near a cart in", "Port Khazard."),
		SIMPLE_S_21(EASY, SIMPLE, OBJECT, 24202, "Search the crates in a house in", "Yanille that has a piano."),
		SIMPLE_S_23(EASY, SIMPLE, OBJECT, 24911, "Search the crates in Canifis."),
		SIMPLE_S_24(EASY, SIMPLE, OBJECT, 47560, "Search the crates in Draynor", "Manor."),
		SIMPLE_S_25(EASY, SIMPLE, OBJECT, 34585, "Search the crates in East", "Ardougne's general store."),
		SIMPLE_S_26(EASY, SIMPLE, OBJECT, 46269, "Search the crates in Horvik's", "armoury."),
		SIMPLE_S_27(EASY, SIMPLE, OBJECT, 11600, "Search the crates in", "Gunnarsgrunn (Barbarian Village)", "helmet shop."),
		SIMPLE_S_28(EASY, SIMPLE, OBJECT, 34585, "Search the crates in the guard", "house of the northern gate of", "East Ardougne."),
		SIMPLE_S_29(EASY, SIMPLE, OBJECT, 46238, "Search the crates in the", "northernmost house in Al Kharid."),
		SIMPLE_S_30(EASY, SIMPLE, OBJECT, 40021, "Search the crates in the Port", "Sarim fishing shop."),
		SIMPLE_S_31(EASY, SIMPLE, OBJECT, 34586, "Search the crates in the shed", "just north of east Ardougne."),
		SIMPLE_S_32(EASY, SIMPLE, OBJECT, 46269, "Search the crates near a cart in", "Varrock."),
		SIMPLE_S_33(EASY, SIMPLE, OBJECT, 24294, "Search the drawers above", "Varrock's shops."),
		SIMPLE_S_34(EASY, SIMPLE, OBJECT, 34482, "Search the drawers found", "upstairs in the houses of East", "Ardougne."),
		SIMPLE_S_35(EASY, SIMPLE, OBJECT, 2631, "Search the drawers in a house in", "Draynor Village."),
		SIMPLE_S_36(EASY, SIMPLE, OBJECT, 348, "Search the drawers in Falador's", "chainmail shop."),
		SIMPLE_S_37(EASY, SIMPLE, OBJECT, 33931, "Search the drawers in Catherby's", "Archery Shop."),
		SIMPLE_S_38(EASY, SIMPLE, OBJECT, 24294, "Search the drawers in one of", "Gertrude's bedrooms."),
		SIMPLE_S_39(EASY, SIMPLE, OBJECT, 350, "Search the drawers in the ground", "floor of a shop in Yanille."),
		SIMPLE_S_40(EASY, SIMPLE, OBJECT, 33931, "Search the drawers in the", "upstairs of a house in Catherby."),
		SIMPLE_S_41(EASY, SIMPLE, OBJECT, 24294, "Search the drawers upstairs in", "the bank to the East of Varrock."),
		SIMPLE_S_42(EASY, SIMPLE, OBJECT, 37011, "Search the drawers of houses in", "Burthorpe."),
		SIMPLE_S_43(EASY, SIMPLE, OBJECT, 34482, "Search the drawers on the first", "floor of a building overlooking", "Ardougne market."),
		SIMPLE_S_44(EASY, SIMPLE, OBJECT, 348, "Search the drawers upstairs in", "Falador's shield shop."),
		SIMPLE_S_45(EASY, SIMPLE, OBJECT, 11745, "Search the crates upstairs of", "houses in eastern part of", "Falador."),
		SIMPLE_S_47(MEDIUM, SIMPLE, OBJECT, 348, "Search the upstairs drawers of a", "house in a village were pirates", "are known to have a good time."),
		SIMPLE_S_48(EASY, SIMPLE, OBJECT, 375, "Search through chests found in", "the upstairs of houses in eastern", "Falador."),
		SIMPLE_S_50(EASY, SIMPLE, OBJECT, 71943, "Search through some drawers in ", "the upstairs of a house in", "Rimmington."),
		SIMPLE_S_51(EASY, SIMPLE, OBJECT, 25766, "Search upstairs in the houses of", "Seers' Village for some drawers."),
		SIMPLE_S_52(EASY, SIMPLE, NPC, 969, "Someone watching the fights in", "the duel arena is your next", "destination."),
		SIMPLE_S_53(MEDIUM, SIMPLE, NPC, 635, "Speak to a referee."),
		SIMPLE_S_54(EASY, SIMPLE, NPC, 563, "Speak to Arhein in Catherby."),
		SIMPLE_S_55(MEDIUM, SIMPLE, NPC, 171, "Speak to Brimstail."),
		SIMPLE_S_56(EASY, SIMPLE, NPC, 806, "Speak to Donovan the Family", "Handyman."),
		SIMPLE_S_57(EASY, SIMPLE, NPC, 284, "Speak to Doric who lives north of", "Falador."),
		SIMPLE_S_58(EASY, SIMPLE, NPC, 2824, "Speak to Ellis in Al Kharid."),
		SIMPLE_S_59(EASY, SIMPLE, NPC, 586, "Speak to Gaius in Burthorpe."),
		SIMPLE_S_60(MEDIUM, SIMPLE, NPC, 510, "Speak to Hajedy."),
		SIMPLE_S_61(EASY, SIMPLE, NPC, 0, "Speak to Hans."),
		SIMPLE_S_62(MEDIUM, SIMPLE, NPC, 669, "Speak to Hazelmere."),
		SIMPLE_S_63(EASY, SIMPLE, NPC, 918, "Speak to Ned in Draynor Village."),
		SIMPLE_S_64(EASY, SIMPLE, NPC, 1042, "Speak to Roavar."),
		SIMPLE_S_65(EASY, SIMPLE, NPC, 241, "Speak to Sir Kay in Camelot", "Castle."),
		SIMPLE_S_66(EASY, SIMPLE, NPC, 733, "Speak to the bartender of the", "Blue Moon Inn in Varrock."),
		SIMPLE_S_67(EASY, SIMPLE, NPC, 809, "Speak to the staff of Sinclair", "Mansion."),
		SIMPLE_S_68(EASY, SIMPLE, NPC, 1054, "Speak to Ulizius."),
		SIMPLE_T_1(EASY, SIMPLE, NPC, 734, "Talk to the bartender of the Rusty", "Anchor in Port Sarim."),
		SIMPLE_T_2(EASY, SIMPLE, NPC, 606, "Talk to the Squire in the White", "Knights' castle in Falador."),
		SIMPLE_T_3(EASY, SIMPLE, NPC, 541, "Talk to Zeke in Al Kharid."),
		SIMPLE_T_4(MEDIUM, SIMPLE, TILE, 54988211, "The treasure is buried in a small", "building full of bones. Here is a hint: ", "it's not near a graveyard."),
		COORDINATE_00_1(HARD, COORDINATE, TILE, UNUSED, 0, 0, NORTH, 7, 13, WEST),
		COORDINATE_00_2(MEDIUM, COORDINATE, TILE, UNUSED, 0, 5, SOUTH, 1, 13, EAST),
		COORDINATE_00_3(MEDIUM, COORDINATE, TILE, UNUSED, 0, 13, SOUTH, 13, 58, EAST),
		COORDINATE_00_4(MEDIUM, COORDINATE, TILE, UNUSED, 0, 18, SOUTH, 9, 28, EAST),
		COORDINATE_00_5(MEDIUM, COORDINATE, TILE, UNUSED, 0, 20, SOUTH, 23, 15, EAST),
		COORDINATE_00_6(MEDIUM, COORDINATE, TILE, UNUSED, 0, 30, NORTH, 24, 16, EAST),
		COORDINATE_00_7(MEDIUM, COORDINATE, TILE, UNUSED, 0, 31, SOUTH, 17, 43, EAST),
		COORDINATE_00_8(MEDIUM, COORDINATE, TILE, UNUSED, 1, 18, SOUTH, 14, 15, EAST),
		COORDINATE_00_9(MEDIUM, COORDINATE, TILE, UNUSED, 1, 24, NORTH, 8, 5, WEST),
		COORDINATE_00_10(MEDIUM, COORDINATE, TILE, UNUSED, 1, 26, NORTH, 8, 1, EAST),
		COORDINATE_00_11(MEDIUM, COORDINATE, TILE, UNUSED, 1, 35, SOUTH, 7, 28, EAST),
		COORDINATE_00_12(MEDIUM, COORDINATE, TILE, UNUSED, 2, 46, NORTH, 29, 11, EAST),
		COORDINATE_00_13(MEDIUM, COORDINATE, TILE, UNUSED, 2, 48, NORTH, 22, 30, EAST),
		COORDINATE_00_14(MEDIUM, COORDINATE, TILE, UNUSED, 2, 50, NORTH, 6, 20, EAST),
		COORDINATE_00_15(MEDIUM, COORDINATE, TILE, UNUSED, 3, 35, SOUTH, 13, 35, EAST),
		COORDINATE_00_16(HARD, COORDINATE, TILE, UNUSED, 3, 45, SOUTH, 22, 45, EAST),
		COORDINATE_00_17(MEDIUM, COORDINATE, TILE, UNUSED, 4, 0, SOUTH, 12, 46, EAST),
		COORDINATE_00_18(HARD, COORDINATE, TILE, UNUSED, 4, 3, SOUTH, 3, 11, EAST),
		COORDINATE_00_19(HARD, COORDINATE, TILE, UNUSED, 4, 5, SOUTH, 4, 24, EAST),
		COORDINATE_00_20(MEDIUM, COORDINATE, TILE, UNUSED, 4, 13, NORTH, 12, 45, EAST),
		COORDINATE_00_21(HARD, COORDINATE, TILE, UNUSED, 4, 16, SOUTH, 16, 16, EAST),
		COORDINATE_00_22(HARD, COORDINATE, TILE, UNUSED, 4, 41, NORTH, 3, 9, WEST),
		COORDINATE_00_23(MEDIUM, COORDINATE, TILE, UNUSED, 5, 20, SOUTH, 4, 28, EAST),
		COORDINATE_00_24(HARD, COORDINATE, TILE, UNUSED, 5, 37, NORTH, 31, 15, EAST),
		COORDINATE_00_25(MEDIUM, COORDINATE, TILE, UNUSED, 5, 43, NORTH, 23, 5, EAST),
		COORDINATE_00_26(HARD, COORDINATE, TILE, UNUSED, 5, 50, SOUTH, 10, 5, EAST),
		COORDINATE_00_27(HARD, COORDINATE, TILE, UNUSED, 6, 0, SOUTH, 21, 48, EAST),
		COORDINATE_00_28(HARD, COORDINATE, TILE, UNUSED, 6, 11, SOUTH, 15, 7, EAST),
		COORDINATE_00_29(MEDIUM, COORDINATE, TILE, UNUSED, 6, 31, NORTH, 1, 46, WEST),
		COORDINATE_00_30(MEDIUM, COORDINATE, TILE, UNUSED, 7, 5, NORTH, 30, 56, EAST),
		COORDINATE_00_31(MEDIUM, COORDINATE, TILE, UNUSED, 7, 33, NORTH, 15, 0, EAST),
		COORDINATE_00_32(HARD, COORDINATE, TILE, UNUSED, 7, 43, SOUTH, 12, 26, EAST),
		COORDINATE_00_33(HARD, COORDINATE, TILE, UNUSED, 8, 3, NORTH, 31, 16, EAST),
		COORDINATE_00_34(HARD, COORDINATE, TILE, UNUSED, 8, 5, SOUTH, 15, 56, EAST),
		COORDINATE_00_35(HARD, COORDINATE, TILE, UNUSED, 8, 26, SOUTH, 10, 28, EAST),
		COORDINATE_00_36(MEDIUM, COORDINATE, TILE, UNUSED, 8, 33, NORTH, 1, 39, WEST),
		COORDINATE_00_37(MEDIUM, COORDINATE, TILE, UNUSED, 9, 33, NORTH, 2, 15, EAST),
		COORDINATE_00_38(MEDIUM, COORDINATE, TILE, UNUSED, 9, 48, NORTH, 17, 39, EAST),
		COORDINATE_00_39(MEDIUM, COORDINATE, TILE, UNUSED, 11, 3, NORTH, 31, 20, EAST),
		COORDINATE_00_40(MEDIUM, COORDINATE, TILE, UNUSED, 11, 5, NORTH, 0, 45, WEST),
		COORDINATE_00_41(MEDIUM, COORDINATE, TILE, UNUSED, 11, 41, NORTH, 14, 58, EAST),
		COORDINATE_00_42(HARD, COORDINATE, TILE, UNUSED, 12, 48, NORTH, 20, 20, EAST),
		COORDINATE_00_43(HARD, COORDINATE, TILE, UNUSED, 13, 46, NORTH, 21, 1, EAST),
		COORDINATE_00_44(MEDIUM, COORDINATE, TILE, UNUSED, 14, 54, NORTH, 9, 13, EAST),
		COORDINATE_00_45(HARD, COORDINATE, TILE, UNUSED, 15, 48, NORTH, 13, 52, EAST),
		COORDINATE_00_46(HARD, COORDINATE, TILE, UNUSED, 16, 3, NORTH, 14, 7, EAST),
		COORDINATE_00_47(HARD, COORDINATE, TILE, UNUSED, 16, 20, NORTH, 12, 45, EAST),
		COORDINATE_00_48(HARD, COORDINATE, TILE, UNUSED, 16, 35, NORTH, 27, 1, EAST),
		COORDINATE_00_49(HARD, COORDINATE, TILE, UNUSED, 16, 43, NORTH, 19, 11, EAST),
		COORDINATE_00_50(HARD, COORDINATE, TILE, UNUSED, 17, 50, NORTH, 8, 30, EAST),
		COORDINATE_00_51(HARD, COORDINATE, TILE, UNUSED, 18, 3, NORTH, 25, 16, EAST),
		COORDINATE_00_52(HARD, COORDINATE, TILE, UNUSED, 18, 22, NORTH, 16, 33, EAST),
		COORDINATE_00_53(HARD, COORDINATE, TILE, UNUSED, 19, 43, NORTH, 25, 7, EAST),
		COORDINATE_00_54(HARD, COORDINATE, TILE, UNUSED, 20, 5, NORTH, 21, 52, EAST),
		COORDINATE_00_55(HARD, COORDINATE, TILE, UNUSED, 20, 7, NORTH, 18, 33, EAST),
		COORDINATE_00_56(HARD, COORDINATE, TILE, UNUSED, 20, 33, NORTH, 15, 48, EAST),
		COORDINATE_00_57(HARD, COORDINATE, TILE, UNUSED, 21, 24, NORTH, 17, 54, EAST),
		COORDINATE_00_58(MEDIUM, COORDINATE, TILE, UNUSED, 22, 30, NORTH, 3, 1, EAST),
		COORDINATE_00_59(HARD, COORDINATE, TILE, UNUSED, 22, 35, NORTH, 19, 18, EAST),
		COORDINATE_00_60(HARD, COORDINATE, TILE, UNUSED, 22, 45, NORTH, 26, 33, EAST),
		COORDINATE_00_61(HARD, COORDINATE, TILE, UNUSED, 24, 24, NORTH, 26, 24, EAST),
		COORDINATE_00_62(HARD, COORDINATE, TILE, UNUSED, 24, 56, NORTH, 22, 28, EAST),
		COORDINATE_00_63(HARD, COORDINATE, TILE, UNUSED, 24, 58, NORTH, 18, 43, EAST),
		COORDINATE_00_64(HARD, COORDINATE, TILE, UNUSED, 25, 3, NORTH, 17, 5, EAST),
		COORDINATE_00_65(HARD, COORDINATE, TILE, UNUSED, 25, 3, NORTH, 23, 24, EAST),
		ANAGRAM_A_1(HARD, ANAGRAM, NPC, 589, "A Zen ", "She"),
		ANAGRAM_A_2(HARD, ANAGRAM, NPC, 2812, "Ace", "Match", "Elm"),
		ANAGRAM_A_3(MEDIUM, ANAGRAM, NPC, 962, "Aha Jar"),
		ANAGRAM_A_4(HARD, ANAGRAM, NPC, 4594, "An Paint", "Tonic"),
		ANAGRAM_A_5(MEDIUM, ANAGRAM, NPC, 696, "Arc O", "Line"),
		ANAGRAM_A_6(HARD, ANAGRAM, NPC, 746, "Are Col")
		// ,
		// ANAGRAM_A_7(MEDIUM,
		// ANAGRAM,
		// NPC,
		// 3827,
		// "Arr!
		// So
		// I",
		// "am
		// a",
		// "crust",
		// "and?")
		,
		ANAGRAM_B_1(MEDIUM, ANAGRAM, NPC, 171, "Bail Trims"),
		ANAGRAM_B_2(HARD, ANAGRAM, NPC, 471, "By Look"),
		ANAGRAM_B_3(MEDIUM, ANAGRAM, NPC, new int[] { 15095, 15097 }, "Boast B"),
		ANAGRAM_C_1(HARD, ANAGRAM, NPC, 2802, "C on game", "hoc"),
		ANAGRAM_D_1(HARD, ANAGRAM, NPC, 1294, "Dt Run B"),
		ANAGRAM_E_1(HARD, ANAGRAM, NPC, 28, "Eek Zero Op"),
		ANAGRAM_E_2(MEDIUM, ANAGRAM, NPC, 550, "El Ow"),
		ANAGRAM_E_3(HARD, ANAGRAM, NPC, 720, "Err Cure It"),
		ANAGRAM_G_1(MEDIUM, ANAGRAM, NPC, 469, "Goblin Kern"),
		ANAGRAM_G_2(MEDIUM, ANAGRAM, NPC, 2520, "Got A Boy"),
		ANAGRAM_G_3(HARD, ANAGRAM, NPC, 2039, "Gulag Run"),
		ANAGRAM_H_1(MEDIUM, ANAGRAM, NPC, 379, "Halt Us"),
		ANAGRAM_H_2(HARD, ANAGRAM, NPC, 2142, "He Do Pose. It is", "Cultrrl, Mk?"),
		ANAGRAM_I_1(MEDIUM, ANAGRAM, NPC, 1011, "Icy Fe"),
		ANAGRAM_I_2(HARD, ANAGRAM, NPC, 3044, "I Eat Its", "Chart", "Hints Do", "U"),
		ANAGRAM_I_3(HARD, ANAGRAM, NPC, 4431, "I Faffy", "Run"),
		ANAGRAM_L_1(HARD, ANAGRAM, NPC, 9173, "Land Doomd"),
		ANAGRAM_L_2(HARD, ANAGRAM, NPC, 648, "Lark In Dog"),
		ANAGRAM_M_1(HARD, ANAGRAM, NPC, 2812, "Me Am The", "Calc"),
		ANAGRAM_M_2(MEDIUM, ANAGRAM, NPC, 676, "Me if"),
		ANAGRAM_N_1(MEDIUM, ANAGRAM, NPC, 714, "Nod med"),
		ANAGRAM_O_1(MEDIUM, ANAGRAM, NPC, 437, "O Birdz A Zany", "En Pc"),
		ANAGRAM_O_2(HARD, ANAGRAM, NPC, 278, "Ok Co"),
		ANAGRAM_O_3(HARD, ANAGRAM, NPC, 460, "Or Zinc Fumes", "Ward"),
		ANAGRAM_P_1(MEDIUM, ANAGRAM, NPC, 659, "Peaty Pert"),
		ANAGRAM_P_2(HARD, ANAGRAM, NPC, 4585, "Profs Lose Wrong Pie"),
		ANAGRAM_R_1(HARD, ANAGRAM, NPC, 543, "R Ak Mi"),
		ANAGRAM_R_2(HARD, ANAGRAM, NPC, 4650, "Red Art Tans"),
		ANAGRAM_S_1(HARD, ANAGRAM, NPC, 1359, "Sequin", "Dirge"),
		ANAGRAM_S_2(MEDIUM, ANAGRAM, NPC, 0, "Hans"),
		ANAGRAM_S_3(MEDIUM, ANAGRAM, NPC, new int[] { 15095, 15097 }, "Stab", "Ob"),
		ANAGRAM_T_1(HARD, ANAGRAM, NPC, 2812, "Them Cal Came"),
		MAP_1(EASY, MAP, OBJECT, 46331, 361),
		MAP_2(EASY, MAP, OBJECT, 24202, 355),
		MAP_3(HARD, MAP, OBJECT, 46331, 359),
		MAP_4(EASY, MAP, OBJECT, 18506, 358),
		MAP_5(EASY, MAP, OBJECT, 2620, 350),
		MAP_6(EASY, MAP, TILE, 40209568, 342),
		MAP_7(EASY, MAP, TILE, 50941008, 356),
		MAP_8(EASY, MAP, TILE, 42798490, 354),
		MAP_9(EASY, MAP, TILE, 41520921, 340),
		MAP_10(EASY, MAP, TILE, 40783082, 357),
		MAP_11(EASY, MAP, TILE, 42863621, 353),
		MAP_12(EASY, MAP, TILE, 43437216, 360),
		MAP_13(EASY, MAP, TILE, 44354915, 349),
		MAP_14(EASY, MAP, TILE, 49859911, 351),
		MAP_15(EASY, MAP, TILE, 51907877, 346),
		MAP_16(EASY, MAP, TILE, 48729430, 337),
		MAP_17(EASY, MAP, TILE, 53890349, 347),
		MAP_18(EASY, MAP, TILE, 43797996, 344),
		MAP_19(EASY, MAP, TILE, 47615201, 352),
		MAP_20(EASY, MAP, TILE, 44633354, 339),
		MAP_21(HARD, MAP, TILE, 49499975, 338),
		MAP_22(EASY, MAP, TILE, 42241549, 343),
		MAP_23(EASY, MAP, TILE, 50711705, 348),
		MAP_24(EASY, MAP, TILE, 56282305, 341),
		MAP_25(EASY, MAP, TILE, 47893642, 362),
		EMOTE_B_1(
				MEDIUM,
				EMOTE,
				TILE,
				45829116,
				new Emote[] { Emote.BECKON, Emote.CLAP },
				new Item[] { new Item(20922, 1), new Item(1099, 1), new Item(1143, 1) },
				"Beckon in Tai Bwo Wannai.",
				"Clap before you talk to me.",
				" Equip green dragonhide chaps,",
				"a ring of duelling, and a mithril med helm."),
		EMOTE_B_2(
				MEDIUM,
				EMOTE,
				TILE,
				55184739,
				new Emote[] { Emote.BECKON, Emote.BOW },
				new Item[] { new Item(3329, 1), new Item(6328, 1), new Item(6328, 1) },
				"Beckon in the Digsite, near",
				"the eastern winch.",
				" Bow or curtsy before you talk to me.",
				"Equip a pointed red and black snelm,",
				"snakeskin boots, and an iron pickaxe."),
		EMOTE_B_3(
				HARD,
				EMOTE,
				TILE,
				46730121,
				new Emote[] { Emote.BLOW_KISS },
				new Item[] { new Item(3385, 1), new Item(7170, 1), new Item(1127, 1) },
				"Blow a kiss between the tables",
				"in Shilo Village bank.",
				"Beware of double agents!",
				"Equip a splitbark helmet, mud pie",
				"and rune platebody."),
		EMOTE_B_4(
				EASY,
				EMOTE,
				TILE,
				42732751,
				new Emote[] { Emote.RASPBERRY },
				new Item[] { new Item(1133, 1), new Item(1075, 1), new Item(7170, 1) },
				"Blow a raspberry at the monkey",
				"cage in Ardougne Zoo.",
				"Equip a studded body,",
				"bronze platelegs and a mud pie."),
		EMOTE_B_5(
				HARD,
				EMOTE,
				TILE,
				42372446,
				new Emote[] { Emote.RASPBERRY },
				new Item[] { new Item(2890, 1), new Item(2493, 1), new Item(1347, 1) },
				"Blow a raspberry in",
				"the Fishing Guild bank.",
				"Beware of double agents!",
				"Equip an elemental shield, blue ",
				"dragonhide chaps, and a rune warhammer."),
		EMOTE_B_6(
				EASY,
				EMOTE,
				TILE,
				45272394,
				new Emote[] { Emote.RASPBERRY },
				new Item[] { new Item(1169, 1), new Item(1115, 1), new Item(1059, 1) },
				"Blow raspberries outside",
				"the entrance to Keep Le Faye.",
				"Equip a coif, an iron platebody",
				"and leather gloves."),
		EMOTE_B_7(
				HARD,
				EMOTE,
				TILE,
				577982012,
				new Emote[] { Emote.BOW },
				new Item[] { new Item(2499, 1), new Item(2487, 1) },
				"Bow or curtsy at the",
				"top of the lighthouse.",
				"Beware of double agents!",
				"Equip a blue dragonhide body,",
				"blue dragonhide vambraces",
				"and no jewellery."),
		EMOTE_B_8(EASY, EMOTE, TILE, 55200971, new Emote[] { Emote.BOW }, new Item[] { new Item(1101, 1), new Item(1095, 1), new Item(1169, 1) }, "Bow or curtsy in the lobby", "of the Duel Arena.", "Equip an iron chainbody,", "leather chaps and a coif."),
		EMOTE_B_9(
				EASY,
				EMOTE,
				TILE,
				44715285,
				new Emote[] { Emote.BOW },
				new Item[] { new Item(1067, 1), new Item(1696, 1), new Item(845, 1) },
				"Bow or curtsy outside",
				"the entrance to the Legends' Guild.",
				"Equip iron platelegs,",
				"an emerald amulet and oak longbow."),
		EMOTE_C_1(EASY, EMOTE, TILE, 36164446, new Emote[] { Emote.CHEER }, new Item[] {}, "Cheer at the Burthrope Games Room.", "Have nothing equipped at all when you do."),
		EMOTE_C_2(EASY, EMOTE, TILE, 47648172, new Emote[] { Emote.CHEER }, new Item[] { new Item(5527, 1), new Item(1307, 1), new Item(1692, 1) }, "Cheer at the druids circle,", "equip an Air tiara, a bronze 2h sword,", "and Gold amulet."),
		EMOTE_C_3(EASY, EMOTE, TILE, 49941668, new Emote[] { Emote.CHEER }, new Item[] { new Item(1169, 1), new Item(1083, 1), new Item(1656, 1) }, "Cheer for the monks at Port Sarim.", "Equip a coif, steel plateskirt", "and a sapphire necklace."),
		EMOTE_C_4(
				MEDIUM,
				EMOTE,
				TILE,
				41799140,
				new Emote[] { Emote.CHEER, Emote.HEADBANG },
				new Item[] { new Item(1119, 1), new Item(853, 1), new Item(4119, 1) },
				"Cheer in the Barbarian Agility Arena.",
				"Headbang before you talk to me.",
				"Equip a steel platebody, maple, ",
				"shortbow and bronze boots."),
		EMOTE_C_5(
				MEDIUM,
				EMOTE,
				TILE,
				41422128,
				new Emote[] { Emote.CHEER, Emote.ANGRY },
				new Item[] { new Item(1135, 1), new Item(1099, 1), new Item(1177, 1) },
				"Cheer in the Ogre pen",
				"in the Training Camp.",
				"Show you are angry before you talk",
				"to me. Equip a Green Dragonhide Body, ",
				"green dragonhide chaps",
				"and a steel sq shield."),
		EMOTE_C_6(EASY, EMOTE, TILE, 55086348, new Emote[] { Emote.CLAP }, new Item[] { new Item(1698, 1), new Item(2464, 1), new Item(1059, 1) }, "Clap in the main exam room", "in the Exam Centre.", "Equip a ruby amulet, blue flowers", "and leather gloves."),
		EMOTE_C_7(EASY, EMOTE, TILE, 51022956, new Emote[] { Emote.CLAP }, new Item[] { new Item(1137, 1), new Item(1639, 1), new Item(1059, 1) }, "Clap on the causeway to", "the Wizard's Tower.", " Equip an Iron helm, emerald ring", "and leather gloves."),
		EMOTE_C_8(
				EASY,
				EMOTE,
				TILE,
				579980602,
				new Emote[] { Emote.CLAP },
				new Item[] { new Item(1639, 1), new Item(1119, 1), new Item(5525, 1) },
				"Clap on the top level of the mill,",
				"north of east Ardougne.",
				"Equip an Emerald ring,",
				"Steel platebody and an uncharged tiara."),
		EMOTE_C_9(
				MEDIUM,
				EMOTE,
				TILE,
				46255474,
				new Emote[] { Emote.CRY, Emote.BOW },
				new Item[] { new Item(3329, 1), new Item(1131, 1), new Item(2961, 1) },
				"Cry in Catherby archery shop.",
				"Bow or curtsy before you talk to me.",
				"Equip a round red and black snelm,",
				"a hardleather body and an unblessed",
				"silver sickle."),
		EMOTE_C_10(
				MEDIUM,
				EMOTE,
				TILE,
				577424732,
				new Emote[] { Emote.CRY, Emote.NO },
				new Item[] { new Item(1099, 1), new Item(1193, 1), new Item(2568, 1) },
				"Cry on the platform of",
				"the south-west tree of the",
				"Gnome Agility Arena. Indicate 'no'",
				"before talking to me,",
				"equip green d'hide chaps, a steel",
				"kiteshield, and a ring of forging."),
		EMOTE_D_1(
				MEDIUM,
				EMOTE,
				TILE,
				54152242,
				new Emote[] { Emote.JIG, Emote.BOW },
				new Item[] { new Item(3333, 1), new Item(1381, 1) },
				"Dance a jig under Shantay's Awning.",
				"Bow or curtsy before you talk to me.",
				"Equip a pointed blue snail",
				"helmet and an air staff."),
		EMOTE_D_2(
				HARD,
				EMOTE,
				TILE,
				53971677,
				new Emote[] { Emote.DANCE },
				new Item[] { new Item(2570, 1), new Item(1704, 1), new Item(1317, 1) },
				"Dance at the cat-doored",
				"pyramid in Sophanem.",
				"Beware of double agents!",
				"Equip a ring of life,",
				"an uncharged amulet of glory,",
				"and an adamant 2h sword."),
		EMOTE_D_3(EASY, EMOTE, TILE, 50957536, new Emote[] { Emote.DANCE }, new Item[] { new Item(1101, 1), new Item(1637, 1), new Item(839, 1) }, "Dance at the crossroads", "north of Draynor,", "equip an iron chainbody,", "a sapphire ring and a longbow. "),
		EMOTE_D_4(
				MEDIUM,
				EMOTE,
				TILE,
				57281956,
				new Emote[] { Emote.DANCE, Emote.BOW },
				new Item[] { new Item(4551, 1), new Item(1071, 1), new Item(1309, 1) },
				"Dance in the centre of Canifis.",
				"Bow or curtsy before you talk to me.",
				"Equip a spiny helm,",
				"mithril platelegs, and an",
				"iron two-handed sword."),
		EMOTE_D_5(EASY, EMOTE, TILE, 49892657, new Emote[] { Emote.DANCE }, new Item[] { new Item(1157, 1), new Item(1119, 1), new Item(1081, 1) }, "Dance in the Party Room.", "Equip a steel full helm,", "steel platebody and, ", "iron plateskirt."),
		EMOTE_D_6(EASY, EMOTE, TILE, 52481120, new Emote[] { Emote.DANCE }, new Item[] { new Item(1205, 1), new Item(1153, 1), new Item(1635, 1) }, "Dance in the shack", "in Lumbridge Swamp.", "Equip a bronze dagger,", "iron full helm and", "a gold ring."),
		EMOTE_H_1(EASY, EMOTE, TILE, 54086900, new Emote[] { Emote.HEADBANG }, new Item[] { new Item(1059, 1), new Item(1061, 1) }, "Headbang in the mine", "north of Al-kharid.", "Equip Leather Gloves", "and Leather Boots."),
		EMOTE_J_1(
				EASY,
				EMOTE,
				TILE,
				42831162,
				new Emote[] { Emote.JIG },
				new Item[] { new Item(1694, 1), new Item(1639, 1), new Item(1103, 1) },
				"Dance a jig by the",
				"entrance to the Fishing Guild,",
				"equip a sapphire amulet,",
				"an emerald ring ",
				"and a bronze chainbody."),
		EMOTE_J_2(
				MEDIUM,
				EMOTE,
				TILE,
				54152242,
				new Emote[] { Emote.JIG, Emote.BOW },
				new Item[] { new Item(3333, 1), new Item(1381, 1) },
				"Dance a jig",
				"under Shantay's Awning.",
				"Bow or curtsy before you talk to me.",
				"Equip a pointed blue",
				"snail helmet, air staff."),
		EMOTE_J_3(EASY, EMOTE, TILE, 45206898, new Emote[] { Emote.JUMP_FOR_JOY }, new Item[] { new Item(4121, 1), new Item(1724, 1), new Item(1353, 1) }, "Jump for joy", "at the beehives.", "Equip iron boots, unholy symbol,", "and a steel hatchet."),
		EMOTE_J_4(
				MEDIUM,
				EMOTE,
				TILE,
				42781716,
				new Emote[] { Emote.JUMP_FOR_JOY, Emote.JIG },
				new Item[] { new Item(9177, 1), new Item(1145, 1), new Item(6324, 1) },
				"Jump for joy in Yanille Bank.",
				"Dance a jig before you talk to me.",
				"Equip an iron crossbow,",
				"adamant medium helmet",
				"and snakeskin chaps."),
		EMOTE_L_1(
				EASY,
				EMOTE,
				TILE,
				44912080,
				new Emote[] { Emote.LAUGH },
				new Item[] { new Item(1167, 1), new Item(1725, 1), new Item(1323, 1) },
				"Laugh at the crossroads",
				"south of Sinclair Mansion.",
				"Equip a leather cowl,",
				"amulet of strength",
				"and iron scimitar."),
		EMOTE_L_2(
				HARD,
				EMOTE,
				TILE,
				45780567,
				new Emote[] { Emote.LAUGH },
				new Item[] { new Item(1163, 1), new Item(2493, 1), new Item(1393, 1) },
				"Laugh in the Jokul's tent",
				"in the mountain camp.",
				"Beware of double agents!",
				"Equip a rune full helm, blue",
				"d'hide chaps and a fire battlestaff."),
		EMOTE_P_1(HARD, EMOTE, TILE, 59231649, new Emote[] { Emote.PANIC }, new Item[] {}, "Panic in the heart of", "the Haunted Woods.", "Beware of double agents!", "Have no items equipped when you do."),
		EMOTE_P_2(EASY, EMOTE, TILE, 55250348, new Emote[] { Emote.PANIC }, new Item[] { new Item(1075, 1), new Item(1269, 1), new Item(1141, 1) }, "Panic in the limestone mine.", "Equip bronze platelegs,", "steel pickaxe", "and steel helm."),
		EMOTE_P_3(
				HARD,
				EMOTE,
				TILE,
				46615979,
				new Emote[] { Emote.PANIC },
				new Item[] { new Item(1071, 1), new Item(2570, 1), new Item(1359, 1) },
				"Panic by the pilot",
				"on White Wolf Mountain.",
				"Beware of double agents!",
				"Equip mithril platelegs,",
				"a ring of life,",
				"and a rune hatchet."),
		EMOTE_P_4(EASY, EMOTE, TILE, 43846754, new Emote[] { Emote.PANIC }, new Item[] {}, "Panic on the pier where you", "catch the Fishing Trawler.", "Have nothing equipped at all", "when you do.")
		// ,
		// EMOTE_P_5(EASY,
		// EMOTE,
		// TILE,57413112,
		// new
		// int[]
		// {20,
		// 7},
		// new
		// Item[]
		// {new
		// Item(1085,
		// 1),
		// new
		// Item(851,
		// 1)},
		// "Panic
		// by
		// the
		// mausoleum",
		// "in
		// Morytania.
		// ",
		// "
		// Wave
		// before
		// you
		// speak
		// to
		// me.",
		// "Equip
		// a
		// mithril
		// plateskirt,",
		// "a
		// maple
		// longbow
		// and
		// no
		// boots.")
		,
		EMOTE_S_1(
				HARD,
				EMOTE,
				TILE,
				47844442,
				new Emote[] { Emote.SALUTE },
				new Item[] { new Item(1643, 1), new Item(1731, 1) },
				"Salute in the banana plantation.",
				"Beware of double agents!",
				"Equip a diamond ring,",
				"amulet of power",
				"and nothing on your chest and legs."),
		EMOTE_S_2(EASY, EMOTE, TILE, 48745637, new Emote[] { Emote.SHRUG }, new Item[] { new Item(1654, 1), new Item(1635, 1), new Item(1237, 1) }, "Shrug in the mine near Rimmington,", "Equip a gold necklace,", "a gold ring and a bronze spear."),
		EMOTE_S_3(
				HARD,
				EMOTE,
				TILE,
				53087770,
				new Emote[] { Emote.SHRUG },
				new Item[] { new Item(1075, 1), new Item(1115, 1), new Item(2487, 1) },
				"Shrug in the Zamorak temple",
				"found in the Eastern Wilderness.",
				"Beware of double agents!",
				"Equip bronze platelegs,",
				"an iron platebody,",
				"and blue d'hide vambraces."),
		EMOTE_T_1(EASY, EMOTE, TILE, 51776739, new Emote[] { Emote.THINK }, new Item[] { new Item(1656, 1), new Item(843, 1) }, "Think in the middle of the", "wheat field by the Lumbridge mill.", "Equip a sapphire necklace", "and an oak shortbow. ")
		// ,
		// EMOTE_T_2(MEDIUM,EMOTE,
		// TILE,
		// -1,
		// new
		// int[]
		// {6,16},
		// new
		// Item[]
		// {new
		// Item(1109,1),
		// new
		// Item(1099,1),
		// new
		// Item(1698,1)},
		// "Think
		// under
		// the
		// lens
		// in
		// the
		// Observatory.",
		// "Twirl
		// before
		// you
		// talk
		// to
		// me.",
		// "Equip
		// a
		// mithril
		// chainbody,",
		// "green
		// dragonhide
		// chaps",
		// "and
		// ruby
		// amulet.
		// ")
		,
		EMOTE_T_3(
				EASY,
				EMOTE,
				TILE,
				48794823,
				new Emote[] { Emote.TWIRL },
				new Item[] { new Item(1637, 1), new Item(2466, 1), new Item(1095, 1) },
				"Twirl at the crossroads",
				"north of Rimmington.",
				"Equip a Sapphire ring,",
				"yellow flowers",
				"and leather chaps."),
		EMOTE_T_4(
				EASY,
				EMOTE,
				TILE,
				50629897,
				new Emote[] { Emote.TWIRL },
				new Item[] { new Item(1115, 1), new Item(1097, 1), new Item(1155, 1) },
				"Twirl in Draynor Manor",
				"by the fountain.",
				"Equip an iron platebody,",
				"studded chaps",
				"and a bronze full helm. "),
		EMOTE_T_5(
				MEDIUM,
				EMOTE,
				TILE,
				50892125,
				new Emote[] { Emote.TWIRL, Emote.SALUTE },
				new Item[] { new Item(1349, 1), new Item(1193, 1), new Item(1159, 1) },
				"Twirl on the bridge",
				"by Gunnarsgrunn (or Barbarian Village).",
				"Salute before you talk to me.",
				"Equip an iron hatchet,",
				"steel kiteshield",
				"and mithril full helm."),
		EMOTE_W_1(EASY, EMOTE, TILE, 49138733, new Emote[] { Emote.WAVE }, new Item[] { new Item(1635, 1), new Item(1095, 1), new Item(1424, 1) }, "Wave on Mudskipper Point.", "Equip a gold ring,", "leather chaps and a steel mace. "),
		EMOTE_W_2(
				EASY,
				EMOTE,
				TILE,
				54168995,
				new Emote[] { Emote.WAVE },
				new Item[] { new Item(1095, 1), new Item(1351, 1), new Item(1131, 1) },
				"Wave along the south",
				"fence of the lumberyard.",
				"Equip leather chaps,",
				"bronze hatchet",
				"and a hardleather body. "),
		EMOTE_Y_1(
				MEDIUM,
				EMOTE,
				TILE,
				40012822,
				new Emote[] { Emote.YAWN, Emote.SHRUG },
				new Item[] { new Item(1698, 1), new Item(1329, 1), new Item(1175, 1) },
				"Yawn in the Castle Wars lobby.",
				"Shrug before you talk to me.",
				"Equip a ruby amulet,",
				"mithril scimitar",
				"and an iron sq shield. "),
		EMOTE_Y_2(EASY, EMOTE, TILE, 50482355, new Emote[] { Emote.YAWN }, new Item[] { new Item(1191, 1), new Item(1295, 1), new Item(1097, 1) }, "Yawn in Draynor Marketplace.", "Equip an iron kiteshield,", "steel longsword", "and studded leather chaps."),
		EMOTE_Y_3(EASY, EMOTE, TILE, 52596135, new Emote[] { Emote.YAWN }, new Item[] { new Item(1718, 1), new Item(1063, 1), new Item(1335, 1) }, "Yawn in Varrock Palace library.", "Equip a holy symbol,", "leather vambraces", "and an iron warhammer."),
		EMOTE_Y_4(
				HARD,
				EMOTE,
				TILE,
				49581685,
				new Emote[] { Emote.YAWN },
				new Item[] { new Item(1175, 1), new Item(2487, 1), new Item(1267, 1) },
				"Yawn in the rogues' general store.",
				"Beware of double agents!",
				"Equip an iron square shield,",
				"blue dragonhide vambraces,",
				"and an iron pickaxe.")

		;

		public int level, type, idType;
		public int[] ids;
		public Object[] parameters;

		private ClueDetails(int level, int type, int idType, int[] ids, Object... parameters) {
			this.level = level;
			this.type = type;
			this.idType = idType;
			this.ids = ids;
			this.parameters = parameters;
		}

		private ClueDetails(int level, int type, int idType, int id, Object... parameters) {
			this.level = level;
			this.type = type;
			this.idType = idType;
			ids = new int[] { id };
			this.parameters = parameters;
		}

		public boolean isId(int id) {
			for (int i : ids)
				if (id == i)
					return true;
			return false;
		}

		public int getId() {
			return ids[0];
		}
	}

	private static class Clue {

		private ClueDetails details;
		private int count;
		private int dificulty;

		public Clue(ClueDetails details, int count, int dificulty) {
			this.details = details;
			this.count = count;
			this.dificulty = dificulty;
		}

		private boolean isLast() {
			return count == 0;
		}
	}
}
