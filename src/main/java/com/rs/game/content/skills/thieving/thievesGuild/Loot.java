package com.rs.game.content.skills.thieving.thievesGuild;

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class Loot extends PlayerAction {

	private final NPC npc;

	private boolean success = false;

	public Loot(NPC npc) {
		this.npc = npc;
	}
	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			success = successful(player);
			player.sendMessage("You search the " + npc.getDefinitions().getName().toLowerCase() + "'s pocket...");
			WorldTasks.delay(0, () -> player.setNextAnimation(new Animation(881)));
			setActionDelay(player, 2);
			player.lock();
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (!success) {
			player.sendMessage("You find nothing in the " + npc.getDefinitions().getName().toLowerCase() + "'s pocket.");
		}
		else {
			double totalXp = calculateExperience(player);
			player.incrementCount(npc.getDefinitions().getName() + " pickpocketed");
			player.getSkills().addXp(Constants.THIEVING, totalXp);
			if (player.getWeeklyI("HankyPoints") < HankyPoints.maxPoints(player) - 4) {
				player.sendMessage("You empty the man's pocket and drape the red handkerchief over him.");
				player.simpleDialogue("You gain 4 Hanky Points.");
				player.incWeeklyI("HankyPoints", 4);
			}
			else
				player.sendMessage("You have earned the maximum number of Hanky Points this week.");
			stop(player);
		}
		return -1;
	}

	public static double calculateExperience(Player player) {
		int playerLevel = player.getSkills().getLevel(Skills.THIEVING);
		double experience = 0.5 * playerLevel + 26;
		return experience;
	}

	@Override
	public void stop(Player player) {
		player.unlock();
		player.setNextFaceEntity(null);
		setActionDelay(player, 1);
	}

	public boolean rollSuccess(Player player) {
		return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul() + (hasArdyCloak(player) ? 0.1 : 0.0), 185, 255);
	}

	private boolean successful(Player player) {
		if (!rollSuccess(player))
			return false;
		return true;
	}

	private boolean checkAll(Player player) {
		if (player.isDead() || player.hasFinished() || player.hasPendingHits())
			return false;
		if (player.getAttackedBy() != null && player.inCombat()) {
			player.sendMessage("You can't do this while you're under combat.");
			return false;
		}
		return true;
	}
	public static boolean hasArdyCloak(Player player) {
        return switch (player.getEquipment().getCapeId()) {
            case 15349, 19748, 9777, 9778 -> true;
            default -> false;
        };
	}
}
