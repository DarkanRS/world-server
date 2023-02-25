package com.rs.game.content.skills.runecrafting.runespan;

import com.rs.game.World;
import com.rs.game.content.skills.runecrafting.Runecrafting;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class SiphonAction extends PlayerAction {
	private Creature creatures;
	private NPC creature;
	private boolean started;
	private int npcLife;

	public SiphonAction(Creature creatures, NPC creature) {
		this.creature = creature;
		this.creatures = creatures;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			npcLife = creatures.npcLife;
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	public boolean checkAll(final Player player) {
		if (player.isLocked() || creature.hasFinished())
			return false;
		if (player.getSkills().getLevel(Constants.RUNECRAFTING) < creatures.levelRequired) {
			player.simpleDialogue("This creature requires level " + creatures.levelRequired + " to siphon.");
			return false;
		}
		if (!creatures.rune.isPureEss() && !player.getInventory().containsOneItem(Runecrafting.PURE_ESS, Runecrafting.RUNE_ESS)) {
			player.sendMessage("You don't have any rune essence to siphon from that creature.");
			return false;
		}
		if (creatures.rune.isPureEss() && !player.getInventory().containsItem(Runecrafting.PURE_ESS)) {
			player.sendMessage("You don't have any pure essence to siphon from that creature.");
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
	public int processWithDelay(Player player) {
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
			creature.setNextAnimation(new Animation(creatures.npcEmoteId));
			creature.setNextFaceTile(player.getTile());
			creature.freeze(4);
			player.setNextFaceTile(creature.getTile());
			WorldProjectile p = World.sendProjectile(creature, player, 3060, 31, 40, 35, 1, 2, 0);
			boolean finalSuccess = success;
			WorldTasks.schedule(Utils.clampI(p.getTaskDelay()-1, 0, 100), () -> player.setNextSpotAnim(new SpotAnim(finalSuccess ? 3062 : 3071)));
		}
		return 1;
	}

	@Override
	public void stop(Player player) {

	}

	public void processEsslingDeath(final Player player) {
		creature.setNextAnimation(new Animation(creatures.deathEmote));
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
