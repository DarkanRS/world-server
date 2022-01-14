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
package com.rs.game.player.controllers;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.npc.others.BarrowsBrother;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.AchievementDef.Area;
import com.rs.game.player.content.achievements.AchievementDef.Difficulty;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.minigames.barrows.BarrowsPath;
import com.rs.game.player.content.minigames.barrows.BarrowsPuzzle;
import com.rs.game.player.content.minigames.barrows.Link;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.file.FileManager;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

public final class BarrowsController extends Controller {

	private transient BarrowsBrother target;
	private transient BarrowsPuzzle puzzle;

	private boolean solvedPuzzle;
	public int removeDarkness;
	public int[] varBits = new int[20];

	private static enum Hills {
		AHRIM_HILL(new WorldTile(3564, 3287, 0), new WorldTile(3557, 9703, 3)),
		DHAROK_HILL(new WorldTile(3573, 3296, 0), new WorldTile(3556, 9718, 3)),
		GUTHAN_HILL(new WorldTile(3574, 3279, 0), new WorldTile(3534, 9704, 3)),
		KARIL_HILL(new WorldTile(3563, 3276, 0), new WorldTile(3546, 9684, 3)),
		TORAG_HILL(new WorldTile(3553, 3281, 0), new WorldTile(3568, 9683, 3)),
		VERAC_HILL(new WorldTile(3556, 3296, 0), new WorldTile(3578, 9706, 3));

		private WorldTile outBound;
		private WorldTile inside;

		private Hills(WorldTile outBound, WorldTile in) {
			this.outBound = outBound;
			inside = in;
		}
	}

