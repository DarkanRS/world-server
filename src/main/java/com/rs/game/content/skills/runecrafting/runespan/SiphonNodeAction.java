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
package com.rs.game.content.skills.runecrafting.runespan;

import java.util.HashMap;
import java.util.Map;
import java.lang.SuppressWarnings;

import com.rs.game.World;
import com.rs.game.content.skills.runecrafting.Runecrafting;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class SiphonNodeAction extends PlayerAction {
	Node nodes;
	GameObject node;
	private boolean started;

	public SiphonNodeAction(Node nodes, GameObject node) {
		this.nodes = nodes;
		this.node = node;
	}

	public enum Node {
		CYCLONE(70455, 16596, 19, 1, 24215),
		MIND_STORM(70456, 16596, 20, 1, 24217),
		WATER_POOL(70457, 16596, 25.3, 5, 24214),
		ROCK_FRAGMENT(70458, 16596, 28.6, 9, 24216),
		FIRE_BALL(70459, 16596, 34.8, 14, 24213),
		VINE(70460, 16596, 34.8, 17, 24214, 24216),
		FLESHLY_GROWTH(70461, 16596, Utils.random(30.3, 34.3), 20, 24218),
		FIRE_STORM(70462, 16596, Utils.random(22.8, 41.7), 27, 24213, 24215),
		CHAOTIC_CLOUD(70463, 16596, 61.6, 35, 24221),
		NEBULA(70464, 16596, Utils.random(63.8, 85.6), 40, 24223, 24224),
		SHIFTER(70465, 16596, 86.8, 44, 24220),
		JUMPER(70466, 16596, 107.8, 54, 24222),
		SKULLS(70467, 16596, 120, 65, 24219),
		BLOOD_POOL(70468, 16596, 146.3, 77, 24225),
		BLOODY_SKULLS(70469, 16596, Utils.random(144, 175.5), 83, 24219, 24225),
		LIVING_SOUL(70470, 16596, 213, 90, 24226),
		UNDEAD_SOUL(70471, 16596, Utils.random(144, 255.5), 95, 24219, 24226);

		private static Map<Integer, Node> MAP = new HashMap<>();

		static {
			for (Node n : Node.values())
				MAP.put(n.objectId, n);
		}

		public static Node forId(int objectId) {
			return MAP.get(objectId);
		}

		private int objectId;
		private int emoteId;
		private double xp;
		private int levelRequired;
		private int[] runeId;

		Node(int objectId, int emoteId, double xp, int levelRequired, int... runeId) {
			this.objectId = objectId;
			this.emoteId = emoteId;
			this.xp = xp;
			this.levelRequired = levelRequired;
			this.runeId = runeId;
		}

		public int getObjectId() {
			return objectId;
		}

		public void setObjectId(int objectId) {
			this.objectId = objectId;
		}

		public int getEmoteId() {
			return emoteId;
		}

		public void setEmoteId(int emoteId) {
			this.emoteId = emoteId;
		}

		public int[] getRuneId() {
			return runeId;
		}

		public double getXp() {
			return xp;
		}

		public void setXp(int xp) {
			this.xp = xp;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public void setLevelRequired(int levelRequired) {
			this.levelRequired = levelRequired;
		}
	}

	public static ObjectClickHandler handleNodes = new ObjectClickHandler(false, Node.MAP.keySet().toArray()) {
		@Override
		public void handle(ObjectClickEvent e) {
			//			Nodes node = getNode(e.getObjectId());
			//			if (node == null)
			//				return;
			//			e.getPlayer().getActionManager().setAction(new SiphonNodeAction(node, e.getObject()));
		}
	};

	@SuppressWarnings("unused")
	private static int getRandomTransformationId() {
		return getNode(Utils.getRandomInclusive(Node.values().length)).getObjectId();
	}

	private static Node getNode(int id) {
		for (Node node : Node.values())
			if (node.objectId == id)
				return node;
		return null;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player))
			return true;
		return false;
	}

	public boolean checkAll(final Player player) {
		if (player.getSkills().getLevel(Constants.RUNECRAFTING) < nodes.getLevelRequired()) {
			player.simpleDialogue("You need a runecrafting level of " + nodes.getLevelRequired() + " to siphon from that node.");
			return false;
		}
		if (!started && !player.withinDistance(node.getTile(), 6))
			return true;
		if (!World.getRegion(player.getRegionId()).objectExists(node)) {
			stop(player);
			return false;
		}
		//		if ((!creatures.rune.isPureEss() && !player.getInventory().containsOneItem(Runecrafting.PURE_ESS, Runecrafting.RUNE_ESS)) || (creatures.rune.isPureEss() && !player.getInventory().containsItem(Runecrafting.PURE_ESS))) {
		//			player.sendMessage("You don't have any rune essence to siphon from that creature.");
		//			return false;
		//		}
		if (!started) {
			player.resetWalkSteps();
			player.setNextAnimation(new Animation(16596));
			started = true;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@SuppressWarnings("unused")
	private void processNodeDestroy(final Player player) {
		player.sendMessage("The node you were siphoning from has been depleted of energy.", true);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(16599));
				World.removeObject(node);
				stop();
			}
		}, 2);
	}

	@Override
	public int processWithDelay(final Player player) {
		if (started) {
			boolean success = false;
			if (Utils.getRandomInclusive(4) == 0) {
				success = true;
				//Runecrafting.runecraft(player, nodes.randomRune(), true);
				double totalXp = nodes.getXp();
				if (Runecrafting.hasRcingSuit(player))
					totalXp *= 1.025;
				player.getSkills().addXp(Constants.RUNECRAFTING, totalXp);
			}
			player.setNextAnimation(new Animation(nodes.getEmoteId()));
			player.setNextFaceWorldTile(node.getTile());
			WorldProjectile p = World.sendProjectile(node, player, 3060, 31, 40, 35, 1, 2, 0);
			final boolean succF = success;
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextSpotAnim(new SpotAnim(succF ? 3062 : 3071));
				}
			}, Utils.clampI(p.getTaskDelay()-1, 0, 100));
		}
		return 1;
	}

	@Override
	public void stop(Player player) {
		player.setNextAnimation(new Animation(16599));
		setActionDelay(player, 3);

	}

}
