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
package com.rs.game.player.content.skills.runecrafting.runespan;

import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.actions.EntityInteractionAction;
import com.rs.game.player.content.skills.runecrafting.Runecrafting;
import com.rs.game.player.content.skills.runecrafting.Runecrafting.RCRune;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.RunespanController;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class SiphonActionCreatures extends EntityInteractionAction {

	private static enum Creature {

		AIR_ESSLING(15403, 9.5, 16596, RCRune.AIR, 16634, 10, 1, 16571, 0.1),
		MIND_ESSLING(15404, 10, 16596, RCRune.MIND, 16634, 10, 1, 16571, 0.2),
		WATER_ESSLING(15405, 12.6, 16596, RCRune.WATER, 16634, 10, 5, 16571, 0.3),
		EARTH_ESSLING(15406, 14.3, 16596, RCRune.EARTH, 16634, 10, 9, 16571, 0.4),
		FIRE_ESSLING(15407, 17.4, 16596, RCRune.FIRE, 16634, 10, 14, 16571, 0.5),
		BODY_ESSHOUND(15408, 23.1, 16596, RCRune.BODY, 16650, 10, 20, 16661, 0.7),
		COSMIC_ESSHOUND(15409, 26.6, 16596, RCRune.COSMIC, 16650, 10, 27, 16661, 0.9),
		CHOAS_ESSHOUND(15410, 30.8, 16596, RCRune.CHAOS, 16650, 10, 35, 16661, 1.1),
		ASTRAL_ESSHOUND(15411, 35.7, 16596, RCRune.ASTRAL, 16650, 10, 40, 16661, 1.3),
		NATURE_ESSHOUND(15412, 43.4, 16596, RCRune.NATURE, 16650, 10, 44, 16661, 1.5),
		LAW_ESSHOUND(15413, 53.9, 16596, RCRune.LAW, 16650, 10, 54, 16661, 1.7),
		DEATH_ESSWRAITH(15414, 60, 16596, RCRune.DEATH, 16644, 10, 65, 16641, 2.5),
		BLOOD_ESSWRAITH(15415, 73.1, 16596, RCRune.BLOOD, 16644, 10, 77, 16641, 3),
		SOUL_ESSWRAITH(15416, 106.5, 16596, RCRune.SOUL, 16644, 10, 90, 16641, 3.5);

		private RCRune rune;
		private int npcId, playerEmoteId, npcEmoteId, npcLife, levelRequired, deathEmote;
		private double pointValue;

		private Creature(int npcId, double xp, int playerEmoteId, RCRune rune, int npcEmoteId, int npcLife, int levelRequired, int deathEmote, double pointValue) {
			this.npcId = npcId;
			//this.xp = xp;
			this.playerEmoteId = playerEmoteId;
			this.rune = rune;
			this.npcEmoteId = npcEmoteId;
			this.npcLife = npcLife;
			this.levelRequired = levelRequired;
			this.deathEmote = deathEmote;
			this.pointValue = pointValue;
		}

		public int getDeathEmote() {
			return deathEmote;
		}

		public int getNpcEmoteId() {
			return npcEmoteId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}
	}

	private Creature creatures;
	private NPC creature;
	private boolean started;
	private int npcLife;

	public SiphonActionCreatures(Creature creatures, NPC creature) {
		super(creature, 7);
		this.creatures = creatures;
		this.creature = creature;
	}

	public static boolean siphon(Player player, NPC npc) {
		Creature creature = getCreature(npc.getId());
		if (creature == null)
			return false;
		player.getActionManager().setAction(new SiphonActionCreatures(creature, npc));
		return true;
	}

	private static Creature getCreature(int id) {
		for (Creature creature : Creature.values())
			if (creature.npcId == id)
				return creature;
		return null;
	}

	@Override
	public boolean canStart(Player player) {
		if (checkAll(player)) {
			npcLife = creatures.npcLife;
			return true;
		}
		return false;
	}

	@Override
	public boolean checkAll(final Player player) {
		if (player.isLocked() || creature.hasFinished())
			return false;
		if (player.getSkills().getLevel(Constants.RUNECRAFTING) < creatures.getLevelRequired()) {
			player.getDialogueManager().execute(new SimpleMessage(), "This creature requires level " + creatures.getLevelRequired() + " to siphon.");
			return false;
		}
		if ((!creatures.rune.isPureEss() && !player.getInventory().containsOneItem(Runecrafting.PURE_ESS, Runecrafting.RUNE_ESS)) || (creatures.rune.isPureEss() && !player.getInventory().containsItem(Runecrafting.PURE_ESS))) {
			player.sendMessage("You don't have any rune essence to siphon from that creature.");
			return false;
		}
		if (!started) {
			creature.resetWalkSteps();
			player.resetWalkSteps();
			player.setNextAnimation(new Animation(creatures.playerEmoteId));
			npcLife = creatures.npcLife;
			started = true;
		}
		return true;
	}

	@Override
	public int loopWithDelay(Player player) {
		if (started) {
			boolean success = false;
			if (Utils.getRandomInclusive(4) <= getWickedWornCount(player)) {
				success = true;
				npcLife--;
				Runecrafting.runecraft(player, creatures.rune, true);
				Controller controller = player.getControllerManager().getController();
				if (controller instanceof RunespanController ctrl)
					ctrl.addRunespanPoints(creatures.pointValue);
			} else
				player.getSkills().addXp(Constants.RUNECRAFTING, 0.5);
			if (npcLife == 0) {
				processEsslingDeath(player);
				return -1;
			}

			player.setNextAnimation(new Animation(16596));
			creature.setNextAnimation(new Animation(creatures.getNpcEmoteId()));
			creature.setNextFaceWorldTile(player);
			creature.freeze(4);
			player.setNextFaceWorldTile(creature);
			WorldProjectile p = World.sendProjectile(creature, player, 3060, 31, 40, 35, 1, 2, 0);
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

	public void processEsslingDeath(final Player player) {
		creature.setNextAnimation(new Animation(creatures.getDeathEmote()));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.sendMessage("The creature has been broken down.");
				player.setNextAnimation(new Animation(16599));
				creature.setRespawnTask(15);
				stop();
			}
		}, 2);
	}

	@Override
	public void onStop(Player player) {
		player.setNextAnimation(new Animation(16599));
		setActionDelay(player, 3);
	}

	public static int getWickedWornCount(Player player) {
		int count = 0;
		if (player.getEquipment().getHatId() == 22332)
			count++;
		if (player.getEquipment().getChestId() == 24206)
			count++;
		if (player.getEquipment().getLegsId() == 24208)
			count++;
		if (player.getEquipment().getCapeId() == 24210)
			count++;
		return count;
	}
}