	public static boolean digIntoGrave(final Player player) {
		for (Hills hill : Hills.values())
			if (player.getPlane() == hill.outBound.getPlane() && player.getX() >= hill.outBound.getX() && player.getY() >= hill.outBound.getY() && player.getX() <= hill.outBound.getX() + 3 && player.getY() <= hill.outBound.getY() + 3) {
				player.useStairs(-1, hill.inside, 1, 2, "You've broken into a crypt.");
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.getControllerManager().startController(new BarrowsController());
					}
				});
				return true;
			}
		return false;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof BarrowsBrother && target != this.target) {
			player.sendMessage("This isn't your target.");
			return false;
		}
		return true;
	}

	private void exit(WorldTile outside) {
		player.setNextWorldTile(outside);
		leave(false);
	}

	private void leave(boolean logout) {
		if (target != null)
			target.finish(); // target also calls removing hint icon at remove
		if (!logout) {
			player.getPackets().setBlockMinimapState(0); // unblacks minimap
			if (player.getHiddenBrother() == -1)
				player.getPackets().sendStopCameraShake();
			else
				player.getInterfaceManager().removeOverlay();
			removeController();
		}
	}

	@Override
	public boolean sendDeath() {
		leave(false);
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		leave(false);
	}

	public int getRandomBrother() {
		List<Integer> bros = new ArrayList<>();
		for (int i = 0; i < Hills.values().length; i++) {
			if (player.getKilledBarrowBrothers()[i] || player.getHiddenBrother() == i)
				continue;
			bros.add(i);
		}
		if (bros.isEmpty())
			return -1;
		return bros.get(Utils.random(bros.size()));
	}

	private static final Drop[] AHRIM = { new Drop(4708, 1), new Drop(4710, 1), new Drop(4712, 1), new Drop(4714, 1) };
	private static final Drop[] DHAROK = { new Drop(4716, 1), new Drop(4718, 1), new Drop(4720, 1), new Drop(4722, 1) };
	private static final Drop[] GUTHAN = { new Drop(4724, 1), new Drop(4726, 1), new Drop(4728, 1), new Drop(4730, 1) };
	private static final Drop[] KARIL = { new Drop(4732, 1), new Drop(4734, 1), new Drop(4736, 1), new Drop(4738, 1) };
	private static final Drop[] TORAG = { new Drop(4745, 1), new Drop(4747, 1), new Drop(4749, 1), new Drop(4751, 1) };
	private static final Drop[] VERAC = { new Drop(4753, 1), new Drop(4755, 1), new Drop(4757, 1), new Drop(4759, 1) };
	private static final Drop[] AKRISAE = { new Drop(21736, 1), new Drop(21744, 1), new Drop(21752, 1), new Drop(21760, 1) };

	private static final Drop[][] BROTHERS_LOOT = { AHRIM, DHAROK, GUTHAN, KARIL, TORAG, VERAC, AKRISAE };

	public void drop(Item... items) {
		if (items == null || items.length <= 0)
			return;
		for (Item item : items) {
			if (NPC.yellDrop(item.getId())) {
				World.sendWorldMessage("<img=4><shad=000000><col=00FF00>" + player.getDisplayName() + " has just recieved " + item.getName() + " as drop from Barrows!", false);
				FileManager.writeToFile("droplog.txt", player.getDisplayName() + " has just recieved a " + item.getName() + " drop from Barrows!");
			}
			player.getInventory().addItem(item.getId(), item.getAmount(), true);
			player.incrementCount(ItemDefinitions.getDefs(item.getId()).getName()+" drops earned", item.getAmount());
		}
	}

	public static Item[] getSimulatedDrop(int brothersKilled, int points) {
		ItemsContainer<Item> container = new ItemsContainer<>(points, false);
		if (points > 1012)
			points = 1012;
		List<Drop> equipment = new ArrayList<>();
		for (int i = 0;i < BROTHERS_LOOT.length-1;i++)
			for (Drop d : BROTHERS_LOOT[i])
				equipment.add(d);
		int rolls = Utils.clampI(brothersKilled+1, 1, 7);
		int equipmentChance = Utils.clampI(450 - (58 * brothersKilled), 73, 450);
		for (int i = 0;i < rolls;i++) {
			if (rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(1, equipmentChance, equipment)))) || (points >= 381 && rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(125, 1012, new Drop(558, 253, 336)))))))
				continue;
			if (points >= 506 && rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(125, 1012, new Drop(562, 112, 139))))))
				continue;
			if (points >= 631 && rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(125, 1012, new Drop(560, 70, 83))))))
				continue;
			if (points >= 756 && rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(125, 1012, new Drop(565, 37, 43))))))
				continue;
			if (points >= 881 && rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(125, 1012, new Drop(4740, 35, 40))))))
				continue;
			if (points >= 1006 && rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(6, 1012, new Drop(985, 1), new Drop(987, 1))))))
				continue;
			if (points >= 1012 && rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(6, 1012, new Drop(1149, 1))))))
				continue;
			rollAdd(container, DropTable.calculateDrops(new DropSet(new DropTable(995, 1, points))));
		}
		for (Item i : container.getItems()) {
			if (i == null)
				continue;
			if (i.getId() >= 554 && i.getId() <= 566)
				i.setAmount(i.getAmount() * 2);
		}
		return container.getItemsNoNull();
	}

	private static boolean rollAdd(ItemsContainer<Item> container, Item[] drops) {
		if (drops.length <= 0)
			return false;
		container.addAll(drops);
		return true;
	}

	public Item[] getRewards() {
		int points = 0;
		int brothersKilled = 0;
		ItemsContainer<Item> container = new ItemsContainer<>(20, false);
		List<Drop> equipment = new ArrayList<>();
		for (int i = 0;i < player.getKilledBarrowBrothers().length;i++)
			if (player.getKilledBarrowBrothers()[i]) {
				for (Drop d : BROTHERS_LOOT[i])
					equipment.add(d);
				brothersKilled++;
				points += 110;
			}
		points += player.getBarrowsKillCount() * 73;
		if (points > 1012)
			points = 1012;
		int rolls = Utils.clampI(brothersKilled+1, 1, 7);
		int equipmentChance = Utils.clampI(450 - (58 * brothersKilled), 73, 450);
		for (int i = 0;i < rolls;i++) {
			if ((player.getKilledBarrowBrothers()[i] && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(1, equipmentChance, equipment))))) || (points >= 381 && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(125, 1012, new Drop(558, 253, 336)))))))
				continue;
			if (points >= 506 && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(125, 1012, new Drop(562, 112, 139))))))
				continue;
			if (points >= 631 && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(125, 1012, new Drop(560, 70, 83))))))
				continue;
			if (points >= 756 && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(125, 1012, new Drop(565, 37, 43))))))
				continue;
			if (points >= 881 && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(125, 1012, new Drop(4740, 35, 40))))))
				continue;
			if (points >= 1006 && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(6, 1012, new Drop(985, 1), new Drop(987, 1))))))
				continue;
			if (points >= 1012 && rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(6, 1012, new Drop(1149, 1))))))
				continue;
			rollAdd(container, DropTable.calculateDrops(player, new DropSet(new DropTable(995, 1, points))));
		}
		if (SetReward.MORYTANIA_LEGS.hasRequirements(player, Area.MORYTANIA, Difficulty.HARD, false))
			for (Item i : container.getItems()) {
				if (i == null)
					continue;
				if (i.getId() >= 554 && i.getId() <= 566)
					i.setAmount(i.getAmount() * 2);
			}
		player.sendMessage("Your drop potential this chest was " + Utils.formatNumber(points) + "/1,012.");
		return container.getItemsNoNull();
	}

	public void sendReward() {
		Item[] rewards = getRewards();
		player.getInterfaceManager().sendInterface(1171);
		player.getPackets().sendInterSetItemsOptionsScript(1171, 7, 100, 8, 3, "Examine");
		player.getPackets().setIFRightClickOps(1171, 7, 0, 10, 0, 1, 2, 3);
		player.getPackets().sendItems(100, rewards);
		drop(rewards);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == 25) {
			if (puzzle == null)
				return false;
			if (puzzle.isCorrect(componentId)) {
				solvedPuzzle = true;
				player.sendMessage("You hear the mechanisms in the door click as they unlock.");
				player.closeInterfaces();
			} else {
				player.sendMessage("That was incorrect! You hear a rumbling around you.");
				player.closeInterfaces();
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() >= 6702 && object.getId() <= 6707) {
			WorldTile out = Hills.values()[object.getId() - 6702].outBound;
			exit(new WorldTile(out.getX() + 1, out.getY() + 1, out.getPlane()));
			return false;
		}
		if (object.getId() >= 6709 && object.getId() <= 6712) {
			player.useLadder(new WorldTile(3565, 3288, 0));
			leave(false);
		} else if (object.getId() == 10284) {
			if (player.getHiddenBrother() == -1) {
				player.sendMessage("You found nothing.");
				return false;
			}
			if (!player.getKilledBarrowBrothers()[player.getHiddenBrother()])
				sendTarget(2025 + player.getHiddenBrother(), new WorldTile(player));
			if (object.getDefinitions(player).getOption(1).equals("Search")) {
				player.incrementCount("Barrows chests looted");
				sendReward();
				player.getPackets().sendCameraShake(3, 12, 25, 12, 25);
				player.getInterfaceManager().removeOverlay();
				player.resetBarrows();
				player.getVars().setVarBit(1394, 0);
			} else
				player.getVars().setVarBit(1394, 1);
			return false;
		} else if (object.getId() >= 6716 && object.getId() <= 6750) {

			switch(object.getId()) {
			case 6720:
			case 6724:
			case 6725:
			case 6727:
			case 6739:
			case 6743:
			case 6744:
			case 6746:
				if (!solvedPuzzle) {
					puzzle = new BarrowsPuzzle().display(player);
					return false;
				}
				break;
			default:
				break;
			}

			Doors.handleDoubleDoor(player, object);
			removeDarkness = (removeDarkness == 1 ? 0 : 1);
			player.getVars().setVar(1270, removeDarkness);
			if (Utils.random(10) == 0)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if (player.getHiddenBrother() != -1) {
							int brother = getRandomBrother();
							if (brother != -1)
								sendTarget(2025 + brother, new WorldTile(player));
						}
					}
				}, 0);
			return false;
		} else {
			int sarcoId = getSarcophagusId(object.getId());
			if (sarcoId != -1) {
				if (sarcoId == player.getHiddenBrother())
					player.startConversation(new Dialogue()
							.addSimple("You've found a hidden tunnel, do you want to enter?")
							.addOption("Select an Option", "Yes, I'm fearless.", "No way, that looks scary!")
							.addNext(() -> {
								int spawn = Utils.random(4);
								ArrayList<Link> currentPath = BarrowsPath.generateBarrowsPath(spawn);
								BarrowsPath.updatePathDoors(player, currentPath);
								BarrowsPath.setSpawn(player, spawn);
							}).finish());
				else if (target != null || player.getKilledBarrowBrothers()[sarcoId])
					player.sendMessage("You found nothing.");
				else
					sendTarget(2025 + sarcoId, player);
				return false;
			}
		}
		return true;
	}

	public int getSarcophagusId(int objectId) {
		switch (objectId) {
		case 66017:
			return 0;
		case 63177:
			return 1;
		case 66020:
			return 2;
		case 66018:
			return 3;
		case 66019:
			return 4;
		case 66016:
			return 5;
		default:
			return -1;
		}
	}

	public void targetDied() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		setBrotherSlained(target.getId() < 14297 ? target.getId() - 2025 : 6);
		target = null;

	}

	public void cheat() {
		setBrotherSlained(0);
		setBrotherSlained(1);
		setBrotherSlained(2);
		setBrotherSlained(3);
		setBrotherSlained(4);
		setBrotherSlained(5);
		player.setBarrowsKillCount(6);
		player.sendNPCKill("Ahrim the Blighted");
		player.sendNPCKill("Dharok the Wretched");
		player.sendNPCKill("Guthan the Infested");
		player.sendNPCKill("Karil the Tainted");
		player.sendNPCKill("Torag the Corrupted");
		player.sendNPCKill("Verac the Defiled");
		player.incrementCount("Barrows chests looted");
		sendReward();
		player.resetBarrows();
		player.getVars().setVarBit(1394, 0);
	}

	public void targetFinishedWithoutDie() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		target = null;
	}

	public void setBrotherSlained(int index) {
		player.getKilledBarrowBrothers()[index] = true;
		sendBrotherSlain(index, true);
	}

	public void sendTarget(int id, WorldTile tile) {
		if (target != null)
			target.disappear();
		target = new BarrowsBrother(id, tile, this);
		target.setForceMultiArea(true);
		target.setTarget(player);
		target.setNextForceTalk(new ForceTalk("You dare disturb my rest!"));
		player.getHintIconsManager().addHintIcon(target, 1, -1, false);
	}

	public BarrowsController() {

	}

	private int headComponentId;
	private int timer;

	public int getAndIncreaseHeadIndex() {
		int head = player.getTempAttribs().removeI("BarrowsHead");
		if (head == -1 || head == player.getKilledBarrowBrothers().length - 1)
			head = 0;
		player.getTempAttribs().setI("BarrowsHead", head + 1);
		return player.getKilledBarrowBrothers()[head] ? head : -1;
	}

	@Override
	public void process() {
		if (timer > 0) {
			timer--;
			return;
		}
		if (headComponentId == 0) {
			if (player.getHiddenBrother() == -1) {
				player.applyHit(new Hit(player, Utils.random(50) + 1, HitLook.TRUE_DAMAGE));
				resetHeadTimer();
				return;
			}
			int headIndex = getAndIncreaseHeadIndex();
			if (headIndex == -1) {
				resetHeadTimer();
				return;
			}
			headComponentId = 9 + Utils.random(2);
			player.getPackets().setIFItem(24, headComponentId, 4761 + headIndex, 0);
			player.getPackets().setIFAnimation(9810, 24, headComponentId);
			double activeLevel = player.getPrayer().getPoints();
			if (activeLevel > 0) {
				double level = player.getSkills().getLevelForXp(Constants.PRAYER) * 10;
				player.getPrayer().drainPrayer(level / 6.0);
			}
			timer = 3;
		} else {
			player.getPackets().setIFItem(24, headComponentId, -1, 0);
			headComponentId = 0;
			resetHeadTimer();
		}
	}

	public void resetHeadTimer() {
		timer = 20 + Utils.random(6);
	}

	@Override
	public void sendInterfaces() {
		if (player.getHiddenBrother() != -1)
			player.getInterfaceManager().setOverlay(24);
	}

	public void loadData() {
		resetHeadTimer();
		for (int i = 0; i < player.getKilledBarrowBrothers().length; i++)
			sendBrotherSlain(i, player.getKilledBarrowBrothers()[i]);
		sendCreaturesSlainCount(player.getBarrowsKillCount());
		player.getPackets().setBlockMinimapState(2); // blacks minimap
	}

	public void sendBrotherSlain(int index, boolean slain) {
		player.getVars().setVarBit(457 + index, slain ? 1 : 0);
	}

	public void sendCreaturesSlainCount(int count) {
		player.getVars().setVarBit(464, count+player.getKilledBarrowBrothersCount());
	}

	public void savePathVars() {
		removeDarkness = player.getVars().getVar(1270);
		for (int i = 0; i<20; i++)
			varBits[i] = player.getVars().getVarBit(465+i);
	}

	public void loadPathVars() {
		player.getVars().setVar(1270, removeDarkness);
		if (varBits != null && (varBits.length == 20))
			for (int i = 0; i<20; i++)
				player.getVars().setVarBit(465+i, varBits[i]);
	}

	public void resetPathVars() {
		varBits = new int[20];
	}

	@Override
	public void start() {
		if (player.getHiddenBrother() == -1)
			player.setHiddenBrother(Utils.random(Hills.values().length));
		loadData();
		sendInterfaces();
	}

	@Override
	public boolean login() {
		if (player.getHiddenBrother() == -1)
			player.getPackets().sendCameraShake(3, 12, 25, 12, 25);
		loadData();
		sendInterfaces();
		loadPathVars();
		return false;
	}

	@Override
	public boolean logout() {
		leave(true);
		savePathVars();
		return false;
	}

	@Override
	public void forceClose() {
		leave(true);
	}

}
