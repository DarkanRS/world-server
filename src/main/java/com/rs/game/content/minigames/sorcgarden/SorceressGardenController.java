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
package com.rs.game.content.minigames.sorcgarden;

import com.rs.game.content.minigames.sorcgarden.SqirkFruitSqueeze.SqirkFruit;
import com.rs.game.content.skills.herblore.HerbCleaning;
import com.rs.game.content.skills.herblore.HerbCleaning.Herbs;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.transportation.FadingScreen;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SorceressGardenController extends Controller {

	private static final Tile MIDDLE = Tile.of(2916, 5475, 0);

	public enum Gate {

		WINTER(21709, 1, Tile.of(2902, 5470, 0), Tile.of(2903, 5470, 0), 231),
		SPRING(21753, 25, Tile.of(2921, 5473, 0), Tile.of(2920, 5473, 0), 228),
		AUTUMN(21731, 45, Tile.of(2913, 5462, 0), Tile.of(2913, 5463, 0), 229),
		SUMMER(21687, 65, Tile.of(2910, 5481, 0), Tile.of(2910, 5480, 0), 230);

		private int objectId;
		private int levelReq;
		private int musicId;
		private Tile inside, outside;

		private static Map<Integer, Gate> Gates = new HashMap<>();

		private Gate(int objectId, int lvlReq, Tile inside, Tile outside, int musicId) {
			this.objectId = objectId;
			levelReq = lvlReq;
			this.inside = inside;
			this.outside = outside;
			this.musicId = musicId;
		}

		static {
			for (Gate gate : Gate.values())
				Gates.put(gate.getObjectId(), gate);
		}

		/**
		 *
		 * @param player
		 *            the Player
		 * @param objectId
		 *            Object id
		 * @param lvlReq
		 *            Level required for entrance
		 * @param toTile
		 *            Where the player will be spawned
		 */
		public static void handleGates(Player player, int objectId, int lvlReq, Tile toTile, int musicId) {
			if (lvlReq > player.getSkills().getLevelForXp(Constants.THIEVING)) {
				player.simpleDialogue("You need " + lvlReq + " thieving level to pick this gate.");
				return;
			}
			player.useStairs(-1, toTile, 0, 1);
			player.getMusicsManager().playSongAndUnlock(musicId);
		}

		public static Gate forId(int id) {
			return Gates.get(id);
		}

		public int getObjectId() {
			return objectId;
		}

		public int getLeveLReq() {
			return levelReq;
		}

		public Tile getInsideTile() {
			return inside;
		}

		public Tile getOutsideTile() {
			return outside;
		}
	}

	@Override
	public void magicTeleported(int type) {
		removeController();

	}

	public static void teleportToSorceressGardenNPC(NPC npc, final Player player) {
		npc.setNextForceTalk(new ForceTalk("Senventior Disthinte Molesko!"));
		SorceressGardenController.teleportToSocreressGarden(player, false);
	}

	public static void teleportToSocreressGarden(final Player player, boolean broomstick) {
		if (player.getControllerManager().getController() instanceof SorceressGardenController) {
			player.sendMessage("You can't teleport to the Sorceress's Garden whilst you're in the Sorceress's Garden!");
			return;
		}
		boolean teleport;
		if (!broomstick)
			teleport = Magic.sendNormalTeleportSpell(player, 0, 0, MIDDLE);
		else
			teleport = Magic.sendTeleportSpell(player, 10538, 10537, -1, -1, 0, 0, MIDDLE, 4, true, Magic.MAGIC_TELEPORT, null);
		if (teleport)
			WorldTasks.schedule(new Task() {

				@Override
				public void run() {
					player.getControllerManager().startController(new SorceressGardenController());
				}
			}, 4);
	}

	@Override
	public void start() {

	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean sendDeath() {
		removeController();
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 21764) {
			player.lock();
			player.setNextAnimation(new Animation(5796));
			WorldTasks.schedule(new Task() {

				@Override
				public void run() {
					player.unlock();
					Magic.sendNormalTeleportSpell(player, 0, 0, Tile.of(3321, 3141, 0));
				}
			}, 1);
			return false;
		}
		if (object.getId() == 21768) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(SqirkFruit.AUTUMM.getFruitId(), 1);
			player.getSkills().addXp(Constants.FARMING, 50);
			teleMiddle();
		} else if (object.getId() == 21769) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(SqirkFruit.WINTER.getFruitId(), 1);
			player.getSkills().addXp(Constants.FARMING, 30);
			teleMiddle();
			return false;
		} else if (object.getId() == 21766) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(SqirkFruit.SUMMER.getFruitId(), 1);
			player.getSkills().addXp(Constants.FARMING, 60);
			teleMiddle();
			return false;
		} else if (object.getId() == 21767) {
			player.setNextAnimation(new Animation(2280));
			player.getInventory().addItem(SqirkFruit.SPRING.getFruitId(), 1);
			player.getSkills().addXp(Constants.FARMING, 40);
			teleMiddle();
			return false;
		} else if (object.getDefinitions().getName().toLowerCase().contains("gate")) {
			final Gate gate = Gate.forId(object.getId());
			if (gate != null) {
				Gate.handleGates(player, gate.getObjectId(), gate.getLeveLReq(), inGardens(player.getTile()) ? gate.getOutsideTile() : gate.getInsideTile(), gate.musicId);
				return false;
			}
		} else if (object.getDefinitions().getName().toLowerCase().equals("herbs")) {
			player.setNextAnimation(new Animation(827));
			Herbs.values();
			List<Herbs> herbs = HerbCleaning.getHerbs();
			player.getInventory().addItem(herbs.get(Utils.random(herbs.size())).getHerbId(), 1);
			player.getInventory().addItem(herbs.get(Utils.random(herbs.size())).getHerbId(), 1);
			player.getSkills().addXp(Constants.FARMING, SorceressGardenController.inAutumnGarden(player.getTile()) ? 50 : (SorceressGardenController.inSpringGarden(player.getTile()) ? 40 : (SorceressGardenController.inSummerGarden(player.getTile()) ? 60 : 30)));
			teleMiddle();
			return false;
		}
		return true;
	}

	public void teleMiddle() {
		player.lock();
		player.sendMessage("An elemental force enamating from the garden teleports you away.");
		FadingScreen.fade(player, () -> {
			player.setNextTile(Tile.of(2913, 5467, 0));
			player.lock(3);
		});
	}

	/**
	 * Checks if the player is in any garden
	 */
	public static boolean inGarden(Tile tile) {
		return ((tile.getX() >= 2880 && tile.getX() <= 2943) && (tile.getY() >= 5440 && tile.getY() <= 5503));
	}

	public static boolean inGardens(Tile tile) {
		return inWinterGarden(tile) || inAutumnGarden(tile) || inSpringGarden(tile) || inSummerGarden(tile);
	}

	/**
	 * Checks if the player is at Winter Garden or not
	 */
	public static boolean inWinterGarden(Tile tile) {
		return ((tile.getX() >= 2886 && tile.getX() <= 2902) && (tile.getY() >= 5464 && tile.getY() <= 5487));
	}

	/**
	 * Checks if the player is at Spring Garden or not
	 */
	public static boolean inSummerGarden(Tile tile) {
		return ((tile.getX() >= 2904 && tile.getX() <= 2927) && (tile.getY() >= 5481 && tile.getY() <= 5497));
	}

	/**
	 * Checks if the player is at Summer Garden or not
	 */
	public static boolean inSpringGarden(Tile tile) {
		return ((tile.getX() >= 2921 && tile.getX() <= 2937) && (tile.getY() >= 5456 && tile.getY() <= 5479));
	}

	/**
	 * Checks if the player is at Autumn Garden or not
	 */
	public static boolean inAutumnGarden(Tile tile) {
		return ((tile.getX() >= 2896 && tile.getX() <= 2919) && (tile.getY() >= 5446 && tile.getY() <= 5462));
	}

}